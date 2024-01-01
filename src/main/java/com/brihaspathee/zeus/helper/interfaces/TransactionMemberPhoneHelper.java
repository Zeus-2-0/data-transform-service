package com.brihaspathee.zeus.helper.interfaces;

import com.brihaspathee.zeus.dto.transaction.TransactionMemberDto;
import com.brihaspathee.zeus.edi.models.common.PER;

import java.time.LocalDateTime;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 27, October 2022
 * Time: 12:48 PM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.helper.interfaces
 * To change this template use File | Settings | File and Code Template
 */
public interface TransactionMemberPhoneHelper {

    /**
     * Build the member phone details
     * @param memberDto
     * @param communications
     * @param transactionReceivedDate
     */
    void buildMemberPhone(TransactionMemberDto memberDto,
                          PER communications,
                          LocalDateTime transactionReceivedDate);
}
