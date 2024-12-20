/*
 * Copyright (c) 2018 - 2023, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.entgra.device.mgt.core.device.mgt.api.jaxrs.service.api;

import io.entgra.device.mgt.core.apimgt.annotations.Scope;
import io.entgra.device.mgt.core.apimgt.annotations.Scopes;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.beans.ErrorResponse;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.beans.analytics.Adapter;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.beans.analytics.EventStream;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.beans.analytics.SiddhiExecutionPlan;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.util.Constants;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@SwaggerDefinition(
        info = @Info(
                version = "1.0.0",
                title = "",
                extensions = {
                        @Extension(properties = {
                                @ExtensionProperty(name = "name", value = "AnalyticsArtifactsManagement"),
                                @ExtensionProperty(name = "context", value = "/api/device-mgt/v1.0/analytics/artifacts"),
                        })
                }
        ),
        tags = {
                @Tag(name = "analytics_artifacts_management", description = "")
        }
)
@Scopes(
        scopes = {
                @Scope(
                        name = "Create Event Stream Artifact",
                        description = "Create Event Stream Artifact",
                        key = "dm:an:artifacts:stream:add",
                        roles = {"Internal/devicemgt-user"},
                        permissions = {"/device-mgt/analytics/artifacts/stream/add"}),
                @Scope(
                        name = "Delete Stream Artifact",
                        description = "Delete Stream Artifact",
                        key = "dm:an:artifacts:stream:delete",
                        roles = {"Internal/devicemgt-user"},
                        permissions = {"/device-mgt/analytics/artifacts/stream/delete"}),
                @Scope(
                        name = "Create Event Receiver Artifact",
                        description = "Create Event Receiver Artifact",
                        key = "dm:an:artifacts:rcv:add",
                        roles = {"Internal/devicemgt-user"},
                        permissions = {"/device-mgt/analytics/artifacts/receiver/add"}),
                @Scope(
                        name = "Delete Receiver Artifact",
                        description = "Delete Receiver Artifact",
                        key = "dm:an:artifacts:rcv:delete",
                        roles = {"Internal/devicemgt-user"},
                        permissions = {"/device-mgt/analytics/artifacts/receiver/delete"}),
                @Scope(
                        name = "Create Event Publisher Artifact",
                        description = "Create Event Publisher Artifact",
                        key = "dm:an:artifacts:pub:add",
                        roles = {"Internal/devicemgt-user"},
                        permissions = {"/device-mgt/analytics/artifacts/publisher/add"}),
                @Scope(
                        name = "Delete Publisher Artifact",
                        description = "Delete Publisher Artifact",
                        key = "dm:an:artifacts:pub:delete",
                        roles = {"Internal/devicemgt-user"},
                        permissions = {"/device-mgt/analytics/artifacts/publisher/delete"}),
                @Scope(
                        name = "Create Siddhi Script Artifact",
                        description = "Create Siddhi Script Artifact",
                        key = "dm:an:artifacts:siddhi:add",
                        roles = {"Internal/devicemgt-user"},
                        permissions = {"/device-mgt/analytics/artifacts/siddhi-script/add"}),
                @Scope(
                        name = "Delete Siddhi Script Artifact",
                        description = "Delete Siddhi Script Artifact",
                        key = "dm:an:artifacts:siddhi:delete",
                        roles = {"Internal/devicemgt-user"},
                        permissions = {"/device-mgt/analytics/artifacts/siddhi-script/delete"})
        }
)

@Api(
        value = "Analytics Artifacts Management",
        description = "This API corresponds to services related to Analytics Artifacts management"
)
@Path("/analytics/artifacts")
public interface AnalyticsArtifactsManagementService {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/stream/{id}")
    @ApiOperation(
            httpMethod = "POST",
            value = "Create Event Stream Artifact through a String argument.",
            notes = "Deploy a Json Stream Artifact in Analytics server.",
            tags = "Analytics Artifacts Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(
                                    name = Constants.SCOPE,
                                    value = "dm:an:artifacts:stream:add"
                            )})
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message = "OK. \n Successfully deployed the Stream Artifact.",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Type",
                                            description = "The content type of the body")
                            }
                    ),
                    @ApiResponse(
                            code = 400,
                            message = "Bad Request. \n The resource to add already exists."),
                    @ApiResponse(
                            code = 404,
                            message = "Not Found. \n The resource to edit not available."),
                    @ApiResponse(
                            code = 406,
                            message = "Not Acceptable. \n The requested media type is not supported."),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. \n Server error occurred while " +
                                      "deploying the Stream Artifact.",
                            response = ErrorResponse.class)
            }
    )
    Response deployEventDefinitionAsString(
            @ApiParam(
                    name = "id",
                    value = "Stream id(name:version).")
            @PathParam("id") String id,
            @ApiParam(
                    name = "isEdited",
                    value = "This stream is being edited or created.")
            @QueryParam("isEdited") boolean isEdited,
            @ApiParam(
                    name = "stream",
                    value = "Add the data to complete the EventStream object.",
                    required = true)
            @Valid EventStream stream);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/stream")
    @ApiOperation(
            httpMethod = "POST",
            value = "Create Event Stream Artifact through a DTO class.",
            notes = "Deploy a Json Stream Artifact in Analytics server.",
            tags = "Analytics Artifacts Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(
                                    name = Constants.SCOPE,
                                    value = "dm:an:artifacts:stream:add"
                            )})
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message = "OK. \n Successfully deployed the Stream Artifact.",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Type",
                                            description = "The content type of the body")
                            }
                    ),
                    @ApiResponse(
                            code = 400,
                            message = "Bad Request. \n The payload is invalid."),
                    @ApiResponse(
                            code = 406,
                            message = "Not Acceptable. \n The requested media type is not supported."),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. \n Server error occurred while deploying the " +
                                      "Stream Artifact.",
                            response = ErrorResponse.class)
            }
    )
    Response deployEventDefinitionAsDto(
            @ApiParam(
                    name = "stream",
                    value = "Add the data to complete the EventStream object.",
                    required = true)
            @Valid EventStream stream);

    @DELETE
    @Path("/stream/{name}/{version}")
    @ApiOperation(
            httpMethod = "DELETE",
            value = "Delete the Stream with id as {name}:{version}",
            notes = "Use this api to delete an already deployed Stream",
            tags = "Analytics Artifacts Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(
                                    name = Constants.SCOPE,
                                    value = "dm:an:artifacts:stream:delete"
                            )})
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message = "OK. \n Successfully deleted the Stream Artifact.",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Type",
                                            description = "The content type of the body")
                            }
                    ),
                    @ApiResponse(
                            code = 404,
                            message = "Not Found. \n The resource to delete not available."),
                    @ApiResponse(
                            code = 406,
                            message = "Not Acceptable. \n The requested media type is not supported."),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. \nServer error occurred while " +
                                      "deleting the Stream Artifact.",
                            response = ErrorResponse.class)
            }
    )
    Response deleteStream(
            @ApiParam(
                    name = "name",
                    value = "Stream name.",
                    required = true)
            @PathParam("name") String name,
            @ApiParam(
                    name = "version",
                    value = "Stream version.",
                    required = true)
            @PathParam("version") String version
    );

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/receiver/{name}")
    @ApiOperation(
            httpMethod = "POST",
            value = "Create Event Receiver Artifact through a String argument.",
            notes = "Deploy a XML Event Receiver Artifact in Analytics server.",
            tags = "Analytics Artifacts Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(
                                    name = Constants.SCOPE,
                                    value = "dm:an:artifacts:rcv:add"
                            )})
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message = "OK. \n Successfully deployed the Receiver Artifact.",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Type",
                                            description = "The content type of the body.")
                            }
                    ),
                    @ApiResponse(
                            code = 406,
                            message = "Not Acceptable. \n The requested media type is not supported."),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. \n Server error occurred while deploying the " +
                                      "Event Receiver Artifact.",
                            response = ErrorResponse.class)
            }
    )
    Response deployEventReceiverAsString(
            @ApiParam(
                    name = "name",
                    value = "Receiver name.")
            @PathParam("name") String name,
            @ApiParam(
                    name = "isEdited",
                    value = "This stream is being edited or created.")
            @QueryParam("isEdited") boolean isEdited,
            @ApiParam(
                    name = "receiver",
                    value = "Add the data to complete the EventReceiver object.",
                    required = true)
            @Valid Adapter receiver);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/receiver")
    @ApiOperation(
            httpMethod = "POST",
            value = "Create Event Receiver Artifact through a DTO class.",
            notes = "Deploy a JSON Event Receiver Artifact in Analytics server.",
            tags = "Analytics Artifacts Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(
                                    name = Constants.SCOPE,
                                    value = "dm:an:artifacts:rcv:add"
                            )})
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message = "OK. \n Successfully deployed the Receiver Artifact.",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Type",
                                            description = "The content type of the body.")
                            }
                    ),
                    @ApiResponse(
                            code = 400,
                            message =
                                    "Bad Request. \n The payload is invalid."),
                    @ApiResponse(
                            code = 406,
                            message = "Not Acceptable. \n The requested media type is not supported."),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. \n Server error occurred while deploying the " +
                                      "Event Receiver Artifact.",
                            response = ErrorResponse.class)
            }
    )
    Response deployEventReceiverAsDto(
            @ApiParam(
                    name = "receiver",
                    value = "Add the data to complete the Adapter object.",
                    required = true)
            @Valid Adapter receiver);

    @DELETE
    @Path("/receiver/{name}")
    @ApiOperation(
            httpMethod = "DELETE",
            value = "Delete a Receiver with the given name",
            notes = "Use this api to delete an already deployed active or inactive Receiver",
            tags = "Analytics Artifacts Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(
                                    name = Constants.SCOPE,
                                    value = "dm:an:artifacts:rcv:delete"
                            )})
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message = "OK. \n Successfully deleted the Receiver Artifact.",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Type",
                                            description = "The content type of the body"
                                    )
                            }
                    ),
                    @ApiResponse(
                            code = 406,
                            message = "Not Acceptable. \n The requested media type is not supported."
                    ),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error.\n Server error occurred while " +
                                      "deleting the Receiver Artifact.",
                            response = ErrorResponse.class
                    )
            }
    )
    Response deleteReceiver(
            @ApiParam(
                    name = "name",
                    value = "Receiver name.",
                    required = true)
            @PathParam("name") String name);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/publisher/{name}")
    @ApiOperation(
            httpMethod = "POST",
            value = "Create Event Publisher Artifact through a String argument.",
            notes = "Deploy a XML Event Publisher Artifact in Analytics server.",
            tags = "Analytics Artifacts Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(
                                    name = Constants.SCOPE,
                                    value = "dm:an:artifacts:pub:add"
                            )})
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message = "OK. \n Successfully deployed the Publisher Artifact.",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Type",
                                            description = "The content type of the body.")
                            }
                    ),
                    @ApiResponse(
                            code = 406,
                            message = "Not Acceptable. \n The requested media type is not supported."),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. \n Server error occurred while deploying the " +
                                      "Event Publisher Artifact.",
                            response = ErrorResponse.class)
            }
    )
    Response deployEventPublisherAsString(
            @ApiParam(
                    name = "name",
                    value = "Publisher name.")
            @PathParam("name") String name,
            @ApiParam(
                    name = "isEdited",
                    value = "This stream is being edited or created.")
            @QueryParam("isEdited") boolean isEdited,
            @ApiParam(
                    name = "publisher",
                    value = "Add the data to complete the EventPublisher object.",
                    required = true)
            @Valid Adapter publisher);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/publisher")
    @ApiOperation(
            httpMethod = "POST",
            value = "Create Event Publisher Artifact through a DTO class.",
            notes = "Deploy a JSON Event Publisher Artifact in Analytics server.",
            tags = "Analytics Artifacts Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(
                                    name = Constants.SCOPE,
                                    value = "dm:an:artifacts:pub:add"
                            )})
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message = "OK. \n Successfully deployed the Publisher Artifact.",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Type",
                                            description = "The content type of the body.")
                            }
                    ),
                    @ApiResponse(
                            code = 400,
                            message =
                                    "Bad Request."),
                    @ApiResponse(
                            code = 406,
                            message = "Not Acceptable. \n The requested media type is not supported."),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. \n Server error occurred while deploying the " +
                                      "Event Publisher Artifact.",
                            response = ErrorResponse.class)
            }
    )
    Response deployEventPublisherAsDto(
            @ApiParam(
                    name = "publisher",
                    value = "Add the data to complete the Adapter object.",
                    required = true)
            @Valid Adapter publisher);

    @DELETE
    @Path("/publisher/{name}")
    @ApiOperation(
            httpMethod = "DELETE",
            value = "Delete a Publisher with the given name",
            notes = "Use this api to delete an already deployed active or inactive Publisher",
            tags = "Analytics Artifacts Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(
                                    name = Constants.SCOPE,
                                    value = "dm:an:artifacts:pub:delete"
                            )})
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message = "OK. \n Successfully deleted the Publisher Artifact.",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Type",
                                            description = "The content type of the body")
                            }
                    ),
                    @ApiResponse(
                            code = 406,
                            message = "Not Acceptable. \n The requested media type is not supported."),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. \n Server error occurred while " +
                                      "deleting the Publisher Artifact.",
                            response = ErrorResponse.class)
            }
    )
    Response deletePublisher(
            @ApiParam(
                    name = "name",
                    value = "Publisher name.",
                    required = true)
            @PathParam("name") String name);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/siddhi-script/{name}")
    @ApiOperation(
            httpMethod = "POST",
            value = "Create Siddhi Script Artifact through a String argument.",
            notes = "Deploy a SiddhiQL script Artifact in Analytics server.",
            tags = "Analytics Artifacts Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(
                                    name = Constants.SCOPE,
                                    value = "dm:an:artifacts:siddhi:add"
                            )})
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message = "OK. \n Successfully deployed the Siddhi script Artifact.",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Type",
                                            description = "The content type of the body.")
                            }
                    ),
                    @ApiResponse(
                            code = 406,
                            message = "Not Acceptable. \n The requested media type is not supported."),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. \n Server error occurred while " +
                                      "deploying the Siddhi script Artifact.",
                            response = ErrorResponse.class)
            }
    )
    Response deploySiddhiExecutableScript(
            @ApiParam(
                    name = "name",
                    value = "Siddhi Executable Script name.")
            @PathParam("name") String name,
            @ApiParam(
                    name = "isEdited",
                    value = "This stream is being edited or created.")
            @QueryParam("isEdited") boolean isEdited,
            @ApiParam(
                    name = "plan",
                    value = "Add the data to complete the SiddhiExecutionPlan object.",
                    required = true)
            @Valid SiddhiExecutionPlan plan);

    @DELETE
    @Path("/siddhi-script/{name}")
    @ApiOperation(
            httpMethod = "DELETE",
            value = "Delete an already deployed Siddhi script",
            notes = "Use this api to delete an already deployed active or inactive Siddhi script",
            tags = "Analytics Artifacts Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(
                                    name = Constants.SCOPE,
                                    value = "dm:an:artifacts:siddhi:delete"
                            )
                    })
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message = "OK. \n Successfully deleted the Siddhi script Artifact.",
                            responseHeaders = {
                                    @ResponseHeader(
                                            name = "Content-Type",
                                            description = "The content type of the body")
                            }
                    ),
                    @ApiResponse(
                            code = 406,
                            message = "Not Acceptable. \n The requested media type is not supported."),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error.\n Server error occurred while " +
                                      "deleting the Siddhi script Artifact.",
                            response = ErrorResponse.class)
            }
    )
    Response deleteSiddhiScript(
            @ApiParam(
                    name = "name",
                    value = "Siddhi script name.",
                    required = true)
            @PathParam("name") String name);
}
