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

package io.entgra.device.mgt.core.notification.mgt.api.service;

import io.entgra.device.mgt.core.apimgt.annotations.Scope;
import io.entgra.device.mgt.core.apimgt.annotations.Scopes;
import io.swagger.annotations.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Notifications related REST-API.
 */
@SwaggerDefinition(
        info = @Info(
                version = "1.0.0",
                title = "NotificationService",
                extensions = {
                        @Extension(properties = {
                                @ExtensionProperty(name = "name", value = "NotificationManagement"),
                                @ExtensionProperty(name = "context", value = "/api/notification-mgt/v1.0/notifications"),
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
                        name = "Getting All Notifications",
                        description = "Getting All Notification Details",
                        key = "dm:notifications:view",
                        roles = {"Internal/devicemgt-user"},
                        permissions = {"/device-mgt/notifications/view"}
                ),
                @Scope(
                        name = "Updating the Notification",
                        description = "Updating the Notifications",
                        key = "dm:notif:mark-checked",
                        roles = {"Internal/devicemgt-user"},
                        permissions = {"/device-mgt/notifications/update"}
                )
        }
)
@Api(value = "Notification Management", description = "Notification Management related operations can be found here.")
@Path("/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface NotificationService {
    String SCOPE = "scope";
    @GET
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Getting All Notification Details",
            notes = "Get the details of all the notifications that were pushed to the devices registered with WSO2 EMM using this REST API.",
            tags = "Notification Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "dm:notifications:view")
                    })
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message = "OK. \n  Successfully retrieved the Notifications",
                            response = Response.class),
                    @ApiResponse(
                            code = 400,
                            message = "Bad Request. \n Invalid request or validation error.",
                            response = Response.class),
                    @ApiResponse(
                            code = 404,
                            message = "Not Found. \n The specified resource does not exist.",
                            response = Response.class),
                    @ApiResponse(
                            code = 409,
                            message = "Conflict. \n  Notifications already exists.",
                            response = Response.class),
                    @ApiResponse(
                            code = 415,
                            message = "Unsupported media type. \n The entity of the request was in a not supported format.",
                            response = Response.class),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. \n Server error occurred while creating the resource.",
                            response = Response.class)
            })
    Response getLatestNotifications(
            @ApiParam(
                    name = "offset",
                    value = "The starting pagination index for the complete list of qualified items.",
                    required = false,
                    defaultValue = "0")
            @QueryParam("offset")
            int offset,
            @ApiParam(
                    name = "limit",
                    value = "Provide how many notification details you require from the starting pagination index/offset.",
                    required = false,
                    defaultValue = "5")
            @QueryParam("limit")
            int limit);

    @GET
    @Path("/user")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Get Notifications With User Read/Unread Status",
            notes = "Retrieve notifications for a specific user with their read/unread status.",
            tags = "Notification Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "dm:notifications:view")
                    })
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message = "OK. Successfully retrieved notifications with status.",
                            response = Response.class),
                    @ApiResponse(
                            code = 400,
                            message = "Bad Request. Invalid request or missing parameters.",
                            response = Response.class),
                    @ApiResponse(
                            code = 404,
                            message = "Not Found. No notifications found for the user.",
                            response = Response.class),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. An error occurred while retrieving notifications.",
                            response = Response.class)
            }
    )
    Response getUserNotificationsWithStatus(
            @ApiParam(
                    name = "username",
                    value = "Username to retrieve notifications for",
                    required = true)
            @QueryParam("username")
            String username,
            @ApiParam(
                    name = "limit",
                    value = "Maximum number of results to return",
                    required = false,
                    defaultValue = "10")
            @QueryParam("limit")
            int limit,
            @ApiParam(
                    name = "offset",
                    value = "Starting index for result pagination",
                    required = false,
                    defaultValue = "0")
            @QueryParam("offset")
            int offset);

    @PUT
    @Path("/mark-read")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "PUT",
            value = "Mark Notification as Read",
            notes = "Mark a specific notification as read for a given user.",
            tags = "Notification Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "dm:notif:mark-checked")
                    })
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message = "OK. Successfully marked notification as read.",
                            response = Response.class),
                    @ApiResponse(
                            code = 400,
                            message = "Bad Request. Missing or invalid parameters.",
                            response = Response.class),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. Failed to mark notification as read.",
                            response = Response.class)
            }
    )
    Response markNotificationAsRead(
            @ApiParam(
                    name = "notificationId",
                    value = "Notification ID to mark as read",
                    required = true)
            @QueryParam("notificationId")
            int notificationId,
            @ApiParam(
                    name = "username",
                    value = "Username for whom the notification should be marked as read",
                    required = true)
            @QueryParam("username")
            String username);
}
