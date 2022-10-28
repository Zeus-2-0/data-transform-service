package com.brihaspathee.zeus.helper.interfaces;

import com.brihaspathee.zeus.dto.transaction.TransactionMemberDto;
import com.brihaspathee.zeus.edi.models.common.PER;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 27, October 2022
 * Time: 1:25 PM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.helper.interfaces
 * To change this template use File | Settings | File and Code Template
 */
public interface TransactionMemberEmailHelper {

    /**
     * Build member  email
     * @param memberDto
     * @param communications
     */
    void buildMemberEmail(TransactionMemberDto memberDto, PER communications);
}
