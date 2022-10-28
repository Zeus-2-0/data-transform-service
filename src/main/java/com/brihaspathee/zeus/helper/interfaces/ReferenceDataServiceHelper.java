package com.brihaspathee.zeus.helper.interfaces;

import com.brihaspathee.zeus.reference.data.model.XWalkRequest;
import com.brihaspathee.zeus.reference.data.model.XWalkResponse;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 21, October 2022
 * Time: 4:44 AM
 * Project: Zeus
 * Package Name: com.brihaspathee.zeus.helper.interfaces
 * To change this template use File | Settings | File and Code Template
 */
public interface ReferenceDataServiceHelper {

    /**
     * Get the internal ref data for the corresponding external ref data
     * @param listCode
     * @param listTypeName
     * @param externalSourceName
     * @return
     */
    XWalkResponse getInternalRefData(String listCode, String listTypeName, String externalSourceName);
}
