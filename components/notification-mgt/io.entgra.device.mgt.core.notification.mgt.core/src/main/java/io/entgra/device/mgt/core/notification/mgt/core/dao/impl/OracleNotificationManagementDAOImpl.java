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

package io.entgra.device.mgt.core.notification.mgt.core.dao.impl;

import io.entgra.device.mgt.core.notification.mgt.common.dto.Notification;
import io.entgra.device.mgt.core.notification.mgt.common.dto.UserNotificationAction;
import io.entgra.device.mgt.core.notification.mgt.common.exception.NotificationManagementException;
import io.entgra.device.mgt.core.notification.mgt.core.dao.NotificationManagementDAO;
import io.entgra.device.mgt.core.notification.mgt.core.dao.factory.NotificationManagementDAOFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OracleNotificationManagementDAOImpl implements NotificationManagementDAO {
    private static final Log log = LogFactory.getLog(OracleNotificationManagementDAOImpl.class);

    @Override
    public List<Notification> getLatestNotifications(int offset, int limit) throws NotificationManagementException {
        List<Notification> notifications = new ArrayList<>();
        String query =
                "SELECT * FROM ( " +
                        "SELECT t.*, ROWNUM rnum FROM ( " +
                        "SELECT * FROM DM_NOTIFICATION ORDER BY CREATED_TIMESTAMP DESC " +
                        ") t WHERE ROWNUM <= ? " +
                        ") WHERE rnum > ?";
        try {
            Connection connection = NotificationManagementDAOFactory.getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, offset + limit);
                preparedStatement.setInt(2, offset);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Notification notification = new Notification();
                        notification.setNotificationId(resultSet.getInt("NOTIFICATION_ID"));
                        notification.setNotificationConfigId(resultSet.getInt("NOTIFICATION_CONFIG_ID"));
                        notification.setTenantId(resultSet.getInt("TENANT_ID"));
                        notification.setDescription(resultSet.getString("DESCRIPTION"));
                        notification.setPriority(resultSet.getInt("PRIORITY"));
                        notification.setCreatedTimestamp(resultSet.getTimestamp("CREATED_TIMESTAMP"));
                        notifications.add(notification);
                    }
                }
            }
        } catch (SQLException e) {
            String msg = "Error occurred while retrieving notifications from Oracle DB";
            log.error(msg, e);
            throw new NotificationManagementException(msg, e);
        }
        return notifications;
    }

    @Override
    public List<Notification> getNotificationsByIds(List<Integer> notificationIds, int limit, int offset)
            throws NotificationManagementException {
        List<Notification> notifications = new ArrayList<>();
        if (notificationIds == null || notificationIds.isEmpty()) {
            return notifications;
        }
        StringBuilder inClause = new StringBuilder();
        for (int i = 0; i < notificationIds.size(); i++) {
            inClause.append("?");
            if (i < notificationIds.size() - 1) {
                inClause.append(",");
            }
        }
        String query =
                "SELECT * " +
                        "FROM (SELECT a.*, ROWNUM rnum " +
                        "FROM (SELECT NOTIFICATION_ID, " +
                        "DESCRIPTION, TYPE " +
                        "FROM DM_NOTIFICATION " +
                "WHERE TENANT_ID = ? " +
                        "AND NOTIFICATION_ID " +
                        "IN (" + inClause +
                ") ORDER BY CREATED_TIMESTAMP DESC) a " +
                        "WHERE ROWNUM <= ?) " +
                        "WHERE rnum > ?";
        try (Connection connection = NotificationManagementDAOFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            int tenantId = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();
            int paramIndex = 1;
            preparedStatement.setInt(paramIndex++, tenantId);
            for (Integer id : notificationIds) {
                preparedStatement.setInt(paramIndex++, id);
            }
            preparedStatement.setInt(paramIndex++, offset + limit);
            preparedStatement.setInt(paramIndex, offset);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    Notification notification = new Notification();
                    notification.setNotificationId(rs.getInt("NOTIFICATION_ID"));
                    notification.setDescription(rs.getString("DESCRIPTION"));
                    notification.setType(rs.getString("TYPE"));
                    notifications.add(notification);
                }
            }
        } catch (SQLException e) {
            String msg = "Error occurred while retrieving notifications by IDs from Oracle DB";
            log.error(msg, e);
            throw new NotificationManagementException(msg, e);
        }
        return notifications;
    }

    @Override
    public List<UserNotificationAction> getNotificationActionsByUser(String username, int limit, int offset)
            throws NotificationManagementException {
        List<UserNotificationAction> actions = new ArrayList<>();
        String query = "SELECT * FROM (" +
                "    SELECT a.*, ROWNUM rnum FROM (" +
                "        SELECT NOTIFICATION_ID, ACTION_ID, ACTION_TYPE " +
                "        FROM DM_NOTIFICATION_USER_ACTION " +
                "        WHERE USERNAME = ? " +
                "        ORDER BY ACTION_TIMESTAMP DESC" +
                "    ) a WHERE ROWNUM <= ?" +
                ") WHERE rnum > ?";
        try {
            Connection connection = NotificationManagementDAOFactory.getConnection();
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, username);
                ps.setInt(2, offset + limit);
                ps.setInt(3, offset);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        UserNotificationAction action = new UserNotificationAction();
                        action.setNotificationId(rs.getInt("NOTIFICATION_ID"));
                        action.setActionId(rs.getInt("ACTION_ID"));
                        action.setActionType(rs.getString("ACTION_TYPE"));
                        actions.add(action);
                    }
                }
            }
        } catch (SQLException e) {
            String msg = "Error fetching user actions from Oracle DB for user: " + username;
            log.error(msg, e);
            throw new NotificationManagementException(msg, e);
        }
        return actions;
    }

    @Override
    public void markNotificationAsRead(int notificationId, String username)
            throws NotificationManagementException {
        String query =
                "UPDATE DM_NOTIFICATION_USER_ACTION " +
                        "SET ACTION_TYPE = 'READ' " +
                "WHERE NOTIFICATION_ID = ? " +
                        "AND USERNAME = ? " +
                        "AND ACTION_TYPE = 'UNREAD'";
        try (Connection connection = NotificationManagementDAOFactory.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, notificationId);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            String msg = "Error updating notification to READ in Oracle DB";
            log.error(msg, e);
            throw new NotificationManagementException(msg, e);
        }
    }

    @Override
    public List<UserNotificationAction> getAllNotificationUserActions() throws NotificationManagementException {
        List<UserNotificationAction> userNotificationActions = new ArrayList<>();
        String query = "SELECT NOTIFICATION_ID, ACTION_ID, ACTION_TYPE, USERNAME, ACTION_TIMESTAMP " +
                "FROM DM_NOTIFICATION_USER_ACTION " +
                "ORDER BY ACTION_TIMESTAMP DESC";
        try {
            Connection connection = NotificationManagementDAOFactory.getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        UserNotificationAction userNotificationAction = new UserNotificationAction();
                        userNotificationAction.setNotificationId(resultSet.getInt("NOTIFICATION_ID"));
                        userNotificationAction.setActionId(resultSet.getInt("ACTION_ID"));
                        userNotificationAction.setActionType(resultSet.getString("ACTION_TYPE"));
                        userNotificationAction.setUsername(resultSet.getString("USERNAME"));
                        userNotificationAction.setActionTimestamp(resultSet.getTimestamp("ACTION_TIMESTAMP"));
                        userNotificationActions.add(userNotificationAction);
                    }
                }
            }
        } catch (SQLException e) {
            String msg = "Error occurred while retrieving all notification user actions.";
            log.error(msg, e);
            throw new NotificationManagementException(msg, e);
        }
        return userNotificationActions;
    }
}
