package io.entgra.device.mgt.core.notification.mgt.api.impl;

import io.entgra.device.mgt.core.notification.mgt.api.service.NotificationService;
import io.entgra.device.mgt.core.notification.mgt.api.util.NotificationManagementApiUtil;
import io.entgra.device.mgt.core.notification.mgt.common.dto.Notification;
import io.entgra.device.mgt.core.notification.mgt.common.exception.NotificationManagementException;
import io.entgra.device.mgt.core.notification.mgt.common.service.NotificationManagementService;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/admin/notifications")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NotificationServiceImpl implements NotificationService {
    private static final Log log = LogFactory.getLog(NotificationServiceImpl.class);

    @GET
    @Path("/latest")
    @Override
    public Response getLatestNotifications() {
        NotificationManagementService notificationService = NotificationManagementApiUtil.getNotificationManagementService();
        try {
            List<Notification> notifications = notificationService.getLatestNotifications();
            if (notifications == null || notifications.isEmpty()) {
                return Response.status(HttpStatus.SC_NOT_FOUND).entity("No notifications found").build();
            }
            return Response.status(HttpStatus.SC_OK).entity(notifications).build();
        } catch (NotificationManagementException e) {
            String msg = "Error occurred while retrieving notifications";
            log.error(msg, e);
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).entity(msg).build();
        }
    }
}
