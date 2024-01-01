package com.brihaspathee.zeus.helper.interfaces;

import com.brihaspathee.zeus.dto.transaction.TransactionMemberDto;
import com.brihaspathee.zeus.edi.models.common.LUI;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 27, October 2022
 * Time: 3:55 PM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.helper.interfaces
 * To change this template use File | Settings | File and Code Template
 */
public interface TransactionMemberLanguageHelper {


    /**
     * Build member language
     * @param memberDto
     * @param memberLanguages
     * @param transactionReceivedDate
     */
    void buildMemberLanguage(TransactionMemberDto memberDto,
                             Set<LUI> memberLanguages,
                             LocalDateTime transactionReceivedDate);
}
