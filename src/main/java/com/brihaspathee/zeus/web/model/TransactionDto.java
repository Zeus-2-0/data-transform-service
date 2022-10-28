package com.brihaspathee.zeus.web.model;

import lombok.*;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 17, March 2022
 * Time: 5:09 PM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.web.model
 * To change this template use File | Settings | File and Code Template
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    private String transactionSK;

    private String transactionId;

    @Override
    public String toString() {
        return "TransactionDto{" +
                "transactionSK='" + transactionSK + '\'' +
                ", transactionId='" + transactionId + '\'' +
                '}';
    }
}
