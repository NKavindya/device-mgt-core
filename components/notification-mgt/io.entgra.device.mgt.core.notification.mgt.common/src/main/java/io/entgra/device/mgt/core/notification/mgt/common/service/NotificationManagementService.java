package io.entgra.device.mgt.core.notification.mgt.common.service;

import io.entgra.device.mgt.core.notification.mgt.common.dto.Notification;
import io.entgra.device.mgt.core.notification.mgt.common.exception.NotificationManagementException;

import java.util.List;

public interface NotificationManagementService {
    /**
     * Retrieve the latest notifications for a tenant.
     *
     * @return {@link List<Notification>}
     * @throws NotificationManagementException Throws when error occurred while retrieving notifications.
     */
    List<Notification> getLatestNotifications() throws NotificationManagementException;
}
