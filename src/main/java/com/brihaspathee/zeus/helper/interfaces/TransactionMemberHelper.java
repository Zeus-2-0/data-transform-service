package com.brihaspathee.zeus.helper.interfaces;

import com.brihaspathee.zeus.dto.account.RawTransactionDto;
import com.brihaspathee.zeus.edi.models.enrollment.Loop2000;
import com.brihaspathee.zeus.web.model.DataTransformationDto;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 26, October 2022
 * Time: 7:49 AM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.helper.interfaces
 * To change this template use File | Settings | File and Code Template
 */
public interface TransactionMemberHelper {

    /**
     * Build member details from the transaction
     * @param dataTransformationDto
     * @param member
     */
    void buildMemberDetail(DataTransformationDto dataTransformationDto,
                           Loop2000 member);
}
