package com.brihaspathee.zeus.helper.impl;

import com.brihaspathee.zeus.dto.transaction.TransactionAlternateContactDto;
import com.brihaspathee.zeus.dto.transaction.TransactionMemberDto;
import com.brihaspathee.zeus.edi.models.common.N3;
import com.brihaspathee.zeus.edi.models.common.N4;
import com.brihaspathee.zeus.edi.models.common.NM1;
import com.brihaspathee.zeus.edi.models.common.PER;
import com.brihaspathee.zeus.edi.models.enrollment.Loop2000;
import com.brihaspathee.zeus.helper.interfaces.ReferenceDataServiceHelper;
import com.brihaspathee.zeus.helper.interfaces.TransactionMemberAlternateContactHelper;
import com.brihaspathee.zeus.reference.data.model.XWalkResponse;
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
 * Time: 8:12 AM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.helper.impl
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionMemberAlternateContactHelperImpl implements TransactionMemberAlternateContactHelper {

    /**
     * Reference data service instance
     */
    private final ReferenceDataServiceHelper referenceDataServiceHelper;

    /**
     * Build Alternate Contact information of the member
     * @param memberDto
     * @param member
     * @param transactionReceivedDate
     */
    @Override
    public void buildAlternateContactInfo(TransactionMemberDto memberDto,
                                          Loop2000 member,
                                          LocalDateTime transactionReceivedDate) {
        List<TransactionAlternateContactDto> alternateContactDtos = new ArrayList<>();
        if(member.getCustodialParent() != null){
            // build the custodial parent information
            alternateContactDtos.add(createAlternateContact(member.getCustodialParent().getCustodialParentName(),
                    member.getCustodialParent().getCustodialParentCommunications(),
                    member.getCustodialParent().getCustodialParentAddressLine(),
                    member.getCustodialParent().getCustodialParentCityStateZip(), transactionReceivedDate));
        }
        if(member.getResponsiblePersons() != null && member.getResponsiblePersons().size() > 0){
            // build the responsible person information
            member.getResponsiblePersons().stream().forEach(responsiblePerson -> {
                alternateContactDtos.add(createAlternateContact(responsiblePerson.getResponsiblePersonName(),
                        responsiblePerson.getResponsiblePersonCommunications(),
                        responsiblePerson.getResponsiblePersonAddressLine(),
                        responsiblePerson.getResponsiblePersonCityStateZip(), transactionReceivedDate));
            });
        }
        if(member.getEmployers() != null && member.getEmployers().size() > 0){
            // build the Employer information
            member.getEmployers().stream().forEach(employer -> {
                alternateContactDtos.add(createAlternateContact(employer.getMemberEmployerName(),
                        employer.getMemberEmployerCommunications(),
                        employer.getMemberEmployerAddressLine(),
                        employer.getMemberEmployerCityStateZip(), transactionReceivedDate));
            });
        }
        if(member.getSchools() != null && member.getSchools().size() > 0){
            // build the responsible person information
            member.getSchools().stream().forEach(school -> {
                alternateContactDtos.add(createAlternateContact(school.getMemberSchoolName(),
                        school.getMemberSchoolCommunications(),
                        school.getMemberSchoolAddressLine(),
                        school.getMemberSchoolCityStateZip(), transactionReceivedDate));
            });
        }
        if(alternateContactDtos.size() > 0){
            memberDto.setAlternateContacts(alternateContactDtos);
        }
    }

    /**
     * Create the alternate contact information
     * @param name
     * @param communications
     * @param addressLines
     * @param cityStateZip
     * @param transactionReceivedDate
     * @return
     */
    private TransactionAlternateContactDto createAlternateContact(NM1 name,
                                                                  PER communications,
                                                                  N3 addressLines,
                                                                  N4 cityStateZip,
                                                                  LocalDateTime transactionReceivedDate){
        XWalkResponse alternateContactType = referenceDataServiceHelper.getInternalRefData(name.getNm101(),
                "Alternate Contact",
                "EDI-834");
        TransactionAlternateContactDto alternateContactDto = TransactionAlternateContactDto.builder()
                .alternateContactTypeCode(alternateContactType.getInternalListCode())
                .firstName(name.getNm104())
                .middleName(name.getNm105())
                .lastName(name.getNm103())
                .build();
        if(name.getNm109() != null){
            XWalkResponse identifier = referenceDataServiceHelper.getInternalRefData(name.getNm108(),
                    "Identifier",
                    "EDI-834");
            alternateContactDto.setIdentifierTypeCode(identifier.getInternalListCode());
            alternateContactDto.setIdentifierValue(name.getNm109());
        }
        if(communications != null){
            if(communications.getPer03() != null && communications.getPer03().equals("EM")){
                alternateContactDto.setEmail(communications.getPer04());
            } else if(communications.getPer05() != null && communications.getPer05().equals("EM")){
                alternateContactDto.setEmail(communications.getPer06());
            } else if(communications.getPer05() != null && communications.getPer07().equals("EM")){
                alternateContactDto.setEmail(communications.getPer08());
            }
            if(communications.getPer03() != null && !communications.getPer03().equals("EM")){
                setPhoneNumber(alternateContactDto, communications.getPer03(), communications.getPer04());
            } else if(communications.getPer05() != null && !communications.getPer05().equals("EM")){
                setPhoneNumber(alternateContactDto, communications.getPer05(), communications.getPer06());
            } else if(communications.getPer07() != null && communications.getPer07().equals("EM")){
                setPhoneNumber(alternateContactDto, communications.getPer07(), communications.getPer08());
            }
        }
        if(addressLines != null){
            alternateContactDto.setAddressLine1(addressLines.getN301());
            alternateContactDto.setAddressLine2(addressLines.getN302());
            alternateContactDto.setCity(cityStateZip.getN401());
            alternateContactDto.setStateTypeCode(cityStateZip.getN402());
            alternateContactDto.setZipCode(cityStateZip.getN403());
        }
        alternateContactDto.setReceivedDate(transactionReceivedDate);
        return alternateContactDto;
    }

    /**
     * Get the phone number type and set it
     * @param alternateContactDto
     * @param phoneType
     * @param phoneNumber
     */
    private void setPhoneNumber(TransactionAlternateContactDto alternateContactDto,
                                String phoneType,
                                String phoneNumber){
        XWalkResponse phone = referenceDataServiceHelper.getInternalRefData(phoneType,
                "Phone",
                "EDI-834");
        alternateContactDto.setPhoneTypeCode(phone.getInternalListCode());
        alternateContactDto.setPhoneNumber(phoneNumber);
    }
}
