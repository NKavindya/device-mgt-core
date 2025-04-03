package io.entgra.device.mgt.core.device.mgt.api.jaxrs.service.api;

import io.entgra.device.mgt.core.apimgt.annotations.Scope;
import io.entgra.device.mgt.core.apimgt.annotations.Scopes;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.beans.ErrorResponse;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.util.Constants;
import io.entgra.device.mgt.core.device.mgt.common.operation.mgt.Operation;
import io.swagger.annotations.*;

import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Device related REST-API. This can be used to manipulated device related details.
 */
@SwaggerDefinition(
        info = @Info(
                version = "1.0.0",
                title = "",
                extensions = {
                        @Extension(properties = {
                                @ExtensionProperty(name = "name", value = "DeviceManagement"),
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
                @io.entgra.device.mgt.core.apimgt.annotations.Scope(
                        name = "Getting Details of Registered Devices",
                        description = "Getting Details of Registered Devices",
                        key = "dm:devices:view",
                        roles = {"Internal/devicemgt-user"},
                        permissions = {"/device-mgt/devices/owning-device/view"}
                ),
                @io.entgra.device.mgt.core.apimgt.annotations.Scope(
                        name = "Getting Details of a Device",
                        description = "Getting Details of a Device",
                        key = "dm:devices:details",
                        roles = {"Internal/devicemgt-user"},
                        permissions = {"/device-mgt/devices/owning-device/details/view"}
                ),
                @io.entgra.device.mgt.core.apimgt.annotations.Scope(
                        name = "Update the device specified by device id",
                        description = "Update the device specified by device id",
                        key = "dm:devices:update",
                        roles = {"Internal/devicemgt-user"},
                        permissions = {"/device-mgt/devices/owning-device/update"}
                ),
                @io.entgra.device.mgt.core.apimgt.annotations.Scope(
                        name = "Delete the device specified by device id",
                        description = "Delete the device specified by device id",
                        key = "dm:devices:delete",
                        roles = {"Internal/devicemgt-user"},
                        permissions = {"/device-mgt/devices/owning-device/delete"}
                ),
                @io.entgra.device.mgt.core.apimgt.annotations.Scope(
                        name = "Getting Feature Details of a Device",
                        description = "Getting Feature Details of a Device",
                        key = "dm:devices:features:view",
                        roles = {"Internal/devicemgt-user"},
                        permissions = {"/device-mgt/devices/owning-device/features/view"}
                ),
                @io.entgra.device.mgt.core.apimgt.annotations.Scope(
                        name = "Advanced Search for Devices",
                        description = "Advanced Search for Devices",
                        key = "dm:devices:search",
                        roles = {"Internal/devicemgt-user"},
                        permissions = {"/device-mgt/devices/owning-device/search"}
                ),
                @io.entgra.device.mgt.core.apimgt.annotations.Scope(
                        name = "Getting Installed Application Details of a Device",
                        description = "Getting Installed Application Details of a Device",
                        key = "dm:devices:app:view",
                        roles = {"Internal/devicemgt-user"},
                        permissions = {"/device-mgt/devices/owning-device/apps/view"}
                ),
                @io.entgra.device.mgt.core.apimgt.annotations.Scope(
                        name = "Getting Device Operation Details",
                        description = "Getting Device Operation Details",
                        key = "dm:devices:ops:view",
                        roles = {"Internal/devicemgt-user"},
                        permissions = {"/device-mgt/devices/owning-device/operations/view"}
                ),
                @io.entgra.device.mgt.core.apimgt.annotations.Scope(
                        name = "Get the details of the policy that is enforced on a device.",
                        description = "Get the details of the policy that is enforced on a device.",
                        key = "dm:devices:policy:view",
                        roles = {"Internal/devicemgt-user"},
                        permissions = {"/device-mgt/devices/owning-device/policies/view"}
                ),
                @io.entgra.device.mgt.core.apimgt.annotations.Scope(
                        name = "Getting Policy Compliance Details of a Device",
                        description = "Getting Policy Compliance Details of a Device",
                        key = "dm:devices:compliance:view",
                        roles = {"Internal/devicemgt-user"},
                        permissions = {"/device-mgt/devices/owning-device/compliance/view"}
                ),
                @io.entgra.device.mgt.core.apimgt.annotations.Scope(
                        name = "Change device status.",
                        description = "Change device status.",
                        key = "dm:devices:status:change",
                        roles = {"Internal/devicemgt-user"},
                        permissions = {"/device-mgt/devices/change-status"}
                ),
                @io.entgra.device.mgt.core.apimgt.annotations.Scope(
                        name = "Update status of a given operation",
                        description = "Updates the status of a given operation of a given device",
                        key = "dm:devices:ops:status:update",
                        roles = {"Internal/devicemgt-user"},
                        permissions = {"/device-mgt/devices/operations/status-update"}
                ),
                @io.entgra.device.mgt.core.apimgt.annotations.Scope(
                        name = "Enroll Device",
                        description = "Register a device",
                        key = "dm:device:enroll",
                        roles = {"Internal/devicemgt-user"},
                        permissions = {"/device-mgt/devices/owning-device/add"}
                ),
                @Scope(
                        name = "Viewing Enrollment Guide",
                        description = "Show enrollment guide to users",
                        key = "dm:devices:enrollment-guide:view",
                        roles = {"Internal/devicemgt-user"},
                        permissions = {"/device-mgt/devices/enrollment-guide/view"}
                ),
        }
)
@Path("/devices")
@Api(value = "Device Management", description = "This API carries all device management related operations " +
        "such as get all the available devices, etc.")
public interface DeviceFeatureOperationService {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{type}/{id}/operations")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Getting Device Operation Details",
            notes = "Get the details of operations carried out on a selected device.",
            tags = "Device Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = Constants.SCOPE, value = "dm:devices:ops:view")
                    })
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message = "OK. \n Successfully fetched the list of operations scheduled for the device.",
                            response = Operation.class,
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
                                            description = "Date and time the resource was last modified" +
                                                    "Used by caches, or in conditional requests.")}),
                    @ApiResponse(
                            code = 303,
                            message = "See Other. \n " +
                                    "The source can be retrieved from the URL specified in the location header.\n",
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
                            message = "Not Found. \n The specified device does not exist.",
                            response = ErrorResponse.class),
                    @ApiResponse(
                            code = 406,
                            message = "Not Acceptable. \n The requested media type is not supported."),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. \n " +
                                    "Server error occurred while retrieving the operation list scheduled for the device.",
                            response = ErrorResponse.class)
            })
    Response getDeviceOperations(
            @ApiParam(
                    name = "type",
                    value = "The device type name, such as ios, android, windows or fire-alarm.",
                    required = true)
            @PathParam("type")
            @Size(max = 45)
            String type,
            @ApiParam(
                    name = "id",
                    value = "The device identifier of the device you wish to get details.\n" +
                            "INFO: Make sure to add the ID of a device that is already registered with WSO2 IoTS.",
                    required = true)
            @PathParam("id")
            @Size(max = 45)
            String id,
            @ApiParam(
                    name = "If-Modified-Since",
                    value = "Checks if the requested variant was modified, since the specified date-time. \n" +
                            "Provide the value in the following format: EEE, d MMM yyyy HH:mm:ss Z.\n" +
                            "Example: Mon, 05 Jan 2014 15:10:00 +0200")
            @HeaderParam("If-Modified-Since")
            String ifModifiedSince,
            @ApiParam(
                    name = "offset",
                    value = "The starting pagination index for the complete list of qualified items.",
                    defaultValue = "0")
            @QueryParam("offset")
            int offset,
            @ApiParam(
                    name = "limit",
                    value = "Provide how many activity details you require from the starting pagination index/offset.",
                    defaultValue = "5")
            @QueryParam("limit")
            int limit,
            @ApiParam(
                    name = "owner",
                    value = "Provides the owner of the required device.",
                    defaultValue = "")
            @QueryParam("owner")
            String owner,
            @ApiParam(
                    name = "ownership",
                    value = "Provides the ownership of the required device.")
            @QueryParam("ownership")
            String ownership,
            @ApiParam(
                    name = "createdFrom",
                    value = "Since when user wants to filter operation logs using the created data and time (timestamp in seconds)")
            @QueryParam("createdFrom")
            Long createdFrom,
            @ApiParam(
                    name = "createdTo",
                    value = "Till when user wants to filter operation logs using the created data and time (timestamp in seconds)")
            @QueryParam("createdTo")
            Long createdTo,
            @ApiParam(
                    name = "updatedFrom",
                    value = "Since when user wants to filter operation logs using the received date and time (timestamp in seconds)")
            @QueryParam("updatedFrom")
            Long updatedFrom,
            @ApiParam(
                    name = "updatedTo",
                    value = "Till when user wants to filter operation logs using the received date and time (timestamp in seconds)")
            @QueryParam("updatedTo")
            Long updatedTo,
            @ApiParam(
                    name = "operationCode",
                    value = "Provides the operation codes to filter the operation logs via operation codes")
            @QueryParam("operationCode")
            List<String> operationCode,
            @ApiParam(
                    name = "operationStatus",
                    value = "Provides the status codes to filter operation logs via status")
            @QueryParam("operationStatus")
            List<String> status);
}
