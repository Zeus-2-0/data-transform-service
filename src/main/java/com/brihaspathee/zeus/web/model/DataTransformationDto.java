package com.brihaspathee.zeus.web.model;

import com.brihaspathee.zeus.dto.transaction.TransactionDto;
import lombok.*;

import java.util.List;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 20, October 2022
 * Time: 5:16 AM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.web.model
 * To change this template use File | Settings | File and Code Template
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataTransformationDto {

    /**
     * The transaction dto object
     */
    private TransactionDto transactionDto;

    /**
     * Messages that were created while raw transaction is transformed
     */
    private List<TransformationMessage> transformationMessages;
}
