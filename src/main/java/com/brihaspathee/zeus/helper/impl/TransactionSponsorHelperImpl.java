package com.brihaspathee.zeus.helper.impl;

import com.brihaspathee.zeus.dto.account.RawTransactionDto;
import com.brihaspathee.zeus.dto.transaction.TransactionSponsorDto;
import com.brihaspathee.zeus.edi.models.enrollment.Loop1000A;
import com.brihaspathee.zeus.helper.interfaces.ReferenceDataServiceHelper;
import com.brihaspathee.zeus.helper.interfaces.TransactionSponsorHelper;
import com.brihaspathee.zeus.reference.data.model.XWalkRequest;
import com.brihaspathee.zeus.reference.data.model.XWalkResponse;
import com.brihaspathee.zeus.web.model.DataTransformationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 25, October 2022
 * Time: 8:05 AM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.helper.impl
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionSponsorHelperImpl implements TransactionSponsorHelper {

    /**
     * Reference data service instance to get the id
     */
    private ReferenceDataServiceHelper referenceDataServiceHelper;

    /**
     * Build the sponsor detail
     * @param dataTransformationDto
     * @param rawTransactionDto
     */
    @Override
    public void buildSponsor(DataTransformationDto dataTransformationDto,
                             RawTransactionDto rawTransactionDto) {
        Loop1000A sponsor = rawTransactionDto.getTransaction().getSponsor();
        TransactionSponsorDto sponsorDto = TransactionSponsorDto.builder()
                .sponsorName(sponsor.getSponsor().getN102())
                .sponsorId(sponsor.getSponsor().getN104())
                .receivedDate(LocalDateTime.now())
                .build();
        dataTransformationDto.getTransactionDto().setSponsor(sponsorDto);
    }
}
