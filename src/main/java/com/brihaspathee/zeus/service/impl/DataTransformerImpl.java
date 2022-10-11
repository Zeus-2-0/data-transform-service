package com.brihaspathee.zeus.service.impl;

import com.brihaspathee.zeus.broker.producer.TransactionProducer;
import com.brihaspathee.zeus.service.interfaces.DataTransformer;
import com.brihaspathee.zeus.web.model.RawTransactionDto;
import com.brihaspathee.zeus.web.model.TransactionDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
     * Transaction producer to send the transformed transaction
     */
    private final TransactionProducer transactionProducer;

    /**
     * Transforms the transaction
     * @param rawTransactionDto
     */
    @Override
    public void transformTransaction(RawTransactionDto rawTransactionDto) throws JsonProcessingException {
        TransactionDto transactionDto = TransactionDto.builder()
                .zfcn(rawTransactionDto.getZfcn())
                .ztcn(rawTransactionDto.getZtcn())
                .marketplaceTypeCode(rawTransactionDto.getMarketplaceTypeCode())
                .lineOfBusinessTypeCode(rawTransactionDto.getLineOfBusinessTypeCode())
                .stateTypeCode(rawTransactionDto.getStateTypeCode())
                .businessUnitTypeCode(rawTransactionDto.getBusinessUnitTypeCode())
                .tradingPartnerId(rawTransactionDto.getTradingPartnerId())
                .build();
        transactionProducer.publishTransaction(transactionDto);
    }
}
