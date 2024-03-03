package com.brihaspathee.zeus.helper.interfaces;

import com.brihaspathee.zeus.dto.account.RawTransactionDto;
import com.brihaspathee.zeus.edi.models.enrollment.Loop2000;
import com.brihaspathee.zeus.test.TestMemberEntityCodes;
import com.brihaspathee.zeus.web.model.DataTransformationDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
     * @param testMemberEntityCodes
     * @param member
     * @param transactionReceivedDate
     */
    void buildMemberDetail(DataTransformationDto dataTransformationDto,
                           List<TestMemberEntityCodes> testMemberEntityCodes,
                           Loop2000 member,
                           LocalDateTime transactionReceivedDate);
}
