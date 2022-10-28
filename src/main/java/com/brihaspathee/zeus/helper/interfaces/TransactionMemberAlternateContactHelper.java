package com.brihaspathee.zeus.helper.interfaces;

import com.brihaspathee.zeus.dto.transaction.TransactionMemberDto;
import com.brihaspathee.zeus.edi.models.enrollment.Loop2000;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 28, October 2022
 * Time: 8:11 AM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.helper.interfaces
 * To change this template use File | Settings | File and Code Template
 */
public interface TransactionMemberAlternateContactHelper {

    /**
     * Build the member's alternate contact details
     * @param memberDto
     * @param member
     */
    void buildAlternateContactInfo(TransactionMemberDto memberDto, Loop2000 member);
}
