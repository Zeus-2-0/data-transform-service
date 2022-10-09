package com.brihaspathee.zeus.web.resource.impl;

import com.brihaspathee.zeus.web.model.TransactionDto;
import com.brihaspathee.zeus.web.resource.interfaces.TransactionAPI;
import com.brihaspathee.zeus.web.response.ZeusApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

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
}
