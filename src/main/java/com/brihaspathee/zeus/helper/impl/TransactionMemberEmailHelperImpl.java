package com.brihaspathee.zeus.helper.impl;

import com.brihaspathee.zeus.dto.transaction.TransactionMemberDto;
import com.brihaspathee.zeus.dto.transaction.TransactionMemberEmailDto;
import com.brihaspathee.zeus.edi.models.common.PER;
import com.brihaspathee.zeus.helper.interfaces.TransactionMemberEmailHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 27, October 2022
 * Time: 1:27 PM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.helper.impl
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionMemberEmailHelperImpl implements TransactionMemberEmailHelper {

    /**
     * Build Member Email information
     * @param memberDto
     * @param communications
     */
    @Override
    public void buildMemberEmail(TransactionMemberDto memberDto, PER communications) {
        if(communications == null){
            return;
        }
        List<TransactionMemberEmailDto> emailDtos = new ArrayList<>();
        retrieveEmail(emailDtos, communications.getPer03(), communications.getPer04());
        retrieveEmail(emailDtos, communications.getPer05(), communications.getPer06());
        retrieveEmail(emailDtos, communications.getPer07(), communications.getPer08());
        if(emailDtos.size() > 0){
            memberDto.setEmails(emailDtos);
        }
    }

    /**
     * Retrieve Emails and create transaction member email dto
     * @param emailDtos
     * @param commType
     * @param commValue
     */
    private void retrieveEmail(List<TransactionMemberEmailDto> emailDtos, String commType, String commValue){
        if(commValue != null && commType.equals("EM")){
            TransactionMemberEmailDto memberEmailDto = TransactionMemberEmailDto.builder()
                    .email(commValue)
                    .build();
            emailDtos.add(memberEmailDto);
        }
    }
}
