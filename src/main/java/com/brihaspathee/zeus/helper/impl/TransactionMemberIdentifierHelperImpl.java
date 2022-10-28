package com.brihaspathee.zeus.helper.impl;

import com.brihaspathee.zeus.dto.transaction.TransactionMemberDto;
import com.brihaspathee.zeus.dto.transaction.TransactionMemberIdentifierDto;
import com.brihaspathee.zeus.edi.models.common.REF;
import com.brihaspathee.zeus.edi.models.enrollment.Loop2000;
import com.brihaspathee.zeus.helper.interfaces.ReferenceDataServiceHelper;
import com.brihaspathee.zeus.helper.interfaces.TransactionMemberIdentifierHelper;
import com.brihaspathee.zeus.reference.data.model.XWalkResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 26, October 2022
 * Time: 5:04 PM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.helper.impl
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionMemberIdentifierHelperImpl implements TransactionMemberIdentifierHelper {

    /**
     * Reference data service instance
     */
    private final ReferenceDataServiceHelper referenceDataServiceHelper;

    /**
     * Populates the member identifiers
     * @param transactionMemberDto
     * @param member
     */
    @Override
    public void buildMemberIdentifier(TransactionMemberDto transactionMemberDto, Loop2000 member) {
        String exchangeSubId = member.getSubscriberIdentifier().getRef02();
        if (exchangeSubId !=null){
            XWalkResponse identifierResponse = referenceDataServiceHelper.getInternalRefData(
                    member.getSubscriberIdentifier().getRef01(), "Identifier", "EDI-834");
            TransactionMemberIdentifierDto identifierDto = TransactionMemberIdentifierDto.builder()
                    .identifierTypeCode(identifierResponse.getInternalListCode())
                    .identifierValue(exchangeSubId)
                    .build();
            transactionMemberDto.getIdentifiers().add(identifierDto);
        }
        Optional<REF> exchangeMemberId =
                member.getMemberSupplementalIdentifiers().stream().filter(
                        identifier -> identifier.getRef01().equals("17")).findFirst();
        if(exchangeMemberId.isPresent()){
            REF exchangeMembId = exchangeMemberId.get();
            XWalkResponse xWalkResponse = referenceDataServiceHelper.getInternalRefData(exchangeMembId.getRef01(), "Identifier",
                    "EDI-834");
            TransactionMemberIdentifierDto identifierDto = TransactionMemberIdentifierDto.builder()
                    .identifierTypeCode(xWalkResponse.getInternalListCode())
                    .identifierValue(exchangeMemberId.get().getRef02())
                    .build();
            transactionMemberDto.getIdentifiers().add(identifierDto);
        }
        String memberIdentifier = member.getMemberDemographics().getMemberName().getNm109();
        if (memberIdentifier != null){
            String memberIdentifierType = member.getMemberDemographics().getMemberName().getNm108();
            XWalkResponse xWalkResponse = referenceDataServiceHelper.getInternalRefData(memberIdentifierType, "Identifier",
                    "EDI-834");
            TransactionMemberIdentifierDto identifierDto = TransactionMemberIdentifierDto.builder()
                    .identifierTypeCode(xWalkResponse.getInternalListCode())
                    .identifierValue(memberIdentifier)
                    .build();
            transactionMemberDto.getIdentifiers().add(identifierDto);
        }
    }
}
