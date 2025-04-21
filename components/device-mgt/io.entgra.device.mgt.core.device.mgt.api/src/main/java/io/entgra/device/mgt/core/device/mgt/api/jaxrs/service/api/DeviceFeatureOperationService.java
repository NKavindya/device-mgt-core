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

package io.entgra.device.mgt.core.device.mgt.api.jaxrs.service.api;

import io.entgra.device.mgt.core.apimgt.annotations.Scope;
import io.entgra.device.mgt.core.apimgt.annotations.Scopes;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.beans.ErrorResponse;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.util.Constants;
import io.entgra.device.mgt.core.device.mgt.common.Feature;
import io.entgra.device.mgt.core.device.mgt.common.dto.DeviceFeatureInfo;
import io.swagger.annotations.*;

import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Device related REST-API. This can be used to manipulated device related details.
 */
@SwaggerDefinition(
        info = @Info(
                version = "1.0.0",
                title = "",
                extensions = {
                        @Extension(properties = {
                                @ExtensionProperty(name = "name", value = "DeviceFeatureManagement"),
                                @ExtensionProperty(name = "context", value = "/api/device-mgt/v1.0/devices"),
                        })
                }
        ),
        tags = {
                @Tag(name = "device_management", description = "")
        }
)
@Scopes(
        scopes = {
                @Scope(
                        name = "Getting Feature Details of a Device",
                        description = "Getting Feature Details of a Device",
                        key = "dm:devices:features:view",
                        roles = {"Internal/devicemgt-user"},
                        permissions = {"/device-mgt/devices/owning-device/features/view"}
                ),
        }
)
@Path("/deviceOperations")
@Api(value = "Device Management", description = "This API carries all device management related operations " +
        "such as get all the available devices, etc.")
public interface DeviceFeatureOperationService {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/device-type/{type}/features")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Getting Feature Details of a Device",
            notes = "WSO2 IoTS features enable you to carry out many operations based on the device platform. " +
                    "Using this REST API you can get the features that can be carried out on a preferred device type," +
                    " such as iOS, Android or Windows.",
            tags = "Device Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = Constants.SCOPE, value = "dm:devices:features:view")
                    })
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message = "OK. \n Successfully fetched the list of features.",
                            response = Feature.class,
                            responseContainer = "List",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Type",
                                            description = "The content type of the body"),
                                    @ResponseHeader(
                                            name = "ETag",
                                            description = "Entity Tag of the response resource.\n" +
                                                    "Used by caches, or in conditional requests."),
                                    @ResponseHeader(
                                            name = "Last-Modified",
                                            description = "Date and time the resource was last modified.\n" +
                                                    "Used by caches, or in conditional requests.")}),
                    @ApiResponse(
                            code = 303,
                            message = "See Other. \n " +
                                    "The source can be retrieved from the URL specified in the location header.",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Location",
                                            description = "The Source URL of the document.")}),
                    @ApiResponse(
                            code = 304,
                            message = "Not Modified. \n " +
                                    "Empty body because the client already has the latest version of the requested resource."),
                    @ApiResponse(
                            code = 400,
                            message = "Bad Request. \n Invalid request or validation error.",
                            response = ErrorResponse.class),
                    @ApiResponse(
                            code = 404,
                            message = "Not Found. \n The specified device can not be found.",
                            response = ErrorResponse.class),
                    @ApiResponse(
                            code = 406,
                            message = "Not Acceptable. \n The requested media type is not supported."),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. \n " +
                                    "Server error occurred while retrieving the feature list for the device platform.",
                            response = ErrorResponse.class)
            })
    Response getFeaturesOfDevice(
            @ApiParam(
                    name = "type",
                    value = "The device type name, such as ios, android, windows or fire-alarm.",
                    required = true)
            @PathParam("type")
            @Size(max = 45)
            String type,
            @ApiParam(
                    name = "If-Modified-Since",
                    value = "Checks if the requested variant was modified, since the specified date-time. \n" +
                            "Provide the value in the following format: EEE, d MMM yyyy HH:mm:ss Z. \n" +
                            "Example: Mon, 05 Jan 2014 15:10:00 +0200",
                    required = false)
            @HeaderParam("If-Modified-Since")
            String ifModifiedSince);
}
