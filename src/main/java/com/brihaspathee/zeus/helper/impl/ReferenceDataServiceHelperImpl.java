package com.brihaspathee.zeus.helper.impl;

import com.brihaspathee.zeus.helper.interfaces.ReferenceDataServiceHelper;
import com.brihaspathee.zeus.reference.data.model.XWalkRequest;
import com.brihaspathee.zeus.reference.data.model.XWalkResponse;
import com.brihaspathee.zeus.web.response.ZeusApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
     * Webclient instance to call the reference data service
     */
    private final WebClient webClient;


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
