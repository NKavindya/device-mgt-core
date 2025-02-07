package io.entgra.device.mgt.core.notification.mgt.core.dao.impl;

import io.entgra.device.mgt.core.notification.mgt.common.dto.Notification;
import io.entgra.device.mgt.core.notification.mgt.common.exception.NotificationManagementException;
import io.entgra.device.mgt.core.notification.mgt.core.dao.NotificationManagementDAO;
import io.entgra.device.mgt.core.notification.mgt.core.dao.factory.NotificationManagementDAOFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificationManagementDAOImpl implements NotificationManagementDAO {
    private static final Log log = LogFactory.getLog(NotificationManagementDAOImpl.class);

    @Override
    public List<Notification> getLatestNotifications() throws NotificationManagementException {
        List<Notification> notifications = new ArrayList<>();
        String query = "SELECT * FROM DM_NOTIFICATION ORDER BY LAST_UPDATED_TIMESTAMP DESC LIMIT 10";
        try (Connection connection = NotificationManagementDAOFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Notification notification = new Notification();
                notification.setNotificationId(resultSet.getInt("NOTIFICATION_ID"));
                notification.setDeviceId(resultSet.getInt("DEVICE_ID"));
                notification.setOperationId(resultSet.getInt("OPERATION_ID"));
                notification.setTenantId(resultSet.getInt("TENANT_ID"));
                notification.setStatus(resultSet.getString("STATUS"));
                notification.setDescription(resultSet.getString("DESCRIPTION"));
                notification.setLastUpdatedTimestamp(resultSet.getTimestamp("LAST_UPDATED_TIMESTAMP"));
                notifications.add(notification);
            }
        } catch (SQLException e) {
            String msg = "Error occurred while retrieving notifications from DB";
            log.error(msg, e);
            throw new NotificationManagementException(msg, e);
        }
        return notifications;
    }
}
