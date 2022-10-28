package com.brihaspathee.zeus.helper.interfaces;

import com.brihaspathee.zeus.dto.account.RawTransactionDto;
import com.brihaspathee.zeus.web.model.DataTransformationDto;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 25, October 2022
 * Time: 2:09 PM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.helper.interfaces
 * To change this template use File | Settings | File and Code Template
 */
public interface TransactionBrokerHelper {

    /**
     * Build the broker details
     * @param dataTransformationDto
     * @param rawTransactionDto
     */
    void buildTransactionBroker(DataTransformationDto dataTransformationDto, RawTransactionDto rawTransactionDto);
}
