package com.brihaspathee.zeus.broker.consumer;

import com.brihaspathee.zeus.domain.entity.PayloadTrackerDetail;
import com.brihaspathee.zeus.helper.interfaces.PayloadTrackerDetailHelper;
import com.brihaspathee.zeus.helper.interfaces.PayloadTrackerHelper;
import com.brihaspathee.zeus.message.Acknowledgement;
import com.brihaspathee.zeus.message.ZeusMessagePayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 11, October 2022
 * Time: 4:26 PM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.broker.consumer
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionAcknowledgmentListener {

    /**
     * Object mapper instance to convert the JSON to object
     */
    private final ObjectMapper objectMapper;

    /**
     * To perform operations on the payload tracker
     */
    private final PayloadTrackerHelper payloadTrackerHelper;

    /**
     * To perform operations on the payload tracker detail
     */
    private final PayloadTrackerDetailHelper payloadTrackerDetailHelper;

    /**
     * kafka consumer to consume the acknowledgement messages
     * @param consumerRecord
     * @throws JsonProcessingException
     */
    @KafkaListener(topics = "ZEUS.TRANSACTION.PROCESSOR.ACK")
    public void listenForAcks(
            ConsumerRecord<String, ZeusMessagePayload<Acknowledgement>> consumerRecord
    ) throws JsonProcessingException {
        log.info("ACK received");
        String valueAsString = objectMapper.writeValueAsString(consumerRecord.value());
        ZeusMessagePayload<Acknowledgement> ackZeusMessagePayload =
                objectMapper.readValue(valueAsString,
                        new TypeReference<ZeusMessagePayload<Acknowledgement>>(){});
        createPayloadTrackerAckDetail(ackZeusMessagePayload);
        log.info("Request payload id:{}", ackZeusMessagePayload.getPayload().getRequestPayloadId());
        log.info("Ack id:{}",ackZeusMessagePayload.getPayload().getAckId());

    }

    /**
     * Log the details of the acknowledgment payload that was received
     * @param payload
     */
    private void createPayloadTrackerAckDetail(
            ZeusMessagePayload<Acknowledgement> payload) throws JsonProcessingException {
        String payloadAsString = objectMapper.writeValueAsString(payload);
        PayloadTrackerDetail payloadTrackerDetail = PayloadTrackerDetail.builder()
                .payloadTracker(payloadTrackerHelper.getPayloadTracker(payload.getPayload().getRequestPayloadId()))
                .responsePayload(payloadAsString)
                .responseTypeCode("ACKNOWLEDGEMENT")
                .responsePayloadId(payload.getPayload().getAckId())
                .payloadDirectionTypeCode("INBOUND")
                .sourceDestinations(payload.getMessageMetadata().getMessageSource())
                .build();
        payloadTrackerDetailHelper.createPayloadTrackerDetail(payloadTrackerDetail);
    }
}
