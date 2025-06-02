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

import io.entgra.device.mgt.core.notification.mgt.common.beans.NotificationConfig;
import io.entgra.device.mgt.core.notification.mgt.common.dto.Notification;
import io.entgra.device.mgt.core.notification.mgt.common.dto.UserNotificationPayload;
import io.entgra.device.mgt.core.notification.mgt.common.exception.NotificationManagementException;
import io.entgra.device.mgt.core.notification.mgt.common.exception.TransactionManagementException;
import org.wso2.carbon.user.api.UserStoreException;

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

    /**
     * Retrieves the total number of user notification actions for a specific user,
     * optionally filtered by notification status.
     *
     * @param username the username to filter notification actions by (e.g., "admin").
     * @param status   (optional) the status of the notification action to filter by
     *                 (e.g., "READ", "UNREAD"). If null or empty, all statuses are counted.
     * @return the total count of notification actions for the given user and status.
     * @throws NotificationManagementException if an error occurs while accessing the database.
     */
    int getUserNotificationCount(String username, String status) throws NotificationManagementException;

    /**
     * Deletes one or more notifications for a given user.
     *
     * @param notificationIds A list of notification IDs to be deleted.
     * @param username        The username associated with the notifications.
     * @throws NotificationManagementException If an error occurs while deleting the notifications.
     */
    void deleteUserNotifications(List<Integer> notificationIds, String username)
            throws NotificationManagementException;

    /**
     * Archive one or more notifications for a given user.
     *
     * @param notificationIds A list of notification IDs to be deleted.
     * @param username        The username associated with the notifications.
     * @throws NotificationManagementException If an error occurs while deleting the notifications.
     */
    void archiveUserNotifications(List<Integer> notificationIds, String username)
            throws NotificationManagementException;
    /**
     * Handles the operation notification if applicable based on the provided operation code,
     * status, device type, and other related details. This method constructs a notification message
     * and triggers the appropriate notifications to the users based on the configuration.
     * It checks the device type, the notification trigger point, and the operation's status
     * before sending notifications.
     *
     * @param operationCode The unique code representing the operation (e.g., "POLICY_REVOKE").
     * @param operationStatus The current status of the operation (e.g., "COMPLETED", "PENDING").
     * @param deviceType The type of the device associated with the operation (e.g., "Smartphone").
     * @param deviceEnrollmentIDs deviceEnrollmentID The unique identifier for the device enrollment.
     * @param tenantId The tenant ID representing the specific tenant context for which notifications
     *                 are being sent.
     * @param notificationTriggerPoint The point in the process at which the notification should be triggered
     *                                 (e.g., "postSync", "immediate").
     *
     * @throws NotificationManagementException If an error occurs while handling the notification
     *                                        (e.g., issues with inserting notifications, user retrieval).
     */
    void handleOperationNotificationIfApplicable(String operationCode, String operationStatus,
                                                 String deviceType, List<Integer> deviceEnrollmentIDs,
                                                 int tenantId, String notificationTriggerPoint)
            throws NotificationManagementException;

    /**
     * Handles and publishes a batch notification when an operation is executed for multiple devices.
     *
     * @param config        The notification configuration corresponding to the operation.
     * @param deviceIds     The list of device IDs for which the operation was executed.
     * @param operationStatus The final status of the operation (e.g., COMPLETED, PENDING).
     * @param deviceType    The type of devices for which the operation was executed.
     * @param tenantId      The tenant ID under which the operation was performed.
     * @throws NotificationManagementException If an error occurs while managing the notification.
     * @throws TransactionManagementException  If a transaction-related error occurs.
     * @throws UserStoreException              If an error occurs while accessing the user store.
     */
    void handleBatchOperationNotificationIfApplicable(NotificationConfig config,
                                                      List<Integer> deviceIds,
                                                      String operationStatus,
                                                      String deviceType,
                                                      int tenantId)
            throws NotificationManagementException, TransactionManagementException, UserStoreException;

    /**
     * Handles task-based notifications if a notification configuration exists for the given task code.
     * If no configuration is found, the method simply exits without performing any notification actions.
     *
     * @param taskCode The unique identifier for the task being executed.
     * @param tenantId The tenant ID under which the task is being executed.
     * @param message The message to be sent with the notification, which can be task-specific.
     * @throws NotificationManagementException If an error occurs while handling the notification.
     */
    void handleTaskNotificationIfApplicable(String taskCode, int tenantId, String message)
            throws NotificationManagementException;
}
