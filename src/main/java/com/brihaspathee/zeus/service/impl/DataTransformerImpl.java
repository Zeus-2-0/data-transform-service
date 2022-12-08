package com.brihaspathee.zeus.service.impl;

import com.brihaspathee.zeus.broker.producer.TransactionProducer;
import com.brihaspathee.zeus.dto.account.RawTransactionDto;
import com.brihaspathee.zeus.dto.transaction.TransactionDetailDto;
import com.brihaspathee.zeus.dto.transaction.TransactionDto;
import com.brihaspathee.zeus.dto.transaction.TransactionTradingPartnerDto;
import com.brihaspathee.zeus.helper.interfaces.*;
import com.brihaspathee.zeus.service.interfaces.DataTransformer;
import com.brihaspathee.zeus.web.model.DataTransformationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

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
     * Service to transform the raw transaction to transaction dto
     * @param rawTransactionDto
     * @return
     * @throws JsonProcessingException
     */
    @Override
    public DataTransformationDto transformTransaction(RawTransactionDto rawTransactionDto) throws JsonProcessingException {
        // Construct the data transformation dto objet
        // This also populates the trading partner details of the transaction
        DataTransformationDto dataTransformationDto = constructDataTransformationObject(rawTransactionDto);
        // build the transaction details
        transactionDetailHelper.buildTransactionDetail(dataTransformationDto, rawTransactionDto);
        // build the sponsor detail received in the transaction
        transactionSponsorHelper.buildSponsor(dataTransformationDto, rawTransactionDto);
        // TODO - Build the payer detail
        transactionPayerHelper.buildTransactionPayer(dataTransformationDto, rawTransactionDto);
        // todo - Build the broker details
        transactionBrokerHelper.buildTransactionBroker(dataTransformationDto, rawTransactionDto);
        // todo - Build the member details
        rawTransactionDto.getTransaction().getMembers().stream().forEach(member -> {
            transactionMemberHelper.buildMemberDetail(dataTransformationDto, member);
        });
        log.info("Data Transformation DTO:{}", dataTransformationDto);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String data = objectMapper.writeValueAsString(dataTransformationDto);
        log.info("Data Transformation DTO as string:{}", data);
        transactionProducer.publishTransaction(dataTransformationDto);
        return dataTransformationDto;
    }

    /**
     * Construct the data transformation object
     * @param rawTransactionDto
     * @return
     */
    private DataTransformationDto constructDataTransformationObject(RawTransactionDto rawTransactionDto){
        return DataTransformationDto.builder()
                .transactionDto(TransactionDto.builder()
                        .zfcn(rawTransactionDto.getZfcn())
                        .ztcn(rawTransactionDto.getZtcn())
                        .transactionReceivedDate(LocalDateTime.now())
                        .transactionSourceTypeCode("MARKETPLACE")
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
}
