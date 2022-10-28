package com.brihaspathee.zeus.helper.interfaces;

import com.brihaspathee.zeus.dto.transaction.TransactionMemberDto;
import com.brihaspathee.zeus.edi.models.enrollment.Loop2000;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 28, October 2022
 * Time: 7:40 AM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.helper.interfaces
 * To change this template use File | Settings | File and Code Template
 */
public interface TransactionMemberAddressHelper {

    /**
     * Build Member Address
     * @param memberDto
     * @param member
     */
    void buildMemberAddress(TransactionMemberDto memberDto,
                            Loop2000 member);
}
