package com.brihaspathee.zeus.helper.interfaces;

import com.brihaspathee.zeus.domain.entity.PayloadTracker;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 11, October 2022
 * Time: 6:34 AM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.helper.interfaces
 * To change this template use File | Settings | File and Code Template
 */
public interface PayloadTrackerHelper {

    /**
     * Create the payload tracker record
     * @param payloadTracker
     * @return
     */
    PayloadTracker createPayloadTracker(PayloadTracker payloadTracker);

    /**
     * Get the payload tracker instance using payload id
     * @param payloadId
     * @return
     */
    PayloadTracker getPayloadTracker(String payloadId);

    /**
     * Clean up the payload tracker
     */
    void deleteAll();
}
