package com.brihaspathee.zeus.web.resource.impl;

import com.brihaspathee.zeus.constants.ApiResponseConstants;
import com.brihaspathee.zeus.dto.account.RawTransactionDto;
import com.brihaspathee.zeus.dto.transaction.TransactionDto;
import com.brihaspathee.zeus.message.ZeusMessagePayload;
import com.brihaspathee.zeus.service.interfaces.DataTransformer;
import com.brihaspathee.zeus.web.model.DataTransformationDto;
import com.brihaspathee.zeus.web.resource.interfaces.TransactionAPI;
import com.brihaspathee.zeus.web.response.ZeusApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 18, March 2022
 * Time: 11:39 AM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.web.resource.impl
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class TransactionResource implements TransactionAPI {

    /**
     * Data transformer service instance to process the raw transaction
     */
    private final DataTransformer dataTransformer;

    /**
     * Get transaction by id
     * @param transactionId
     * @return
     */
    @Override
    public ResponseEntity<ZeusApiResponse<TransactionDto>> getTransactionById(String transactionId) {
        ZeusApiResponse<TransactionDto> apiResponse = ZeusApiResponse.<TransactionDto>builder()
                .response(TransactionDto.builder()
                        .ztcn("Test Transaction Id")
                        .zfcn("Test Transaction SK")
                        .build())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Process the raw transaction
     * @param rawTransactionDto
     * @param sendToTransactionManager
     * @return
     * @throws JsonProcessingException
     */
    @Override
    public ResponseEntity<ZeusApiResponse<DataTransformationDto>> processRawTransaction(
            RawTransactionDto rawTransactionDto,
            boolean sendToTransactionManager) throws JsonProcessingException {
        log.info("Send to Transaction Manager:{}", sendToTransactionManager);
        DataTransformationDto dataTransformationDto = dataTransformer.transformTransaction(rawTransactionDto, sendToTransactionManager);
        ZeusApiResponse<DataTransformationDto> apiResponse = ZeusApiResponse.<DataTransformationDto>builder()
                .reason(ApiResponseConstants.SUCCESS_REASON)
                .developerMessage(ApiResponseConstants.SUCCESS)
                .message(ApiResponseConstants.SUCCESS)
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .response(dataTransformationDto)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
