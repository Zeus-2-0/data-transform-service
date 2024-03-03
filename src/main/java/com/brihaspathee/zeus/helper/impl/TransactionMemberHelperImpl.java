package com.brihaspathee.zeus.helper.impl;

import com.brihaspathee.zeus.dto.transaction.TransactionMemberDto;
import com.brihaspathee.zeus.dto.transaction.TransactionMemberIdentifierDto;
import com.brihaspathee.zeus.edi.models.common.*;
import com.brihaspathee.zeus.edi.models.enrollment.Loop2000;
import com.brihaspathee.zeus.edi.models.enrollment.Loop2100A;
import com.brihaspathee.zeus.edi.models.enrollment.Loop2300;
import com.brihaspathee.zeus.edi.models.enrollment.Loop2710;
import com.brihaspathee.zeus.helper.interfaces.*;
import com.brihaspathee.zeus.reference.data.model.XWalkRequest;
import com.brihaspathee.zeus.reference.data.model.XWalkResponse;
import com.brihaspathee.zeus.test.TestMemberEntityCodes;
import com.brihaspathee.zeus.util.DataTransformerUtil;
import com.brihaspathee.zeus.web.model.DataTransformationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 26, October 2022
 * Time: 7:50 AM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.helper.impl
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionMemberHelperImpl implements TransactionMemberHelper {

    /**
     * Reference data service instance
     */
    private final ReferenceDataServiceHelper referenceDataServiceHelper;

    /**
     * Transaction Member Identifier Helper Instance
     */
    private final TransactionMemberIdentifierHelper identifierHelper;

    /**
     * Transaction Member Address Helper Instance
     */
    private final TransactionMemberAddressHelper addressHelper;

    /**
     * Transaction Member Phone Helper Instance
     */
    private final TransactionMemberPhoneHelper memberPhoneHelper;

    /**
     * Transaction Member Email Helper Instance
     */
    private final TransactionMemberEmailHelper memberEmailHelper;

    /**
     * Transaction Member Language Helper Instance
     */
    private final TransactionMemberLanguageHelper memberLanguageHelper;

    /**
     * Transaction Member Alternate Contact Helper Instance
     */
    private final TransactionMemberAlternateContactHelper memberAlternateContactHelper;

    /**
     * Utility class to populate the entity codes on a test environment
     */
    private final DataTransformerUtil dataTransformerUtil;

    /**
     * Build Member detail
     * @param dataTransformationDto
     * @param testMemberEntityCodes
     * @param member
     * @param transactionReceivedDate
     */
    @Override
    public void buildMemberDetail(DataTransformationDto dataTransformationDto,
                                  List<TestMemberEntityCodes> testMemberEntityCodes,
                                  Loop2000 member,
                                  LocalDateTime transactionReceivedDate) {
        TransactionMemberDto memberDto = TransactionMemberDto.builder()
                .identifiers(new ArrayList<>())
                .build();
        // Check if member entity codes needs to be populated
        // this is done for testing purposes
        Map<String, List<String>> entityCodes = dataTransformerUtil.getMemberEntityCodes(testMemberEntityCodes, member);
        memberDto.setEntityCodes(entityCodes);
        // Populate the details from INS segment
        populateMemberTransactionInfo(memberDto, member.getMemberDetail());
        // Populate member demographics
        populateMemberDemographics(memberDto, member);
        // Get member effective date
        populateMemberDates(memberDto, member);
        // Retrieve all the identifiers from the transaction for the member
        identifierHelper.buildMemberIdentifier(memberDto, member, transactionReceivedDate);
        // Retrieve member residential and mailing address from the transaction
        addressHelper.buildMemberAddress(memberDto,member, transactionReceivedDate);
        // Retrieve all the member phones from the transaction
        memberPhoneHelper.buildMemberPhone(memberDto, member.getMemberDemographics().getMemberCommunication(), transactionReceivedDate);
        // Retrieve all the member emails from the transaction
        memberEmailHelper.buildMemberEmail(memberDto, member.getMemberDemographics().getMemberCommunication(), transactionReceivedDate);
        // Retrieve all the member languages from the transaction
        memberLanguageHelper.buildMemberLanguage(memberDto, member.getMemberDemographics().getMemberLanguages(), transactionReceivedDate);
        // Retrieve all the alternate contacts from the transaction
        memberAlternateContactHelper.buildAlternateContactInfo(memberDto, member, transactionReceivedDate);
        dataTransformationDto.getTransactionDto().getMembers().add(memberDto);
    }

    /**
     * This method populates the member transaction dto object
     * @param transactionMemberDto
     * @param memberInfo
     */
    private void populateMemberTransactionInfo(TransactionMemberDto transactionMemberDto,
                                               INS memberInfo){
        String relationshipTypeCode = memberInfo.getIns02();
        String transactionTypeCode = memberInfo.getIns03();
        String reasonTypeCode = memberInfo.getIns04();
        transactionMemberDto.setRelationshipTypeCode(referenceDataServiceHelper.getInternalRefData(
                relationshipTypeCode, "Relationship","EDI-834")
                .getInternalListCode());
        transactionMemberDto.setTransactionTypeCode(referenceDataServiceHelper.getInternalRefData(
                transactionTypeCode, "Transaction", "EDI-834")
                .getInternalListCode());
        if(reasonTypeCode != null && !reasonTypeCode.equals("")){
            transactionMemberDto.setReasonTypeCode(referenceDataServiceHelper.getInternalRefData(
                    reasonTypeCode, "Reason","EDI-834")
                    .getInternalListCode());
        }
    }


    /**
     * Populate effective dates of the member
     * @param memberDto
     * @param member
     */
    private void populateMemberDates(TransactionMemberDto memberDto, Loop2000 member){
        // Try and get the dates from the health coverage loop
        Loop2300 healthCoverage = member.getHealthCoverages().iterator().next();
        healthCoverage.getHealthCoverageDates().stream().forEach(date -> {
            if(date.getDtp01().equals("348")){
                memberDto.setEffectiveDate(LocalDate.parse(date.getDtp03(), DateTimeFormatter.BASIC_ISO_DATE));
            }else if(date.getDtp01().equals("349")){
                memberDto.setEndDate(LocalDate.parse(date.getDtp03(), DateTimeFormatter.BASIC_ISO_DATE));
            }
        });
        // if effective date is not present in the health coverage loop then look for it in member level dates
        if(memberDto.getEffectiveDate() == null){
            Optional<DTP> endDate = member.getMemberLevelDates().stream()
                    .filter(date -> date.getDtp01().equals("356"))
                    .findFirst();
            if(endDate.isPresent()){
                memberDto.setEndDate(LocalDate.parse(endDate.get().getDtp03(), DateTimeFormatter.BASIC_ISO_DATE));
            }
        }
        // if end date is not present in the health coverage loop then look for it in member level dates
        if(memberDto.getEndDate() == null){
            Optional<DTP> endDate = member.getMemberLevelDates().stream()
                    .filter(date -> date.getDtp01().equals("357"))
                    .findFirst();
            if(endDate.isPresent()){
                memberDto.setEndDate(LocalDate.parse(endDate.get().getDtp03(), DateTimeFormatter.BASIC_ISO_DATE));
            }

        }
    }

    /**
     * Populate the member demographic information
     * @param memberDto
     * @param member
     */
    private void populateMemberDemographics(TransactionMemberDto memberDto, Loop2000 member){
        Loop2100A memberDemo = member.getMemberDemographics();
        NM1 memberName = memberDemo.getMemberName();
        memberDto.setFirstName(memberName.getNm104());
        memberDto.setMiddleName(memberName.getNm105());
        memberDto.setLastName(memberName.getNm103());
        DMG dobGender = memberDemo.getMemberDemographics();
        if(dobGender.getDmg02() != null){
            memberDto.setDateOfBirth(LocalDate.parse(dobGender.getDmg02(), DateTimeFormatter.BASIC_ISO_DATE));
        }
        if(dobGender.getDmg03() != null){
            XWalkResponse genderResponse = referenceDataServiceHelper.getInternalRefData(
                    dobGender.getDmg03(),
                    "Gender",
                    "EDI-834");
            memberDto.setGenderTypeCode(genderResponse.getInternalListCode());
        }
        HLH healthInfo = memberDemo.getMemberHealthInformation();
        if(healthInfo != null && healthInfo.getHlh01() != null){
            XWalkResponse tobaccoResponse = referenceDataServiceHelper.getInternalRefData(
                    healthInfo.getHlh01(),
                    "Tobacco",
                    "EDI-834"
            );
            memberDto.setTobaccoIndicator(tobaccoResponse.getInternalListCode());
        }else{
            memberDto.setTobaccoIndicator("UNKNOWN");
        }
        Optional<Loop2710> memberRateCategory = member.getReportingCategories().getReportingCategories().stream().filter(reportingCategory -> {
           return reportingCategory.getReportingCategoryDetails().getReportingCategory().getN102().equals("PRE AMT 1");
        }).findFirst();
        if(memberRateCategory.isPresent()){
            memberDto.setMemberRate(
                    BigDecimal.valueOf(
                            Double.valueOf(
                                    memberRateCategory.get()
                                            .getReportingCategoryDetails()
                                            .getCategoryReference()
                                            .iterator().next()
                                            .getRef02())));
        }
    }
}
