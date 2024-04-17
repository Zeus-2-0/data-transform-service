package com.brihaspathee.zeus.helper.impl;

import com.brihaspathee.zeus.dto.account.MemberDto;
import com.brihaspathee.zeus.dto.account.RawTransactionDto;
import com.brihaspathee.zeus.dto.transaction.TransactionAttributeDto;
import com.brihaspathee.zeus.dto.transaction.TransactionDetailDto;
import com.brihaspathee.zeus.dto.transaction.TransactionDto;
import com.brihaspathee.zeus.dto.transaction.TransactionRateDto;
import com.brihaspathee.zeus.edi.models.common.DTP;
import com.brihaspathee.zeus.edi.models.common.REF;
import com.brihaspathee.zeus.edi.models.enrollment.Loop2000;
import com.brihaspathee.zeus.edi.models.enrollment.Loop2300;
import com.brihaspathee.zeus.edi.models.enrollment.Loop2710;
import com.brihaspathee.zeus.edi.models.enrollment.Transaction;
import com.brihaspathee.zeus.exception.PrimaryMemberNotFoundException;
import com.brihaspathee.zeus.helper.interfaces.ReferenceDataServiceHelper;
import com.brihaspathee.zeus.helper.interfaces.TransactionDetailHelper;
import com.brihaspathee.zeus.reference.data.model.XWalkRequest;
import com.brihaspathee.zeus.reference.data.model.XWalkResponse;
import com.brihaspathee.zeus.web.model.DataTransformationDto;
import com.brihaspathee.zeus.web.model.TransformationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 19, October 2022
 * Time: 4:03 PM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.helper.impl
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionDetailHelperImpl implements TransactionDetailHelper {

    /**
     * Reference data service helper to call the reference data service
     */
    private final ReferenceDataServiceHelper referenceDataServiceHelper;

    /**
     * Build the transaction detail object from the raw transaction
     * @param dataTransformationDto
     * @param rawTransactionDto
     */
    @Override
    public void buildTransactionDetail(DataTransformationDto dataTransformationDto, RawTransactionDto rawTransactionDto) {
        TransactionDetailDto transactionDetailDto = TransactionDetailDto.builder()
                .build();
        Loop2000 primarySubscriber = getPrimaryMember(rawTransactionDto);
        // Get and Set the transaction type
        getTransactionType(dataTransformationDto, primarySubscriber);
        // Get and Set the maintenance reason code
        getMaintenanceReasonCode(dataTransformationDto, primarySubscriber);
        // Get and Set the coverage type (Family or Dependent)
        getCoverageType(dataTransformationDto, primarySubscriber);
        log.info("Data Transformation DTO after setting coverage type:{}", dataTransformationDto);
        // Get and Set the Plan id and CSR Variant from the transaction
        getPlanId(dataTransformationDto, primarySubscriber);
        // Get and Set the group policy id
        getGroupPolicyId(dataTransformationDto, primarySubscriber);
        // Get and Set the effective date
        getEffectiveDate(dataTransformationDto, primarySubscriber);
        // Get and Set the end date
        getEndDate(dataTransformationDto, primarySubscriber);
        // Get and set the maintenance effective date
        getMaintenanceEffectiveDate(dataTransformationDto, primarySubscriber);
        // Get and set the policy amounts (This where the amounts like PREAMTTOT are set)
        getPolicyAmounts(dataTransformationDto, primarySubscriber);
        // Get and set all the transaction attributes
        getTransactionAttributes(dataTransformationDto, primarySubscriber);
    }

    /**
     * Get the primary member of the transaction
     * @param rawTransactionDto
     * @return
     */
    private Loop2000 getPrimaryMember(RawTransactionDto rawTransactionDto){
        return rawTransactionDto.getTransaction().getMembers().stream().filter(member ->
                member.getMemberDetail().getIns02().equals("18")).findFirst().orElseThrow(() -> {
            throw new PrimaryMemberNotFoundException("Primary member not found for transaction:" + rawTransactionDto.getZtcn());
        });
    }

    /**
     * Get the transaction type
     * @param dataTransformationDto
     * @param primaryMember
     */
    private void getTransactionType(DataTransformationDto dataTransformationDto, Loop2000 primaryMember){
        String transactionType = primaryMember.getMemberDetail().getIns03();
        XWalkResponse xWalkResponse = referenceDataServiceHelper.getInternalRefData(transactionType,
                "Transaction",
                "EDI-834");
//        if(transactionType.equals("021")){
//            transactionType = "ADD";
//        }else if (transactionType.equals("001")){
//            transactionType = "CHANGE";
//        }else if (transactionType.equals("025")){
//            transactionType = "REINSTATEMENT";
//        }else {
//            transactionType = "CANCEL-TERM";
//        }
        dataTransformationDto.getTransactionDto()
                .getTransactionDetail()
                .setTransactionTypeCode(
                        xWalkResponse.getInternalListCode());
    }

    /**
     * Get mainatenance reason code
     * @param dataTransformationDto
     * @param primaryMember
     */
    private void getMaintenanceReasonCode(DataTransformationDto dataTransformationDto,
                                          Loop2000 primaryMember){
        String maintenanceReasonCode = primaryMember.getMemberDetail().getIns04();
        XWalkResponse xWalkResponse = referenceDataServiceHelper.getInternalRefData(maintenanceReasonCode,
                "Reason",
                "EDI-834");
        dataTransformationDto.getTransactionDto()
                .getTransactionDetail()
                .setMaintenanceReasonCode(
                        xWalkResponse.getInternalListCode());
    }

    /**
     * Set the coverage type that was received in the transaction
     * @param dataTransformationDto
     * @param primaryMember
     */
    private void getCoverageType(DataTransformationDto dataTransformationDto,
                                 Loop2000 primaryMember){
        Optional<Loop2300> optionalHC = primaryMember.getHealthCoverages()
                .stream()
                .findFirst();
        String coverageTypeCode = null;
        if(optionalHC.isPresent()){
            Loop2300 healthCoverage = optionalHC.get();
            if(healthCoverage.getHealthCoverage() != null){
                coverageTypeCode = healthCoverage.getHealthCoverage().getHd05();
            }
        }
        dataTransformationDto.getTransactionDto()
                .getTransactionDetail()
                .setCoverageTypeCode(Objects.requireNonNullElse(coverageTypeCode, "FAM"));
        
    }

    /**
     * Get the plan id from the transaction and set it in the detail object
     * @param dataTransformationDto
     * @param primaryMember
     */
    private void getPlanId(DataTransformationDto dataTransformationDto, Loop2000 primaryMember){
        // Get the plan id from the primary subscriber's health coverage object
        Optional<REF> planIdRef = primaryMember.getHealthCoverages().iterator().next().getHealthCoveragePolicyNumbers().stream().filter(policyNumber ->
            policyNumber.getRef01().equals("CE")
        ).findFirst();
        // Check if plan id is present for the transaction
        if(planIdRef.isEmpty()){
            // if it is not present then create a transformation message
            TransformationMessage transformationMessage = TransformationMessage.builder()
                    .message("No Plan Id present for the primary subscriber")
                    .messageCode("2000001")
                    .messageType("CRITICAL")
                    .build();
            dataTransformationDto.getTransformationMessages().add(transformationMessage);
        }else{
            // if present then set the plan id in the transaction detail
            String fullPlanId = planIdRef.get().getRef02();
            // get the csr variant from the plan id
            String csrVariant = fullPlanId.substring(Math.max(fullPlanId.length() - 2, 0));
            // Strip of the csr variant from the plan id
            String planId = fullPlanId.substring(0, fullPlanId.length() - 2);
            dataTransformationDto.getTransactionDto().getTransactionDetail().setPlanId(planId);
            dataTransformationDto.getTransactionDto().getTransactionDetail().setCsrVariant(csrVariant);
        }
    }

    /**
     * Get the group policy id from the transaction and set it in the detail object
     * @param dataTransformationDto
     * @param primaryMember
     */
    private void getGroupPolicyId(DataTransformationDto dataTransformationDto,
                                    Loop2000 primaryMember){
        // Get the group policy id from the primary subscriber's health coverage object
        Optional<REF> planIdRef = primaryMember.getHealthCoverages().iterator().next().getHealthCoveragePolicyNumbers().stream().filter(policyNumber ->
                policyNumber.getRef01().equals("1L")
        ).findFirst();
        // Check if group policy id is present for the transaction
        if(planIdRef.isEmpty()){
            // if it is not present then create a transformation message
            TransformationMessage transformationMessage = TransformationMessage.builder()
                    .message("No Group Policy Id present for the primary subscriber")
                    .messageCode("2000002")
                    .messageType("CRITICAL")
                    .build();
            dataTransformationDto.getTransformationMessages().add(transformationMessage);
        }else{
            // if present then set the group policy id in the transaction detail
            dataTransformationDto.getTransactionDto().getTransactionDetail().setGroupPolicyId(planIdRef.get().getRef02());
        }
    }

    /**
     * Get the effective date of the transaction
     * @param primaryMember
     * @return
     */
    private void getEffectiveDate(DataTransformationDto dataTransformationDto,
                                       Loop2000 primaryMember){
        // Get the effective date of the transaction from the primary subscriber
        Optional<DTP> effectiveDate = primaryMember.getHealthCoverages().iterator().next().getHealthCoverageDates().stream().filter(coverageDate ->
                coverageDate.getDtp01().equals("348")
        ).findFirst();
        if (effectiveDate.isEmpty()){
            // if effectiveDate date is not present in the health coverage check for it in 356
            effectiveDate = primaryMember.getMemberLevelDates().stream()
                    .filter(
                            memberLevelDate -> memberLevelDate.getDtp01().equals("356")).findFirst();
        }
        if(effectiveDate.isEmpty() && !primaryMember.getMemberDetail().getIns03().equals("024")){
            // if it is not present and the transaction is anything other than CANCEL/TERM
            // set the transaction message
            TransformationMessage transformationMessage = TransformationMessage.builder()
                    .message("No effective date present in the transaction")
                    .messageCode("2000003")
                    .messageType("CRITICAL")
                    .build();
            dataTransformationDto.getTransformationMessages().add(transformationMessage);
        }else if(effectiveDate.isPresent()) {
            dataTransformationDto.getTransactionDto()
                    .getTransactionDetail()
                    .setEffectiveDate(LocalDate.parse(
                            effectiveDate.get().getDtp03(),
                            DateTimeFormatter.BASIC_ISO_DATE));
        }else{
            // this means that the transaction is a Cancel or term (024) transaction
            // so the effective date is end date
            Optional<DTP> endDate = getEndDate(primaryMember);
            if(endDate.isEmpty() && primaryMember.getMemberDetail().getIns03().equals("024")){
                // if it is not present and the transaction is CANCEL/TERM
                // set the transaction message
                TransformationMessage transformationMessage = TransformationMessage.builder()
                        .message("No end date present in the transaction")
                        .messageCode("2000004")
                        .messageType("CRITICAL")
                        .build();
                dataTransformationDto.getTransformationMessages().add(transformationMessage);
            }
            endDate.ifPresent(dtp -> dataTransformationDto.getTransactionDto()
                    .getTransactionDetail()
                    .setEffectiveDate(LocalDate.parse(dtp.getDtp03(), DateTimeFormatter.BASIC_ISO_DATE)));
        }
    }

    /**
     * Get the end date of the transaction
     * @param primaryMember
     * @return
     */
    private void getEndDate(DataTransformationDto dataTransformationDto,
                                  Loop2000 primaryMember){
        String transactionType = primaryMember.getMemberDetail().getIns03();
        if(!transactionType.equals("024")){
            // set the end date only if the transaction is not a cancel or term transaction
            // if it is a cancel or term transaction then the 357 0r 349 date will be set
            // as the effective date, since that is the date that the span is being canceled
            // or termed
            // Get the end date of the transaction from the primary subscriber
            Optional<DTP> endDate = getEndDate(primaryMember);
            endDate.ifPresent(dtp -> dataTransformationDto.getTransactionDto()
                    .getTransactionDetail()
                    .setEffectiveDate(LocalDate.parse(dtp.getDtp03(), DateTimeFormatter.BASIC_ISO_DATE)));
        }
    }

    /**
     * Get the end date from the transaction (357, 349)
     * @param primaryMember
     */
    private Optional<DTP> getEndDate(Loop2000 primaryMember){
        Optional<DTP> endDate = primaryMember.getHealthCoverages().iterator().next().getHealthCoverageDates().stream().filter(coverageDate ->
                coverageDate.getDtp01().equals("349")
        ).findFirst();
        if (endDate.isEmpty()){
            // if end date is not present in the health coverage check for it in 357
            endDate = primaryMember.getMemberLevelDates().stream()
                    .filter(
                            memberLevelDate -> memberLevelDate.getDtp01().equals("357")).findFirst();
        }
        return endDate;
    }

    /**
     * Get the maintenance effective date of the transaction
     * @param dataTransformationDto
     * @param primaryMember
     */
    private void getMaintenanceEffectiveDate(DataTransformationDto dataTransformationDto, Loop2000 primaryMember){
        Optional<DTP> maintenanceDate = primaryMember.getMemberLevelDates().stream()
                .filter(
                        memberLevelDate -> memberLevelDate.getDtp01().equals("303")).findFirst();
        if(maintenanceDate.isEmpty()){
            maintenanceDate = primaryMember.getHealthCoverages().iterator().next().getHealthCoverageDates().stream()
                    .filter(healthCoverageDate -> healthCoverageDate.getDtp01().equals("303")).findFirst();
        }
        if(maintenanceDate.isPresent()){
            dataTransformationDto.getTransactionDto().getTransactionDetail().setMaintenanceEffectiveDate(
                    LocalDate.parse(maintenanceDate.get().getDtp03(),DateTimeFormatter.BASIC_ISO_DATE));
        }
    }

    /**
     * Get the policy amounts from the transaction
     * @param dataTransformationDto
     * @param primaryMember
     */
    private void getPolicyAmounts(DataTransformationDto dataTransformationDto, Loop2000 primaryMember){
        List<TransactionRateDto> transactionRateDtos = new ArrayList<>();
        Map<LocalDate, String> csrVariantMap = new HashMap<>();
        primaryMember.getReportingCategories().getReportingCategories().stream().forEach(reportingCategory -> {
            String reportingCategoryName = reportingCategory
                    .getReportingCategoryDetails().getReportingCategory().getN102();
            switch (reportingCategoryName){
                case "PRE AMT TOT":
                    transactionRateDtos.add(extractPolicyAmount(reportingCategory, "PREAMTTOT"));
                    break;
                case "TOT RES AMT":
                    transactionRateDtos.add(extractPolicyAmount(reportingCategory, "TOTRESAMT"));
                    break;
                case "APTC AMT":
                    transactionRateDtos.add(extractPolicyAmount(reportingCategory, "APTCAMT"));
                    break;
                case "OTH PAY AMT1":
                    transactionRateDtos.add(extractPolicyAmount(reportingCategory, "OTHERPAYAMT1"));
                    break;
                case "OTH PAY AMT2":
                    transactionRateDtos.add(extractPolicyAmount(reportingCategory, "OTHERPAYAMT2"));
                    break;
                case "CSR AMT":
                    transactionRateDtos.add(extractPolicyAmount(reportingCategory, "CSRAMT"));
                    break;
                case "CSR ELIG CAT":
                    mapCSRVariant(csrVariantMap, reportingCategory);
            }
        });
        if(transactionRateDtos.size() > 0){
            if(csrVariantMap.isEmpty()){
                // this means that there is only one CSR Variant received in the transaction, and it can be set for all the rate types
                transactionRateDtos.stream().forEach(rateDto -> {
                    rateDto.setCsrVariant(dataTransformationDto.getTransactionDto().getTransactionDetail().getCsrVariant());
                });
            }else{
                // there should be CSR Variant for each of the PREAMTTOT rate start date
                for (Map.Entry<LocalDate, String> csrEntry:csrVariantMap.entrySet()) {
                    String csrVariant = csrEntry.getValue();
                    LocalDate csrDate = csrEntry.getKey();
                    // Find transaction rates that have the same date as that of the CSR Variant and set the
                    // CSR Variant on each rate
                    transactionRateDtos.stream()
                                    .filter(rateDto -> csrDate.equals(rateDto.getRateStartDate()))
                                    .collect(Collectors.toList())
                                    .stream().forEach(rateDto -> rateDto.setCsrVariant(csrVariant));
                }
            }
            dataTransformationDto.getTransactionDto().setTransactionRates(transactionRateDtos);
        }
    }

    /**
     * Map that contains the CSR Variant and its effective date
     * @param csrVariantMap
     * @param reportingCategory
     */
    private void mapCSRVariant(Map<LocalDate, String> csrVariantMap, Loop2710 reportingCategory) {
        String csrVariant = reportingCategory
                .getReportingCategoryDetails()
                .getCategoryReference()
                .iterator().next()
                .getRef02();
        DTP csrVariantDate = reportingCategory.getReportingCategoryDetails().getCategoryDate();
        if(csrVariantDate.getDtp02().equals("D8")){
            csrVariantMap.put(LocalDate.parse(csrVariantDate.getDtp03(), DateTimeFormatter.BASIC_ISO_DATE), csrVariant);
        }else{
            String csrVariantStartDate = csrVariantDate.getDtp02().split("-")[0];
            csrVariantMap.put(LocalDate.parse(csrVariantStartDate, DateTimeFormatter.BASIC_ISO_DATE), csrVariant);
        }
    }

    /**
     * Extract the policy amounts from the respective reporting category
     * @param reportingCategory
     * @param rateTypeCode
     * @return
     */
    private TransactionRateDto extractPolicyAmount(Loop2710 reportingCategory, String rateTypeCode) {
        String transactionRate = reportingCategory
                .getReportingCategoryDetails()
                .getCategoryReference()
                .iterator().next()
                .getRef02();
        TransactionRateDto transactionRateDto = TransactionRateDto.builder()
                .rateTypeCode(rateTypeCode)
                .transactionRate(BigDecimal.valueOf(Double.valueOf(transactionRate)))
                .build();
        DTP rateDates = reportingCategory.getReportingCategoryDetails().getCategoryDate();
        if(rateDates.getDtp02().equals("D8")){
            transactionRateDto.setRateStartDate(LocalDate.parse(rateDates.getDtp03(), DateTimeFormatter.BASIC_ISO_DATE));
        }else{
            String rateStartDate = rateDates.getDtp02().split("-")[0];
            String rateEndDate = rateDates.getDtp02().split("-")[1];
            transactionRateDto.setRateStartDate(LocalDate.parse(rateStartDate, DateTimeFormatter.BASIC_ISO_DATE));
            transactionRateDto.setRateEndDate(LocalDate.parse(rateEndDate, DateTimeFormatter.BASIC_ISO_DATE));
        }
        return transactionRateDto;
    }

    /**
     * Get all the transaction attributes
     * @param dataTransformationDto
     * @param primaryMember
     */
    private void getTransactionAttributes(DataTransformationDto dataTransformationDto, Loop2000 primaryMember){
        List<TransactionAttributeDto> transactionAttributeDtos = new ArrayList<>();
        primaryMember.getReportingCategories().getReportingCategories().stream().forEach(reportingCategory -> {
            String reportingCategoryName = reportingCategory
                    .getReportingCategoryDetails().getReportingCategory().getN102();
            switch (reportingCategoryName){
                case "SOURCE EXCHANGE ID":
                    transactionAttributeDtos.add(extractTransactionAttribute(reportingCategory, "SRCEXCHID"));
                    break;
                case "ADDL MAINT REASON":
                    transactionAttributeDtos.add(extractTransactionAttribute(reportingCategory, "AMRC"));
                    break;
                case "SEP REASON":
                    transactionAttributeDtos.add(extractTransactionAttribute(reportingCategory, "SEPREASON"));
                    break;
                case "REQUEST SUBMIT TIMESTAMP":
                    transactionAttributeDtos.add(extractTransactionAttribute(reportingCategory, "REQSBTTMS"));
                    break;
                case "APPLICATION ID AND ORIGIN":
                    transactionAttributeDtos.add(extractTransactionAttribute(reportingCategory, "APPID"));
                    break;
                case "RATING AREA":
                    transactionAttributeDtos.add(extractTransactionAttribute(reportingCategory, "RATINGAREA"));
                    break;
            }
            if(transactionAttributeDtos.size() > 0){
                dataTransformationDto.getTransactionDto().setTransactionAttributes(transactionAttributeDtos);
            }
        });
    }

    /**
     * Extract the attribute values
     * @param reportingCategory
     * @param attributeType
     * @return
     */
    private TransactionAttributeDto extractTransactionAttribute(Loop2710 reportingCategory, String attributeType){
        String attributeValue = reportingCategory
                .getReportingCategoryDetails()
                .getCategoryReference()
                .iterator().next()
                .getRef02();
        TransactionAttributeDto transactionAttributeDto = TransactionAttributeDto.builder()
                .transactionAttributeTypeCode(attributeType)
                .transactionAttributeValue(attributeValue)
                .build();
        return transactionAttributeDto;
    }

}
