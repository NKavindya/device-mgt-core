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

package io.entgra.device.mgt.core.notification.mgt.core.dao;

import io.entgra.device.mgt.core.notification.mgt.common.dto.Notification;
import io.entgra.device.mgt.core.notification.mgt.common.dto.UserNotificationAction;
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
    List<Notification> getLatestNotifications(int offset, int limit) throws NotificationManagementException;

    /**
     * Retrieves a paginated list of notifications from the database based on a given list of notification IDs.
     * The results are filtered by the current tenant ID and ordered by creation timestamp in descending order
     * (i.e., most recent first). Only selected fields — notification ID, description, and type — are returned.
     *
     * @param notificationIds List of notification IDs to filter the query. Must not be null or empty.
     * @param limit The maximum number of results to return.
     * @param offset The offset from where to start fetching the results (used for pagination).
     * @return A list of {@link Notification} objects containing ID, description, and type for each matched record.
     * @throws NotificationManagementException If any SQL or connection error occurs during query execution.
     */
    List<Notification> getNotificationsByIds(List<Integer> notificationIds, int limit, int offset)
            throws NotificationManagementException;

    /**
     * Retrieves a paginated list of NotificationAction records for the specified user.
     *
     * @param username the user to filter actions for
     * @param offset pagination offset
     * @param limit pagination limit
     * @return list of NotificationAction entries for the user
     * @throws NotificationManagementException if a DB error occurs
     */
    List<UserNotificationAction> getNotificationActionsByUser(String username, int limit, int offset, String status)
            throws NotificationManagementException;

    /**
     * Retrieves a list of notification IDs and their action types for the specified username.
     *
     * @param username the username whose notification actions are to be fetched
     * @return a list of pairs containing notification ID and action type
     * @throws NotificationManagementException if a database access error occurs
     */
    void markNotificationAsRead(int notificationId, String username) throws NotificationManagementException;

    List<UserNotificationAction> getAllNotificationUserActions() throws NotificationManagementException;
}
