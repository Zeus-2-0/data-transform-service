package com.brihaspathee.zeus.helper.impl;

import com.brihaspathee.zeus.dto.transaction.TransactionMemberAddressDto;
import com.brihaspathee.zeus.dto.transaction.TransactionMemberDto;
import com.brihaspathee.zeus.edi.models.common.N3;
import com.brihaspathee.zeus.edi.models.common.N4;
import com.brihaspathee.zeus.edi.models.enrollment.Loop2000;
import com.brihaspathee.zeus.helper.interfaces.TransactionMemberAddressHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 28, October 2022
 * Time: 7:42 AM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.helper.impl
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionMemberAddressHelperImpl implements TransactionMemberAddressHelper {

    /**
     * Build member address
     * @param memberDto
     * @param member
     */
    @Override
    public void buildMemberAddress(TransactionMemberDto memberDto, Loop2000 member) {
        List<TransactionMemberAddressDto> memberAddressDtos = new ArrayList<>();
        TransactionMemberAddressDto residence = buildMemberAddress("RESIDENCE",
                member.getMemberDemographics().getMemberAddressLine(),
                member.getMemberDemographics().getMemberCityStateZip());
        if(residence != null){
            memberAddressDtos.add(residence);
        }
        if(member.getMemberMailingAddress() != null){
            TransactionMemberAddressDto mailing = buildMemberAddress("MAIL",
                    member.getMemberMailingAddress().getMemberAddressLine(),
                    member.getMemberMailingAddress().getMemberCityStateZip());
            if(mailing != null){
                memberAddressDtos.add(mailing);
            }
        }

        if(memberAddressDtos.size() > 0){
            memberDto.setMemberAddresses(memberAddressDtos);
        }
    }

    private TransactionMemberAddressDto buildMemberAddress(String addressType, N3 addressLine, N4 cityStateZip){
        if(addressLine == null || cityStateZip == null){
            return null;
        }
        TransactionMemberAddressDto addressDto = TransactionMemberAddressDto.builder()
                .addressTypeCode(addressType)
                .addressLine1(addressLine.getN301())
                .addressLine2(addressLine.getN302())
                .city(cityStateZip.getN401())
                .stateTypeCode(cityStateZip.getN402())
                .zipCode(cityStateZip.getN403())
                .countyCode(cityStateZip.getN406())
                .receivedDate(LocalDateTime.now())
                .build();
        return addressDto;
    }
}
