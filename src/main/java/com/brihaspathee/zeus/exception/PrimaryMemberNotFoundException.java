package com.brihaspathee.zeus.exception;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 19, October 2022
 * Time: 4:13 PM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.exception
 * To change this template use File | Settings | File and Code Template
 */
public class PrimaryMemberNotFoundException extends RuntimeException{

    public PrimaryMemberNotFoundException(String message){
        super(message);
    }

    public PrimaryMemberNotFoundException(String message, Throwable cause){
        super(message, cause);
    }
}
