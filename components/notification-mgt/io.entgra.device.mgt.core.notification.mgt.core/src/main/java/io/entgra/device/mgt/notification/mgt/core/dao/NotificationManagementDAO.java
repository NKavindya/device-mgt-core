package io.entgra.device.mgt.notification.mgt.core.dao;

import io.entgra.device.mgt.core.notification.mgt.common.dto.Notification;
import io.entgra.device.mgt.core.notification.mgt.common.exception.NotificationManagementException;

import java.util.List;

/**
 * DAO class for Notification management
 */
public interface NotificationManagementDAO {
    /**
     * Retrieve the latest notifications for the tenant.
     *
     * @return {@link List<Notification>}
     * @throws NotificationManagementException Throws when error occurred while retrieving notifications.
     */
    List<Notification> getLatestNotifications() throws NotificationManagementException;

}
