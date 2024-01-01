package com.brihaspathee.zeus.helper.impl;

import com.brihaspathee.zeus.dto.account.RawTransactionDto;
import com.brihaspathee.zeus.dto.transaction.TransactionBrokerDto;
import com.brihaspathee.zeus.edi.models.enrollment.Loop1000C;
import com.brihaspathee.zeus.helper.interfaces.TransactionBrokerHelper;
import com.brihaspathee.zeus.web.model.DataTransformationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 25, October 2022
 * Time: 2:10 PM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.helper.impl
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionBrokerHelperImpl implements TransactionBrokerHelper {

    /**
     * Building the transaction broker
     * @param dataTransformationDto
     * @param rawTransactionDto
     * @param transactionReceivedDate
     */
    @Override
    public void buildTransactionBroker(DataTransformationDto dataTransformationDto,
                                       RawTransactionDto rawTransactionDto,
                                       LocalDateTime transactionReceivedDate) {

        Set<Loop1000C> brokers = rawTransactionDto.getTransaction().getBrokers();
        // check if there are any brokers present in the transaction
        if(brokers !=null && !brokers.isEmpty()){
            TransactionBrokerDto brokerDto = TransactionBrokerDto.builder().build();
            brokers.stream().forEach(broker -> {
                String brokerType = broker.getBroker().getN101();
                // check if the broker type is agent or agency
                if (brokerType.equals("BO")) {
                    brokerDto.setBrokerName(broker.getBroker().getN102());
                    brokerDto.setBrokerId(broker.getBroker().getN104());
                    if (broker.getAccountDetails() != null){
                        brokerDto.setAccountNumber1(
                                broker.getAccountDetails()
                                        .getBrokerAccount().getAct01());
                        brokerDto.setAccountNumber2(
                                broker.getAccountDetails()
                                        .getBrokerAccount().getAct06());
                }
            }else{
                    brokerDto.setAgencyName(broker.getBroker().getN102());
                    brokerDto.setAgencyId(broker.getBroker().getN104());
                }
            });
            brokerDto.setReceivedDate(transactionReceivedDate);
            dataTransformationDto.getTransactionDto().setBroker(brokerDto);
        }

    }
}
