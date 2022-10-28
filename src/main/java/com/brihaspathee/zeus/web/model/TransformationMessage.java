package com.brihaspathee.zeus.web.model;

import lombok.*;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 20, October 2022
 * Time: 5:18 AM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.web.model
 * To change this template use File | Settings | File and Code Template
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransformationMessage {

    /**
     * Member code will be populated if the message is for a member
     * Will not be populated if the message is at the transaction level
     */
    private String memberCode;

    /**
     * Unique message code for the message
     */
    private String messageCode;

    /**
     * Identifies the type of message "WARNING", "CRITICAL", "INFO"
     */
    private String messageType;

    /**
     * A short description of the message
     */
    private String message;

    /**
     * toString method
     * @return
     */
    @Override
    public String toString() {
        return "TransformationMessage{" +
                "messageCode='" + messageCode + '\'' +
                ", messageType='" + messageType + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
