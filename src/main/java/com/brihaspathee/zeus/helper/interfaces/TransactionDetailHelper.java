package com.brihaspathee.zeus.helper.interfaces;

import com.brihaspathee.zeus.dto.account.RawTransactionDto;
import com.brihaspathee.zeus.dto.transaction.TransactionDto;
import com.brihaspathee.zeus.web.model.DataTransformationDto;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 19, October 2022
 * Time: 4:00 PM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.helper.interfaces
 * To change this template use File | Settings | File and Code Template
 */
public interface TransactionDetailHelper {

    /**
     * Build the transaction detail object from the raw transaction
     * @param dataTransformationDto
     * @param rawTransactionDto
     */
    void buildTransactionDetail(DataTransformationDto dataTransformationDto, RawTransactionDto rawTransactionDto);

}
