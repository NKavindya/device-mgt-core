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

package io.entgra.device.mgt.core.notification.mgt.common.service;

import io.entgra.device.mgt.core.notification.mgt.common.dto.Notification;
import io.entgra.device.mgt.core.notification.mgt.common.dto.UserNotificationPayload;
import io.entgra.device.mgt.core.notification.mgt.common.exception.NotificationManagementException;

import java.util.List;

public interface NotificationManagementService {
    /**
     * Retrieve the latest notifications for a tenant.
     *
     * @return {@link List<Notification>}
     * @throws NotificationManagementException Throws when error occurred while retrieving notifications.
     */
    List<Notification> getLatestNotifications(int offset, int limit) throws NotificationManagementException;

    /**
     * Retrieves a list of notifications for a given user along with their read/unread status.
     * This method performs the following steps:
     *   Fetches the user's notification actions (e.g., READ, UNREAD) with pagination support.
     *   Retrieves the corresponding notification details (ID, description, type) using the notification IDs.
     *   Combines both into a list of {@link UserNotificationPayload} objects.
     * @param username The username of the user whose notifications are to be retrieved.
     * @param limit    The maximum number of notifications to return.
     * @param offset   The offset from which to start retrieving notifications (for pagination).
     * @return A list of {@link UserNotificationPayload} objects containing notification metadata and action status.
     * @throws NotificationManagementException If an error occurs while accessing the data store.
     */
    List<UserNotificationPayload> getUserNotificationsWithStatus(String username, int limit, int offset, String status)
            throws NotificationManagementException;

    /**
     * Marks a specific notification as READ for the given user.
     * This method updates the user’s notification action in the database from 'UNREAD' to 'READ' for
     * the specified notification ID. If the notification has already been marked as READ,
     * the method will have no effect.
     * @param notificationId The ID of the notification to mark as read.
     * @param username       The username of the user marking the notification as read.
     * @throws NotificationManagementException If there is an error while updating the database.
     */
    void markNotificationAsReadForUser(int notificationId, String username)
            throws NotificationManagementException;
}
