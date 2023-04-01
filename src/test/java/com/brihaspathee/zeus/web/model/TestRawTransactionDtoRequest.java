package com.brihaspathee.zeus.web.model;

import com.brihaspathee.zeus.dto.account.RawTransactionDto;
import lombok.*;

import java.util.List;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 13, March 2023
 * Time: 9:33 PM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.web.model
 * To change this template use File | Settings | File and Code Template
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestRawTransactionDtoRequest {

    /**
     * Identifies if an exception is expected
     */
    private boolean exceptionExpected;

    /**
     * The exception code when an exception is expected
     */
    private String exceptionCode;

    /**
     * The exception message when an exception is expected
     */
    private String exceptionMessage;

    /**
     * The http status code expected
     */
    private String httpStatusCode;

    /**
     * The raw transaction Dto that is provided as input
     */
    private RawTransactionDto rawTransactionDto;

    /**
     * The data transformation DTO that is expected as output
     */
    private DataTransformationDto dataTransformationDto;

}
