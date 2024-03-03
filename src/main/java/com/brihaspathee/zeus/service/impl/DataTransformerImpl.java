package com.brihaspathee.zeus.service.impl;

import com.brihaspathee.zeus.broker.producer.TransactionProducer;
import com.brihaspathee.zeus.dto.account.RawTransactionDto;
import com.brihaspathee.zeus.dto.transaction.TransactionDetailDto;
import com.brihaspathee.zeus.dto.transaction.TransactionDto;
import com.brihaspathee.zeus.dto.transaction.TransactionTradingPartnerDto;
import com.brihaspathee.zeus.edi.models.common.REF;
import com.brihaspathee.zeus.edi.models.enrollment.Loop2000;
import com.brihaspathee.zeus.edi.models.enrollment.Loop2710;
import com.brihaspathee.zeus.edi.models.enrollment.Transaction;
import com.brihaspathee.zeus.helper.interfaces.*;
import com.brihaspathee.zeus.service.interfaces.DataTransformer;
import com.brihaspathee.zeus.test.TestMemberEntityCodes;
import com.brihaspathee.zeus.util.DataTransformerUtil;
import com.brihaspathee.zeus.web.model.DataTransformationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 09, October 2022
 * Time: 7:12 AM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.service.impl
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataTransformerImpl implements DataTransformer {

    /**
     * Transaction detail helper instance
     */
    private final TransactionDetailHelper transactionDetailHelper;

    /**
     * Transaction sponsor helper instance
     */
    private final TransactionSponsorHelper transactionSponsorHelper;

    /**
     * Transaction payer helper instance
     */
    private final TransactionPayerHelper transactionPayerHelper;

    /**
     * Transaction broker helper instance
     */
    private final TransactionBrokerHelper transactionBrokerHelper;

    /**
     * Transaction member helper instance
     */
    private final TransactionMemberHelper transactionMemberHelper;

    /**
     * Transaction producer to send the transformed transaction
     */
    private final TransactionProducer transactionProducer;

    /**
     * Utility class to populate the entity codes on a test environment
     */
    private final DataTransformerUtil dataTransformerUtil;

    /**
     * Payload tracker helper instance
     */
    private final PayloadTrackerHelper payloadTrackerHelper;

    /**
     * Service to transform the raw transaction to transaction dto
     * @param rawTransactionDto
     * @param sendToTransactionManager
     * @return
     * @throws JsonProcessingException
     */
    @Override
    public DataTransformationDto transformTransaction(RawTransactionDto rawTransactionDto,
                                                      boolean sendToTransactionManager) throws JsonProcessingException {
        // get the transaction received date
        LocalDateTime transactionReceivedDate = getTransactionReceivedDate(rawTransactionDto.getTransaction());
        // Construct the data transformation dto objet
        // This also populates the trading partner details of the transaction
        DataTransformationDto dataTransformationDto = constructDataTransformationObject(rawTransactionDto, transactionReceivedDate);
        // build the transaction details
        transactionDetailHelper.buildTransactionDetail(dataTransformationDto, rawTransactionDto);
        // build the sponsor detail received in the transaction
        transactionSponsorHelper.buildSponsor(dataTransformationDto, rawTransactionDto, transactionReceivedDate);
        // Build the payer detail
        transactionPayerHelper.buildTransactionPayer(dataTransformationDto, rawTransactionDto, transactionReceivedDate);
        // Build the broker details
        transactionBrokerHelper.buildTransactionBroker(dataTransformationDto, rawTransactionDto, transactionReceivedDate);
        // Build the member details
        List<TestMemberEntityCodes> testMemberEntityCodes = dataTransformerUtil.
                getMemberEntityCodes(rawTransactionDto.getZeusTransactionControlNumber());
        rawTransactionDto.getTransaction().getMembers().stream().forEach(member -> {
            transactionMemberHelper.buildMemberDetail(dataTransformationDto,
                    testMemberEntityCodes,
                    member, transactionReceivedDate);
        });
        log.info("Data Transformation DTO:{}", dataTransformationDto);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String data = objectMapper.writeValueAsString(dataTransformationDto);
        log.info("Data Transformation DTO as string:{}", data);
        if(sendToTransactionManager){
            transactionProducer.publishTransaction(dataTransformationDto);
        }
        return dataTransformationDto;
    }

    /**
     * Clean up the entire database
     */
    @Override
    public void deleteAll() {
        payloadTrackerHelper.deleteAll();
    }

    /**
     * Construct the data transformation object
     * @param rawTransactionDto
     * @param transactionReceivedDate
     * @return
     */
    private DataTransformationDto constructDataTransformationObject(RawTransactionDto rawTransactionDto,
                                                                    LocalDateTime transactionReceivedDate){
        return DataTransformationDto.builder()
                .transactionDto(TransactionDto.builder()
                        .zfcn(rawTransactionDto.getZfcn())
                        .ztcn(rawTransactionDto.getZtcn())
                        .source(rawTransactionDto.getSource())
                        .entityCodes(dataTransformerUtil.getAccountEntityCodes(rawTransactionDto.getZeusTransactionControlNumber()))
                        .transactionReceivedDate(transactionReceivedDate)
                        .transactionDetail(TransactionDetailDto.builder().build())
                        .tradingPartnerDto(TransactionTradingPartnerDto.builder()
                                .tradingPartnerId(rawTransactionDto.getTradingPartnerId())
                                .lineOfBusinessTypeCode(rawTransactionDto.getLineOfBusinessTypeCode())
                                .marketplaceTypeCode(rawTransactionDto.getMarketplaceTypeCode())
                                .stateTypeCode(rawTransactionDto.getStateTypeCode())
                                .businessTypeCode(rawTransactionDto.getBusinessUnitTypeCode())
                                .build())
                        .members(new ArrayList<>())
                        .build())
                .transformationMessages(new ArrayList<>())
                .build();
    }

    private LocalDateTime getTransactionReceivedDate(Transaction transaction){
        // the BGN segment should be present or else there should have been a 999 error
        String transactionSetDateAsString = transaction.getBeginningSegment().getBgn03();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime transactionSetDate = LocalDate.parse(transactionSetDateAsString, formatter).atStartOfDay();
        log.info("Transaction Set Date received:{}", transactionSetDate);
        Optional<Loop2000> optionalMember = transaction.getMembers().stream()
                .filter(loop2000 -> loop2000.getMemberDetail().getIns01().equals("Y")).findFirst();
        if(optionalMember.isEmpty()){
            optionalMember = transaction.getMembers().stream().findFirst();
        }
        if(optionalMember.isPresent()){
            Loop2000 member = optionalMember.get();
            Optional<Loop2710> optionalRC =  member.getReportingCategories()
                    .getReportingCategories()
                    .stream()
                    .filter(loop2710 ->
                            loop2710.getReportingCategoryDetails()
                                    .getReportingCategory()
                                    .getN102()
                                    .equals("REQUEST SUBMIT TIMESTAMP"))
                    .findFirst();
            if(optionalRC.isPresent()){
                Loop2710 submitTSRC = optionalRC.get();
                Optional<REF> optionalREF = submitTSRC.getReportingCategoryDetails()
                        .getCategoryReference()
                        .stream()
                        .findFirst();
                if(optionalREF.isPresent()){
                    REF tsREF = optionalREF.get();
                    String submitTS = tsREF.getRef02();
                    log.info("Submit Time stamp received:{}", submitTS);
                    submitTS = submitTS.substring(0, 14);
                    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                    LocalDateTime dateTime = LocalDateTime.parse(submitTS, dateFormat);
                    log.info("Submit time stamp in local date format:{}", dateTime);
                    return dateTime;
                }else{

                    return transactionSetDate;
                }
            }else{
                return transactionSetDate;
            }
        }else{
            return transactionSetDate;
        }
    }
}
