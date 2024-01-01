package com.brihaspathee.zeus.util;

import com.brihaspathee.zeus.edi.models.common.REF;
import com.brihaspathee.zeus.edi.models.enrollment.Loop2000;
import com.brihaspathee.zeus.test.TestAccountEntityCodes;
import com.brihaspathee.zeus.test.TestMemberEntityCodes;
import com.brihaspathee.zeus.test.ZeusTransactionControlNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 26, December 2023
 * Time: 6:09 AM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.util
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataTransformerUtil {

    /**
     * The spring environment instance
     */
    private final Environment environment;

    /**
     * Populate the entity codes if they are available in the transaction
     * @param zeusTransactionControlNumber
     * @return
     */
    public Map<String, List<String>> getAccountEntityCodes(ZeusTransactionControlNumber zeusTransactionControlNumber){
        if(zeusTransactionControlNumber == null ||
                zeusTransactionControlNumber.getAccountEntityCodes() == null){
            return null;
        }
        return zeusTransactionControlNumber.getAccountEntityCodes().getEntityCodes();
    }

    /**
     * Return the list of member entity codes
     * @param zeusTransactionControlNumber
     * @return
     */
    public List<TestMemberEntityCodes> getMemberEntityCodes(ZeusTransactionControlNumber zeusTransactionControlNumber){
        if(zeusTransactionControlNumber == null ||
                zeusTransactionControlNumber.getAccountEntityCodes() == null){
            return null;
        }
        return zeusTransactionControlNumber.getAccountEntityCodes().getMemberEntityCodes();
    }

    /**
     * Get the member entity codes of the member
     * @param testMemberEntityCodes
     * @param member
     * @return
     */
    public Map<String, List<String>> getMemberEntityCodes(List<TestMemberEntityCodes> testMemberEntityCodes,
                                                            Loop2000 member){
        if(testMemberEntityCodes == null){
            return null;
        }
        String exchangeMemberId = getExchangeMemberId(member);
        if (exchangeMemberId == null){
            return null;
        }else{
            Optional<TestMemberEntityCodes> memberEntityCodes = testMemberEntityCodes.stream()
                    .filter(entityCodes ->
                            entityCodes.getExchangeMemberId().equals(exchangeMemberId)).findFirst();
            return memberEntityCodes.map(TestMemberEntityCodes::getEntityCodes).orElse(null);
        }
    }

    /**
     * Get the exchange member id of the member
     * @param member
     * @return
     */
    private String getExchangeMemberId(Loop2000 member){
        Optional<REF> exchangeMemberId =
                member.getMemberSupplementalIdentifiers().stream().filter(
                        identifier -> identifier.getRef01().equals("17")).findFirst();
        return exchangeMemberId.map(REF::getRef02).orElse(null);
    }
}
