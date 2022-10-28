package com.brihaspathee.zeus.helper.impl;

import com.brihaspathee.zeus.dto.transaction.TransactionMemberDto;
import com.brihaspathee.zeus.dto.transaction.TransactionMemberPhoneDto;
import com.brihaspathee.zeus.edi.models.common.PER;
import com.brihaspathee.zeus.helper.interfaces.ReferenceDataServiceHelper;
import com.brihaspathee.zeus.helper.interfaces.TransactionMemberPhoneHelper;
import com.brihaspathee.zeus.reference.data.model.XWalkResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 27, October 2022
 * Time: 12:49 PM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.helper.impl
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionMemberPhoneHelperImpl implements TransactionMemberPhoneHelper {

    /**
     * Reference data service instance
     */
    private final ReferenceDataServiceHelper referenceDataServiceHelper;

    /**
     * Build member phone details
     * @param memberDto
     * @param communications
     */
    @Override
    public void buildMemberPhone(TransactionMemberDto memberDto, PER communications) {
        if(communications == null){
            return;
        }
        List<TransactionMemberPhoneDto> memberPhoneDtos = new ArrayList<>();
        if(communications.getPer04() != null && !communications.getPer03().equals("EM")){
            populatePhoneNumber(communications.getPer03(),
                    communications.getPer04(),
                    memberPhoneDtos);
        }
        if(communications.getPer06() != null && !communications.getPer05().equals("EM")){
            populatePhoneNumber(communications.getPer05(),
                    communications.getPer06(),
                    memberPhoneDtos);
        }
        if(communications.getPer08() != null && !communications.getPer07().equals("EM")){
            populatePhoneNumber(communications.getPer07(),
                    communications.getPer08(),
                    memberPhoneDtos);
        }
        if(memberPhoneDtos.size() > 0){
            memberDto.setMemberPhones(memberPhoneDtos);
        }
    }

    /**
     * Populate the phone number
     * @param phoneType
     * @param phoneNumber
     * @param memberPhoneDtos
     */
    private void populatePhoneNumber(String phoneType, String phoneNumber, List<TransactionMemberPhoneDto> memberPhoneDtos) {
        XWalkResponse xWalkResponse = referenceDataServiceHelper.getInternalRefData(
                phoneType,
                "Phone",
                "EDI-834");
        TransactionMemberPhoneDto memberPhoneDto = TransactionMemberPhoneDto.builder()
                .phoneTypeCode(xWalkResponse.getInternalListCode())
                .phoneNumber(phoneNumber)
                .build();
        memberPhoneDtos.add(memberPhoneDto);
    }
}
