package io.entgra.device.mgt.core.notification.mgt.core.dao.impl.archive;

import io.entgra.device.mgt.core.device.mgt.core.config.DeviceConfigurationManager;
import io.entgra.device.mgt.core.notification.mgt.common.exception.NotificationManagementException;
import io.entgra.device.mgt.core.notification.mgt.core.dao.NotificationArchivalDAO;
import io.entgra.device.mgt.core.notification.mgt.core.dao.factory.archive.NotificationArchivalSourceDAOFactory;
import io.entgra.device.mgt.core.notification.mgt.core.dao.factory.archive.NotificationArchivalDestDAOFactory;
import io.entgra.device.mgt.core.notification.mgt.core.dao.util.NotificationDAOUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NotificationArchivalDAOImpl implements NotificationArchivalDAO {
    private static final Log log = LogFactory.getLog(NotificationArchivalDAOImpl.class);

    private static final String SOURCE_DB =
            DeviceConfigurationManager.getInstance().getDeviceManagementConfig().getArchivalConfiguration()
                    .getArchivalTaskConfiguration().getDbConfig().getSourceDB();

    private static final String DESTINATION_DB =
            DeviceConfigurationManager.getInstance().getDeviceManagementConfig().getArchivalConfiguration()
                    .getArchivalTaskConfiguration().getDbConfig().getDestinationDB();

    @Override
    public List<Integer> moveNotificationsToArchive(Timestamp cutoff, int tenantId)
            throws NotificationManagementException {
        List<Integer> notificationIds = new ArrayList<>();
        String selectSQL =
                "SELECT NOTIFICATION_ID, " +
                        "NOTIFICATION_CONFIG_ID, " +
                        "TENANT_ID, " +
                        "DESCRIPTION, " +
                        "TYPE, " +
                        "CREATED_TIMESTAMP " +
                        "FROM " + SOURCE_DB + ".DM_NOTIFICATION " +
                        "WHERE CREATED_TIMESTAMP < ? " +
                        "AND TENANT_ID = ?";
        String insertSQL =
                "INSERT INTO " + DESTINATION_DB + ".DM_NOTIFICATION_ARCH " +
                        "(NOTIFICATION_ID, " +
                        "NOTIFICATION_CONFIG_ID, " +
                        "TENANT_ID, " +
                        "DESCRIPTION, " +
                        "TYPE, " +
                        "CREATED_TIMESTAMP) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
        Connection sourceConn = null;
        Connection destConn = null;
        try {
            sourceConn = NotificationArchivalSourceDAOFactory.getConnection();
            destConn = NotificationArchivalDestDAOFactory.getConnection();
            try (PreparedStatement selectStmt = sourceConn.prepareStatement(selectSQL);
                 PreparedStatement insertStmt = destConn.prepareStatement(insertSQL)) {
                selectStmt.setTimestamp(1, cutoff);
                selectStmt.setInt(2, tenantId);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("NOTIFICATION_ID");
                        insertStmt.setInt(1, id);
                        insertStmt.setInt(2, rs.getInt("NOTIFICATION_CONFIG_ID"));
                        insertStmt.setInt(3, rs.getInt("TENANT_ID"));
                        insertStmt.setString(4, rs.getString("DESCRIPTION"));
                        insertStmt.setString(5, rs.getString("TYPE"));
                        insertStmt.setTimestamp(6, rs.getTimestamp("CREATED_TIMESTAMP"));
                        insertStmt.addBatch();
                        notificationIds.add(id);
                    }
                    insertStmt.executeBatch();
                }
            }
        } catch (SQLException e) {
            String msg = "Error occurred while archiving notifications from source DB to destination DB.";
            log.error(msg, e);
            throw new NotificationManagementException(msg, e);
        } finally {
            NotificationDAOUtil.cleanupResources(sourceConn);
            NotificationDAOUtil.cleanupResources(destConn);
        }
        return notificationIds;
    }

    @Override
    public void moveUserActionsToArchive(List<Integer> notificationIds) throws NotificationManagementException {
        if (notificationIds == null || notificationIds.isEmpty()) return;
        String inClause = notificationIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String insertSQL =
                "INSERT INTO " + DESTINATION_DB + ".DM_NOTIFICATION_USER_ACTION_ARCH " +
                "(ACTION_ID, " +
                        "NOTIFICATION_ID, " +
                        "USERNAME, " +
                        "ACTION_TYPE, " +
                        "ACTION_TIMESTAMP) " +
                "SELECT " +
                        "ACTION_ID, " +
                        "NOTIFICATION_ID, " +
                        "USERNAME, " +
                        "ACTION_TYPE, " +
                        "ACTION_TIMESTAMP " +
                "FROM " + SOURCE_DB + ".DM_NOTIFICATION_USER_ACTION " +
                "WHERE NOTIFICATION_ID " +
                        "IN (" + inClause + ")";
        String deleteSQL =
                "DELETE FROM " + SOURCE_DB + ".DM_NOTIFICATION_USER_ACTION " +
                "WHERE NOTIFICATION_ID " +
                        "IN (" + inClause + ")";
        try {
            Connection sourceConn = NotificationArchivalSourceDAOFactory.getConnection();
            Connection destConn = NotificationArchivalDestDAOFactory.getConnection();
            try (PreparedStatement insertStmt = destConn.prepareStatement(insertSQL);
                 PreparedStatement deleteStmt = sourceConn.prepareStatement(deleteSQL)) {
                for (int i = 0; i < notificationIds.size(); i++) {
                    insertStmt.setInt(i + 1, notificationIds.get(i));
                    deleteStmt.setInt(i + 1, notificationIds.get(i));
                }
                insertStmt.executeUpdate();
                deleteStmt.executeUpdate();
            }
        } catch (SQLException e) {
            String msg = "Error occurred while archiving user actions";
            log.error(msg, e);
            throw new NotificationManagementException(msg, e);
        }
    }

    @Override
    public int deleteOldNotifications(Timestamp cutoff, int tenantId) throws NotificationManagementException {
        String sql =
                "DELETE " +
                        "FROM DM_NOTIFICATION " +
                        "WHERE CREATED_TIMESTAMP < ? " +
                        "AND TENANT_ID = ?";
        try {
            Connection conn = NotificationArchivalSourceDAOFactory.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setTimestamp(1, cutoff);
                stmt.setInt(2, tenantId);
                return stmt.executeUpdate();
            }
        } catch (SQLException e) {
            String msg = "Failed to delete old notifications for tenant " + tenantId;
            log.error(msg, e);
            throw new NotificationManagementException(msg, e);
        }
    }

    @Override
    public int deleteOldUserActions(List<Integer> notificationIds) throws NotificationManagementException {
        if (notificationIds.isEmpty()) return 0;
        String inClause = notificationIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql =
                "DELETE " +
                        "FROM DM_NOTIFICATION_USER_ACTION " +
                        "WHERE NOTIFICATION_ID " +
                        "IN (" + inClause + ")";
        try {
            Connection conn = NotificationArchivalSourceDAOFactory.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < notificationIds.size(); i++) {
                    stmt.setInt(i + 1, notificationIds.get(i));
                }
                return stmt.executeUpdate();
            }
        } catch (SQLException e) {
            String msg = "Failed to delete old user actions";
            log.error(msg, e);
            throw new NotificationManagementException(msg, e);
        }
    }

    @Override
    public List<Integer> moveNotificationsToArchiveByConfig(Timestamp cutoff, int tenantId, int configId)
            throws NotificationManagementException {
        List<Integer> movedIds = new ArrayList<>();
        String insertSQL =
                "INSERT INTO " + DESTINATION_DB + ".DM_NOTIFICATION_ARCH " +
                "(NOTIFICATION_ID, " +
                        "NOTIFICATION_CONFIG_ID, " +
                        "TENANT_ID, " +
                        "DESCRIPTION, " +
                        "TYPE, " +
                        "CREATED_TIMESTAMP) " +
                "SELECT " +
                        "NOTIFICATION_ID, " +
                        "NOTIFICATION_CONFIG_ID, " +
                        "TENANT_ID, " +
                        "DESCRIPTION, " +
                        "TYPE, " +
                        "CREATED_TIMESTAMP " +
                "FROM " + SOURCE_DB + ".DM_NOTIFICATION " +
                "WHERE TENANT_ID = ? " +
                        "AND NOTIFICATION_CONFIG_ID = ? " +
                        "AND CREATED_TIMESTAMP < ?";
        String selectIdsSQL =
                "SELECT NOTIFICATION_ID " +
                        "FROM " + SOURCE_DB + ".DM_NOTIFICATION " +
                "WHERE TENANT_ID = ? " +
                        "AND NOTIFICATION_CONFIG_ID = ? " +
                        "AND CREATED_TIMESTAMP < ?";
        try {
            Connection sourceConn = NotificationArchivalSourceDAOFactory.getConnection();
            Connection destConn = NotificationArchivalDestDAOFactory.getConnection();
            try (PreparedStatement insertStmt = destConn.prepareStatement(insertSQL);
                 PreparedStatement selectStmt = sourceConn.prepareStatement(selectIdsSQL)) {
                insertStmt.setInt(1, tenantId);
                insertStmt.setInt(2, configId);
                insertStmt.setTimestamp(3, cutoff);
                insertStmt.executeUpdate();
                selectStmt.setInt(1, tenantId);
                selectStmt.setInt(2, configId);
                selectStmt.setTimestamp(3, cutoff);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    while (rs.next()) {
                        movedIds.add(rs.getInt("NOTIFICATION_ID"));
                    }
                }
            }
        } catch (SQLException e) {
            String msg = "Error moving notifications to archive for configId: " + configId;
            log.error(msg, e);
            throw new NotificationManagementException(msg, e);
        }
        return movedIds;
    }

    @Override
    public List<Integer> moveNotificationsToArchiveExcludingConfigs(Timestamp cutoff, int tenantId,
                                                                    Set<Integer> excludedConfigIds)
            throws NotificationManagementException {
        List<Integer> movedIds = new ArrayList<>();
        if (excludedConfigIds == null || excludedConfigIds.isEmpty()) {
            return moveNotificationsToArchiveByConfig(cutoff, tenantId, -1);
        }
        String placeholders = excludedConfigIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String insertSQL =
                "INSERT INTO " + DESTINATION_DB + ".DM_NOTIFICATION_ARCH " +
                "(NOTIFICATION_ID, " +
                        "NOTIFICATION_CONFIG_ID, " +
                        "TENANT_ID, " +
                        "DESCRIPTION, " +
                        "TYPE, " +
                        "CREATED_TIMESTAMP) " +
                "SELECT " +
                        "NOTIFICATION_ID, " +
                        "NOTIFICATION_CONFIG_ID, " +
                        "TENANT_ID, " +
                        "DESCRIPTION, " +
                        "TYPE, " +
                        "CREATED_TIMESTAMP " +
                "FROM " + SOURCE_DB + ".DM_NOTIFICATION " +
                "WHERE TENANT_ID = ? " +
                        "AND CREATED_TIMESTAMP < ? " +
                        "AND NOTIFICATION_CONFIG_ID " +
                        "NOT IN (" + placeholders + ")";
        String selectIdsSQL =
                "SELECT NOTIFICATION_ID " +
                        "FROM " + SOURCE_DB + ".DM_NOTIFICATION " +
                "WHERE TENANT_ID = ? " +
                        "AND CREATED_TIMESTAMP < ? " +
                        "AND NOTIFICATION_CONFIG_ID " +
                        "NOT IN (" + placeholders + ")";
        try {
            Connection sourceConn = NotificationArchivalSourceDAOFactory.getConnection();
            Connection destConn = NotificationArchivalDestDAOFactory.getConnection();
            try (PreparedStatement insertStmt = destConn.prepareStatement(insertSQL);
                 PreparedStatement selectStmt = sourceConn.prepareStatement(selectIdsSQL)) {
                insertStmt.setInt(1, tenantId);
                insertStmt.setTimestamp(2, cutoff);
                selectStmt.setInt(1, tenantId);
                selectStmt.setTimestamp(2, cutoff);
                int i = 3;
                for (Integer configId : excludedConfigIds) {
                    insertStmt.setInt(i, configId);
                    selectStmt.setInt(i, configId);
                    i++;
                }
                insertStmt.executeUpdate();
                try (ResultSet rs = selectStmt.executeQuery()) {
                    while (rs.next()) {
                        movedIds.add(rs.getInt("NOTIFICATION_ID"));
                    }
                }
            }
        } catch (SQLException e) {
            String msg = "Error moving notifications excluding configIds";
            log.error(msg, e);
            throw new NotificationManagementException(msg, e);
        }
        return movedIds;
    }

    @Override
    public int deleteOldNotificationsByConfig(Timestamp cutoff, int tenantId, int configId)
            throws NotificationManagementException {
        String deleteSQL =
                "DELETE FROM DM_NOTIFICATION " +
                        "WHERE TENANT_ID = ? " +
                        "AND NOTIFICATION_CONFIG_ID = ? " +
                        "AND CREATED_TIMESTAMP < ?";
        try {
            Connection conn = NotificationArchivalSourceDAOFactory.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(deleteSQL)) {
                stmt.setInt(1, tenantId);
                stmt.setInt(2, configId);
                stmt.setTimestamp(3, cutoff);
                return stmt.executeUpdate();
            }
        } catch (SQLException e) {
            String msg = "Error deleting notifications by config ID";
            log.error(msg, e);
            throw new NotificationManagementException(msg, e);
        }
    }

    @Override
    public int deleteOldNotificationsExcludingConfigs(Timestamp cutoff, int tenantId, Set<Integer> excludedConfigIds)
            throws NotificationManagementException {
        if (excludedConfigIds == null || excludedConfigIds.isEmpty()) {
            String deleteSQL =
                    "DELETE FROM DM_NOTIFICATION " +
                            "WHERE TENANT_ID = ? " +
                            "AND CREATED_TIMESTAMP < ?";
            try (Connection conn = NotificationArchivalSourceDAOFactory.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(deleteSQL)) {
                stmt.setInt(1, tenantId);
                stmt.setTimestamp(2, cutoff);
                return stmt.executeUpdate();
            } catch (SQLException e) {
                throw new NotificationManagementException("Error deleting all old notifications", e);
            }
        }
        StringBuilder queryBuilder = new StringBuilder(
                "DELETE FROM DM_NOTIFICATION " +
                        "WHERE TENANT_ID = ? " +
                        "AND CREATED_TIMESTAMP < ? " +
                        "AND NOTIFICATION_CONFIG_ID " +
                        "NOT IN (");
        String placeholders =
                excludedConfigIds.stream().map(id -> "?").collect(Collectors.joining(","));
        queryBuilder.append(placeholders).append(")");
        try {
            Connection conn = NotificationArchivalSourceDAOFactory.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {
                stmt.setInt(1, tenantId);
                stmt.setTimestamp(2, cutoff);
                int i = 3;
                for (Integer configId : excludedConfigIds) {
                    stmt.setInt(i++, configId);
                }
                return stmt.executeUpdate();
            }
        } catch (SQLException e) {
            String msg = "Error deleting notifications excluding config IDs";
            log.error(msg, e);
            throw new NotificationManagementException(msg, e);
        }
    }
}
