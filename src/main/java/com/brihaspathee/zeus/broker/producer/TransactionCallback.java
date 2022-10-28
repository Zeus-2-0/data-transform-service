package com.brihaspathee.zeus.broker.producer;

import com.brihaspathee.zeus.dto.transaction.TransactionDto;
import com.brihaspathee.zeus.message.ZeusMessagePayload;
import com.brihaspathee.zeus.web.model.DataTransformationDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 09, October 2022
 * Time: 6:44 AM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.broker.producer
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@Getter
@Setter
@Component
@RequiredArgsConstructor
public class TransactionCallback implements
        ListenableFutureCallback<SendResult<String, ZeusMessagePayload<DataTransformationDto>>> {

    /**
     * The data transformation dto that was sent in the payload
     */
    private DataTransformationDto transactionDto;

    /**
     * Invoked when the message publishing is failed
     * @param ex
     */
    @Override
    public void onFailure(Throwable ex) {
        log.info("The message failed to publish");
    }

    /**
     * Invoked when the message publishing was successful
     * @param result
     */
    @Override
    public void onSuccess(SendResult<String, ZeusMessagePayload<DataTransformationDto>> result) {
        log.info("The message successfully published");
    }
}
