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

package io.entgra.device.mgt.core.device.mgt.core.notification.mgt.dao.impl;

import io.entgra.device.mgt.core.device.mgt.common.PaginationRequest;
import io.entgra.device.mgt.core.device.mgt.common.dto.NotificationConfig;
import io.entgra.device.mgt.core.device.mgt.common.notification.mgt.Notification;
import io.entgra.device.mgt.core.device.mgt.common.notification.mgt.NotificationManagementException;
import io.entgra.device.mgt.core.device.mgt.core.notification.mgt.dao.NotificationManagementDAOFactory;
import io.entgra.device.mgt.core.device.mgt.core.notification.mgt.dao.util.NotificationDAOUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class holds the Oracle implementation of NotificationDAO which can be used to support Oracle db syntax.
 */
public class OracleNotificationDAOImpl extends AbstractNotificationDAOImpl {

    @Override
    public int addNotification(int deviceId, int tenantId, Notification notification) throws
            NotificationManagementException {
        Connection conn;
        PreparedStatement stmt = null;
        ResultSet rs;
        int notificationId = -1;
        try {
            conn = NotificationManagementDAOFactory.getConnection();
            String sql = "INSERT INTO DM_NOTIFICATION(DEVICE_ID, OPERATION_ID, STATUS, " +
                    "DESCRIPTION, TENANT_ID, LAST_UPDATED_TIMESTAMP) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, new int[] { 1 });
            stmt.setInt(1, deviceId);
            stmt.setInt(2, notification.getOperationId());
            stmt.setString(3, notification.getStatus().toString());
            stmt.setString(4, notification.getDescription());
            stmt.setInt(5, tenantId);
            stmt.setTimestamp(6, new Timestamp(new Date().getTime()));
            stmt.execute();
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                notificationId = rs.getInt(1);
            }
        } catch (Exception e) {
            throw new NotificationManagementException(
                    "Error occurred while adding the " + "Notification for device id : " + deviceId, e);
        } finally {
            NotificationDAOUtil.cleanupResources(stmt, null);
        }
        return notificationId;
    }

    @Override
    public List<Notification> getAllNotifications(PaginationRequest request, int tenantId) throws
                                                                                           NotificationManagementException {
        Connection conn;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Notification> notifications = null;
        try {
            conn = NotificationManagementDAOFactory.getConnection();
            String sql =
                    "SELECT n1.NOTIFICATION_ID, n1.DEVICE_ID, n1.OPERATION_ID, n1.STATUS, n1.DESCRIPTION," +
                    " d.DEVICE_IDENTIFICATION, d.NAME as DEVICE_NAME, t.NAME AS DEVICE_TYPE FROM DM_DEVICE d, DM_DEVICE_TYPE t, (SELECT " +
                    "NOTIFICATION_ID, DEVICE_ID, OPERATION_ID, STATUS, DESCRIPTION FROM DM_NOTIFICATION WHERE " +
                    "TENANT_ID = ?) n1 WHERE n1.DEVICE_ID = d.ID AND d.DEVICE_TYPE_ID=t.ID AND TENANT_ID = ?";

            sql = sql + " ORDER BY n1.NOTIFICATION_ID DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, tenantId);
            stmt.setInt(2, tenantId);
            int paramIdx = 3;

            stmt.setInt(paramIdx++, request.getStartIndex());
            stmt.setInt(paramIdx, request.getRowCount());

            rs = stmt.executeQuery();
            notifications = new ArrayList<>();
            while (rs.next()) {
                notifications.add(NotificationDAOUtil.getNotificationWithDeviceInfo(rs));
            }
        } catch (SQLException e) {
            throw new NotificationManagementException(
                    "Error occurred while retrieving information of all notifications", e);
        } finally {
            NotificationDAOUtil.cleanupResources(stmt, rs);
        }
        return notifications;
    }


    @Override
    public List<Notification> getNotificationsByStatus(PaginationRequest request, Notification.Status status, int tenantId) throws
                                                                                                                            NotificationManagementException{
        Connection conn;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Notification> notifications = null;
        try {
            conn = NotificationManagementDAOFactory.getConnection();
            String sql = "SELECT n1.NOTIFICATION_ID, n1.DEVICE_ID, n1.OPERATION_ID, n1.STATUS,"
                    + " n1.DESCRIPTION, d.DEVICE_IDENTIFICATION, d.NAME as DEVICE_NAME, t.NAME AS DEVICE_TYPE FROM "
                    + "DM_DEVICE d, DM_DEVICE_TYPE t, (SELECT NOTIFICATION_ID, DEVICE_ID, "
                    + "OPERATION_ID, STATUS, DESCRIPTION FROM DM_NOTIFICATION WHERE "
                    + "TENANT_ID = ? AND STATUS = ?) n1 WHERE n1.DEVICE_ID = d.ID AND d.DEVICE_TYPE_ID=t.ID "
                    + "AND TENANT_ID = ?";

            sql = sql + " ORDER BY n1.NOTIFICATION_ID DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, tenantId);
            stmt.setString(2, status.toString());
            stmt.setInt(3, tenantId);

            int paramIdx = 4;

            stmt.setInt(paramIdx++, request.getStartIndex());
            stmt.setInt(paramIdx, request.getRowCount());


            rs = stmt.executeQuery();
            notifications = new ArrayList<>();
            while (rs.next()) {
                notifications.add(NotificationDAOUtil.getNotificationWithDeviceInfo(rs));
            }
        } catch (SQLException e) {
            throw new NotificationManagementException(
                    "Error occurred while retrieving information of all " +
                    "notifications by status : " + status, e);
        } finally {
            NotificationDAOUtil.cleanupResources(stmt, rs);
        }
        return notifications;
    }

    @Override
    public NotificationConfig getNotificationConfig(int tenantId, String operationCode)
            throws NotificationManagementException {
        String sql =
                "SELECT " +
                        "METADATA_VALUE " +
                        "FROM DM_METADATA " +
                        "WHERE METADATA_KEY = ? " +
                        "AND TENANT_ID = ?";
        try (Connection conn = NotificationManagementDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "notificationConfig");
            stmt.setInt(2, tenantId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String metadataValue = rs.getString("METADATA_VALUE");
                    JSONArray jsonArray = new JSONArray(metadataValue);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonConfig = jsonArray.getJSONObject(i);
                        if (jsonConfig.getString("code").equalsIgnoreCase(operationCode)) {

                            JSONObject recipientsObj = jsonConfig.optJSONObject("recipients");
                            List<String> recipients = new ArrayList<>();
                            if (recipientsObj != null) {
                                if (recipientsObj.has("users")) {
                                    JSONArray users = recipientsObj.getJSONArray("users");
                                    for (int j = 0; j < users.length(); j++) {
                                        recipients.add("user:" + users.getString(j));
                                    }
                                }
                                if (recipientsObj.has("roles")) {
                                    JSONArray roles = recipientsObj.getJSONArray("roles");
                                    for (int j = 0; j < roles.length(); j++) {
                                        recipients.add("role:" + roles.getString(j));
                                    }
                                }
                            }
                            return new NotificationConfig(
                                    jsonConfig.getString("id"),
                                    jsonConfig.getInt("priority"),
                                    jsonConfig.getString("type"),
                                    recipients
                            );
                        }
                    }
                }
            }
        } catch (SQLException | JSONException e) {
            throw new NotificationManagementException("Error fetching notificationConfig for tenant ID " + tenantId, e);
        }
        return null;
    }

    @Override
    public int insertNotification(int tenantId, String notificationConfigId, int priority, String type, String description)
            throws NotificationManagementException {
        String sql =
                "INSERT INTO DM_NOTIFICATION " +
                        "(NOTIFICATION_CONFIG_ID, " +
                        "TENANT_ID, " +
                        "DESCRIPTION, " +
                        "TYPE) " +
                "VALUES (?, ?, ?, ?, ?) " +
                        "RETURNING NOTIFICATION_ID INTO ?";
        try (Connection conn = NotificationManagementDAOFactory.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, notificationConfigId);
            stmt.setInt(2, tenantId);
            stmt.setString(3, description);
            stmt.setInt(4, priority);
            stmt.setString(5, type);
            stmt.registerOutParameter(6, java.sql.Types.INTEGER);
            stmt.executeUpdate();
            return stmt.getInt(6);
        } catch (SQLException e) {
            throw new NotificationManagementException("Error inserting notification", e);
        }
    }

    @Override
    public void insertNotificationUserActions(int notificationId, List<String> usernames)
            throws NotificationManagementException {
        String sql =
                "INSERT INTO DM_NOTIFICATION_USER_ACTION " +
                        "(NOTIFICATION_ID, " +
                        "USERNAME, " +
                        "ACTION_TYPE) " +
                        "VALUES (?, ?, ?)";
        try (Connection conn = NotificationManagementDAOFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (String username : usernames) {
                stmt.setInt(1, notificationId);
                stmt.setString(2, username);
                stmt.setString(3, "UNREAD");
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new NotificationManagementException("Error inserting notification user actions", e);
        }
    }
}
