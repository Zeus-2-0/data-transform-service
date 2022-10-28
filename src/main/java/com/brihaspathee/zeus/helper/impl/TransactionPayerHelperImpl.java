package com.brihaspathee.zeus.helper.impl;

import com.brihaspathee.zeus.dto.account.RawTransactionDto;
import com.brihaspathee.zeus.dto.transaction.TransactionPayerDto;
import com.brihaspathee.zeus.dto.transaction.TransactionSponsorDto;
import com.brihaspathee.zeus.edi.models.enrollment.Loop1000A;
import com.brihaspathee.zeus.edi.models.enrollment.Loop1000B;
import com.brihaspathee.zeus.helper.interfaces.TransactionPayerHelper;
import com.brihaspathee.zeus.web.model.DataTransformationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 25, October 2022
 * Time: 2:03 PM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.helper.impl
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionPayerHelperImpl implements TransactionPayerHelper {

    /**
     * Builds the transaction payer detail
     * @param dataTransformationDto
     * @param rawTransactionDto
     */
    @Override
    public void buildTransactionPayer(DataTransformationDto dataTransformationDto, RawTransactionDto rawTransactionDto) {
        Loop1000B payer = rawTransactionDto.getTransaction().getPayer();
        TransactionPayerDto payerDto = TransactionPayerDto.builder()
                .payerName(payer.getPayer().getN102())
                .payerId(payer.getPayer().getN104())
                .receivedDate(LocalDateTime.now())
                .build();
        dataTransformationDto.getTransactionDto().setPayer(payerDto);
    }
}
