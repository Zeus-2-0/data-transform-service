package com.brihaspathee.zeus.service.interfaces;

import com.brihaspathee.zeus.web.model.RawTransactionDto;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 09, October 2022
 * Time: 7:11 AM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.service.interfaces
 * To change this template use File | Settings | File and Code Template
 */
public interface DataTransformer {

    /**
     * Service to transform the raw transaction to transaction dto
     * @param rawTransactionDto
     */
    void transformTransaction(RawTransactionDto rawTransactionDto) throws JsonProcessingException;
}
