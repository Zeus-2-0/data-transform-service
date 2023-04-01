package com.brihaspathee.zeus.helper.impl;

import com.brihaspathee.zeus.helper.interfaces.ReferenceDataServiceHelper;
import com.brihaspathee.zeus.reference.data.lookup.ReferenceDataLookupHelper;
import com.brihaspathee.zeus.reference.data.model.XWalkRequest;
import com.brihaspathee.zeus.reference.data.model.XWalkResponse;
import com.brihaspathee.zeus.web.response.ZeusApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 21, October 2022
 * Time: 5:23 AM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.helper.impl
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReferenceDataServiceHelperImpl implements ReferenceDataServiceHelper {

    /**
     * URL for the reference data service
     */
    @Value("${url.host.ref-data}")
    private String refDataHost;

    /**
     * Environment variable to know if this is test environment or not
     */
    private final Environment environment;

    /**
     * Webclient instance to call the reference data service
     */
    private final WebClient webClient;

    /**
     * This instance will be used to lookup ref data during testing
     */
    private final ReferenceDataLookupHelper referenceDataLookupHelper;


    /**
     * Get the internal ref data for the corresponding external ref data
     * @param listCode
     * @param listTypeName
     * @param externalSourceName
     * @return
     */
    @Override
    public XWalkResponse getInternalRefData(String listCode, String listTypeName, String externalSourceName) {
        XWalkRequest xWalkRequest = XWalkRequest.builder()
                .listCode(listCode)
                .listTypeName(listTypeName)
                .externalSourceName(externalSourceName)
                .build();
        //log.info("Active Profiles:{}", environment.getActiveProfiles());

        if(Arrays.stream(environment.getActiveProfiles()).anyMatch("test"::equals)){
            log.info("Looking up for {} in ref-data-properties file:", xWalkRequest.getListCode() + "." + xWalkRequest.getListTypeName() + "." + xWalkRequest.getExternalSourceName());
            log.info("Look up response:{}",referenceDataLookupHelper.getRefData(xWalkRequest));
            return referenceDataLookupHelper.getRefData(xWalkRequest);
        }else{
            String host = refDataHost + "x-walk/internal";
            ZeusApiResponse<XWalkResponse> apiResponse = webClient.post()
                    .uri(host)
                    .body(Mono.just(xWalkRequest), XWalkRequest.class)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ZeusApiResponse<XWalkResponse>>() {})
                    .block();
            return apiResponse.getResponse();
        }

    }
}
