package io.entgra.device.mgt.core.notification.mgt.api.service;

import io.entgra.device.mgt.core.apimgt.annotations.Scope;
import io.entgra.device.mgt.core.apimgt.annotations.Scopes;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Api(tags = {"notifications", "device_management"})
@Path("/admin/notifications")
@SwaggerDefinition(
        info = @Info(
                description = "Notification Management API",
                version = "v1.0.0",
                title = "NotificationService API",
                extensions = @Extension(properties = {
                        @ExtensionProperty(name = "name", value = "NotificationService"),
                        @ExtensionProperty(name = "context", value = "/api/device-mgt/v1.0/admin/notifications"),
                })
        ),
        consumes = {MediaType.APPLICATION_JSON},
        produces = {MediaType.APPLICATION_JSON},
        schemes = {SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS},
        tags = {
                @Tag(name = "device_management", description = "Device management"),
                @Tag(name = "notifications", description = "Notifications management")
        }
)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Scopes(
        scopes = {
                @Scope(
                        name = "View Notifications",
                        description = "Retrieve latest notifications",
                        key = "dm:admin:notifications:view",
                        roles = {"Internal/devicemgt-admin"},
                        permissions = {"/device-mgt/admin/notifications/view"}
                ),
        }
)
public interface NotificationService {
    String SCOPE = "scope";

    @GET
    @Path("/latest")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            httpMethod = HttpMethod.GET,
            value = "Retrieve latest notifications",
            notes = "Returns the latest notifications for device management",
            tags = {"notifications", "device_management"},
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "dm:admin:notifications:view")
                    })
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200,
                            message = "OK. \n Successfully retrieve the cea ui configurations",
                            response = Integer.class),
                    @ApiResponse(
                            code = 304,
                            message = "Not Modified. \n Empty body because the client has already the latest version of " +
                                    "the requested resource.",
                            response = Response.class),
                    @ApiResponse(
                            code = 404,
                            message = "Configurations not found",
                            response = Response.class),
                    @ApiResponse(
                            code = 406,
                            message = "Not Acceptable.\n The requested media type is not supported.",
                            response = Response.class),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. \n Server error occurred while fetching the group count.",
                            response = Response.class)
            }
    )
    Response getLatestNotifications();

//    @POST
//    @Path("/create")
//    @ApiOperation(
//            produces = MediaType.APPLICATION_JSON,
//            httpMethod = HttpMethod.POST,
//            value = "Create a new notification",
//            notes = "Creates and stores a new notification in the system",
//            tags = {"notifications", "device_management"},
//            extensions = {
//                    @Extension(properties = {
//                            @ExtensionProperty(name = SCOPE, value = "dm:admin:notifications:create")
//                    })
//            }
//    )
//    @ApiResponses(
//            value = {
//                    @ApiResponse(code = 201, message = "Created. Successfully created notification", response = Notification.class),
//                    @ApiResponse(code = 500, message = "Internal Server Error. Error occurred while creating notification", response = Response.class)
//            }
//    )
//    Response createNotification(Notification notification);
}
