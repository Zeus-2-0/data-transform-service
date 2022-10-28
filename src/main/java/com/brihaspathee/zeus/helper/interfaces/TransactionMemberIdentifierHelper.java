package com.brihaspathee.zeus.helper.interfaces;

import com.brihaspathee.zeus.dto.transaction.TransactionMemberDto;
import com.brihaspathee.zeus.edi.models.enrollment.Loop2000;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 26, October 2022
 * Time: 5:03 PM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.helper.interfaces
 * To change this template use File | Settings | File and Code Template
 */
public interface TransactionMemberIdentifierHelper {

    /**
     * Populate the identifiers that are received for the member
     * @param transactionMemberDto
     * @param member
     */
    void buildMemberIdentifier(TransactionMemberDto transactionMemberDto, Loop2000 member);
}
