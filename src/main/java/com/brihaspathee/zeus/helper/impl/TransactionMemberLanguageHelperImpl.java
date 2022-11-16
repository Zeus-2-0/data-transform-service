package com.brihaspathee.zeus.helper.impl;

import com.brihaspathee.zeus.dto.transaction.TransactionMemberDto;
import com.brihaspathee.zeus.dto.transaction.TransactionMemberLanguageDto;
import com.brihaspathee.zeus.edi.models.common.LUI;
import com.brihaspathee.zeus.helper.interfaces.ReferenceDataServiceHelper;
import com.brihaspathee.zeus.helper.interfaces.TransactionMemberLanguageHelper;
import com.brihaspathee.zeus.reference.data.model.XWalkResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 27, October 2022
 * Time: 3:57 PM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.helper.impl
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionMemberLanguageHelperImpl implements TransactionMemberLanguageHelper {

    /**
     * Reference data service instance
     */
    private final ReferenceDataServiceHelper referenceDataServiceHelper;

    /**
     * Build member language
     * @param memberDto
     * @param memberLanguages
     */
    @Override
    public void buildMemberLanguage(TransactionMemberDto memberDto, Set<LUI> memberLanguages) {
        List<TransactionMemberLanguageDto> memberLanguageDtos = new ArrayList<>();
        if(memberLanguages != null && memberLanguages.size() >0 ){
            memberLanguages.stream().forEach(language -> {
                XWalkResponse languageResponse =
                        referenceDataServiceHelper.getInternalRefData(
                                language.getLui04(),
                                "Language Type",
                                "EDI-834");
                TransactionMemberLanguageDto memberLanguageDto = TransactionMemberLanguageDto.builder()
                        .languageTypeCode(languageResponse.getInternalListCode())
                        .languageCode(language.getLui02())
                        .receivedDate(LocalDateTime.now())
                        .build();
                memberLanguageDtos.add(memberLanguageDto);

            });
        }
        if(memberLanguageDtos.size() > 0){
            memberDto.setLanguages(memberLanguageDtos);
        }
    }
}
