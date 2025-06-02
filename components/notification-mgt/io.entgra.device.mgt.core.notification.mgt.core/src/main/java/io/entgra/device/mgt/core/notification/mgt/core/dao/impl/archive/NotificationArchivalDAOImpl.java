package io.entgra.device.mgt.core.notification.mgt.core.dao.impl.archive;

import io.entgra.device.mgt.core.notification.mgt.common.exception.NotificationManagementException;
import io.entgra.device.mgt.core.notification.mgt.core.dao.NotificationArchivalDAO;
import io.entgra.device.mgt.core.notification.mgt.core.dao.factory.NotificationManagementDAOFactory;
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

    @Override
    public List<Integer> moveNotificationsToArchive(Timestamp cutoff, int tenantId)
            throws NotificationManagementException {
        List<Integer> notificationIds = new ArrayList<>();
        String selectSQL =
                "SELECT " +
                        "NOTIFICATION_ID, " +
                        "NOTIFICATION_CONFIG_ID, " +
                        "TENANT_ID, " +
                        "DESCRIPTION, " +
                        "TYPE, " +
                        "CREATED_TIMESTAMP " +
                        "FROM DM_NOTIFICATION " +
                        "WHERE CREATED_TIMESTAMP < ? " +
                        "AND TENANT_ID = ?";
        String insertSQL =
                "INSERT " +
                        "INTO DM_NOTIFICATION_ARCH " +
                        "(NOTIFICATION_ID, " +
                        "NOTIFICATION_CONFIG_ID, " +
                        "TENANT_ID, " +
                        "DESCRIPTION, " +
                        "TYPE, " +
                        "CREATED_TIMESTAMP) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = NotificationManagementDAOFactory.getConnection();
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSQL);
                 PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                selectStmt.setTimestamp(1, cutoff);
                selectStmt.setInt(2, tenantId);
                ResultSet rs = selectStmt.executeQuery();
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
        } catch (SQLException e) {
            String msg = "Error occurred while archiving notifications";
            log.error(msg, e);
            throw new NotificationManagementException(msg, e);
        }
        return notificationIds;
    }

    @Override
    public void moveUserActionsToArchive(List<Integer> notificationIds) throws NotificationManagementException {
        if (notificationIds.isEmpty()) return;
        String inClause = notificationIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String selectSQL =
                "SELECT " +
                        "ACTION_ID, " +
                        "NOTIFICATION_ID, " +
                        "USERNAME, " +
                        "ACTION_TYPE, " +
                        "ACTION_TIMESTAMP " +
                        "FROM DM_NOTIFICATION_USER_ACTION " +
                        "WHERE NOTIFICATION_ID " +
                        "IN (" + inClause + ")";
        String checkExistSQL =
                "SELECT 1 " +
                        "FROM DM_NOTIFICATION_USER_ACTION_ARCH " +
                        "WHERE ACTION_ID = ?";
        String insertSQL =
                "INSERT " +
                        "INTO DM_NOTIFICATION_USER_ACTION_ARCH " +
                        "(ACTION_ID, " +
                        "NOTIFICATION_ID, " +
                        "USERNAME, " +
                        "ACTION_TYPE, " +
                        "ACTION_TIMESTAMP) " +
                        "VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = NotificationManagementDAOFactory.getConnection();
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSQL);
                 PreparedStatement checkExistStmt = conn.prepareStatement(checkExistSQL);
                 PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                for (int i = 0; i < notificationIds.size(); i++) {
                    selectStmt.setInt(i + 1, notificationIds.get(i));
                }
                ResultSet rs = selectStmt.executeQuery();
                while (rs.next()) {
                    int actionId = rs.getInt("ACTION_ID");
                    checkExistStmt.setInt(1, actionId);
                    ResultSet checkRs = checkExistStmt.executeQuery();
                    if (checkRs.next()) {
                        // Already exists, skip
                        continue;
                    }
                    insertStmt.setInt(1, actionId);
                    insertStmt.setInt(2, rs.getInt("NOTIFICATION_ID"));
                    insertStmt.setString(3, rs.getString("USERNAME"));
                    insertStmt.setString(4, rs.getString("ACTION_TYPE"));
                    insertStmt.setTimestamp(5, rs.getTimestamp("ACTION_TIMESTAMP"));
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }
        } catch (SQLException e) {
            String msg = "Failed to archive user actions";
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
            Connection conn = NotificationManagementDAOFactory.getConnection();
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
            Connection conn = NotificationManagementDAOFactory.getConnection();
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
        String selectSQL =
                "SELECT " +
                        "NOTIFICATION_ID, " +
                        "NOTIFICATION_CONFIG_ID, " +
                        "TENANT_ID, " +
                        "DESCRIPTION, " +
                        "TYPE, " +
                        "CREATED_TIMESTAMP " +
                "FROM DM_NOTIFICATION " +
                        "WHERE TENANT_ID = ? " +
                        "AND NOTIFICATION_CONFIG_ID = ? " +
                        "AND CREATED_TIMESTAMP < ?";
        String insertSQL =
                "INSERT INTO DM_NOTIFICATION_ARCH " +
                        "(NOTIFICATION_ID, " +
                        "NOTIFICATION_CONFIG_ID, " +
                        "TENANT_ID, " +
                        "DESCRIPTION, " +
                        "TYPE, " +
                        "CREATED_TIMESTAMP) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        List<Integer> movedIds = new ArrayList<>();
        try {
            Connection conn = NotificationManagementDAOFactory.getConnection();
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSQL);
                 PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                selectStmt.setInt(1, tenantId);
                selectStmt.setInt(2, configId);
                selectStmt.setTimestamp(3, cutoff);
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
                        movedIds.add(id);
                    }
                }
                insertStmt.executeBatch();
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
        if (excludedConfigIds == null || excludedConfigIds.isEmpty()) {
            // no exclusions, use standard archival
            return moveNotificationsToArchiveByConfig(cutoff, tenantId, -1);
        }
        StringBuilder queryBuilder = new StringBuilder(
                "SELECT " +
                        "NOTIFICATION_ID, " +
                        "NOTIFICATION_CONFIG_ID, " +
                        "TENANT_ID, " +
                        "DESCRIPTION, " +
                        "TYPE, " +
                        "CREATED_TIMESTAMP " +
                        "FROM DM_NOTIFICATION " +
                        "WHERE TENANT_ID = ? " +
                        "AND CREATED_TIMESTAMP < ? " +
                        "AND NOTIFICATION_CONFIG_ID " +
                        "NOT IN (");
        String placeholders = excludedConfigIds.stream().map(id -> "?").collect(Collectors.joining(","));
        queryBuilder.append(placeholders).append(")");
        String insertSQL =
                "INSERT INTO DM_NOTIFICATION_ARCH " +
                        "(NOTIFICATION_ID, " +
                        "NOTIFICATION_CONFIG_ID, " +
                        "TENANT_ID, " +
                        "DESCRIPTION, " +
                        "TYPE, " +
                        "CREATED_TIMESTAMP) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        List<Integer> movedIds = new ArrayList<>();
        try {
            Connection conn = NotificationManagementDAOFactory.getConnection();
            try (PreparedStatement selectStmt = conn.prepareStatement(queryBuilder.toString());
                 PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                selectStmt.setInt(1, tenantId);
                selectStmt.setTimestamp(2, cutoff);
                int i = 3;
                for (Integer configId : excludedConfigIds) {
                    selectStmt.setInt(i++, configId);
                }
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
                        movedIds.add(id);
                    }
                }
                insertStmt.executeBatch();
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
            Connection conn = NotificationManagementDAOFactory.getConnection();
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
            try (Connection conn = NotificationManagementDAOFactory.getConnection();
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
            Connection conn = NotificationManagementDAOFactory.getConnection();
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
