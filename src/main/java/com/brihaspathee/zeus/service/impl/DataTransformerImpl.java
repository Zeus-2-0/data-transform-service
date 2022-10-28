package com.brihaspathee.zeus.service.impl;

import com.brihaspathee.zeus.broker.producer.TransactionProducer;
import com.brihaspathee.zeus.dto.account.RawTransactionDto;
import com.brihaspathee.zeus.dto.transaction.TransactionDetailDto;
import com.brihaspathee.zeus.dto.transaction.TransactionDto;
import com.brihaspathee.zeus.dto.transaction.TransactionTradingPartnerDto;
import com.brihaspathee.zeus.helper.interfaces.TransactionDetailHelper;
import com.brihaspathee.zeus.service.interfaces.DataTransformer;
import com.brihaspathee.zeus.web.model.DataTransformationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
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
     * Transaction producer to send the transformed transaction
     */
    private final TransactionProducer transactionProducer;

    /**
     * Transforms the transaction
     * @param rawTransactionDto
     */
    @Override
    public void transformTransaction(RawTransactionDto rawTransactionDto) throws JsonProcessingException {
        DataTransformationDto dataTransformationDto = constructDataTransformationObject(rawTransactionDto);
        transactionDetailHelper.buildTransactionDetail(dataTransformationDto, rawTransactionDto);
        transactionProducer.publishTransaction(dataTransformationDto);
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
                        .tradingPartnerDto(TransactionTradingPartnerDto.builder().build())
                        .build())
                .transformationMessages(new ArrayList<>())
                .build();
    }
}
