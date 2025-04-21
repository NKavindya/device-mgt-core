/*
 *  Copyright (c) 2018 - 2025, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package io.entgra.device.mgt.core.device.mgt.api.jaxrs.service.impl;

import io.entgra.device.mgt.core.device.mgt.api.jaxrs.beans.ErrorResponse;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.service.api.DeviceFeatureOperationService;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.util.DeviceMgtAPIUtils;
import io.entgra.device.mgt.core.device.mgt.common.Feature;
import io.entgra.device.mgt.core.device.mgt.common.FeatureManager;
import io.entgra.device.mgt.core.device.mgt.common.dto.DeviceFeatureInfo;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.DeviceFeatureOperationException;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.DeviceManagementException;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.DeviceTypeNotFoundException;
import io.entgra.device.mgt.core.device.mgt.core.service.DeviceFeatureOperations;
import io.entgra.device.mgt.core.device.mgt.core.service.DeviceFeatureOperationsImpl;
import io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/deviceOperations")
public class DeviceFeatureOperationServiceImpl implements DeviceFeatureOperationService {
    private static final Log log = LogFactory.getLog(DeviceFeatureOperationServiceImpl.class);

    @GET
    @Path("/device-type/{type}/features")
    @Override
    public Response getFeaturesOfDevice(
            @PathParam("type") @Size(max = 45) String type,
            @HeaderParam("If-Modified-Since") String ifModifiedSince) {
        List<Feature> features = new ArrayList<>();
        DeviceManagementProviderService dms;
        try {
            dms = DeviceMgtAPIUtils.getDeviceManagementService();
            FeatureManager fm;
            try {
                fm = dms.getFeatureManager(type);
            } catch (DeviceTypeNotFoundException e) {
                String msg = "No device type found with name : " + type ;
                return Response.status(Response.Status.NOT_FOUND).entity(msg).build();
            }
            if (fm != null) {
                features = fm.getFeatures();
            }
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while retrieving the list of features of '" + type + "'";
            log.error(msg, e);
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(msg).build()).build();
        }
        return Response.status(Response.Status.OK).entity(features).build();
    }
}

