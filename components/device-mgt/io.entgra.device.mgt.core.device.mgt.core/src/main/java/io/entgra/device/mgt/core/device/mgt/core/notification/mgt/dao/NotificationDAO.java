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

package io.entgra.device.mgt.core.device.mgt.core.notification.mgt.dao;

import io.entgra.device.mgt.core.device.mgt.common.PaginationRequest;
import io.entgra.device.mgt.core.device.mgt.common.notification.mgt.Notification;
import io.entgra.device.mgt.core.device.mgt.common.notification.mgt.NotificationManagementException;

import java.util.List;

/**
 * This class defines the methods to be implemented by NotificationDAO layer.
 */
public interface NotificationDAO {

	/**
	 * This method is used to add a notification.
	 *
	 * @param deviceId device id.
	 * @param tenantId tenant id.
	 * @param notification Notification object.
	 * @return returns the id of the persisted Notification record.
	 * @throws NotificationManagementException
	 */
	int addNotification(int deviceId, int tenantId, Notification notification) throws
	                                                                NotificationManagementException;

	/**
	 * This method is used to update a notification.
	 *
	 * @param notification Notification object.
	 * @return returns the no of updated records.
	 * @throws NotificationManagementException
	 */
	int updateNotification(Notification notification) throws NotificationManagementException;

	/**
	 * This method is used to update a notification status.
	 *
	 * @param notificationId notification id.
	 * @param status Notification.Status.
	 * @return returns the no of updated records.
	 * @throws NotificationManagementException
	 */
	int updateNotificationStatus(int notificationId, Notification.Status status)
			throws NotificationManagementException;

	/**
	 * Update status of all notifications.
	 *
	 * @return returns the no of updated records.
	 * @throws NotificationManagementException
	 */
	int updateAllNotifications(Notification.Status status, int tenantID) throws
			NotificationManagementException;

	/**
	 * This method is used to get all notifications based on tenant-id.
	 *
	 * @param tenantId tenant id.
	 * @return returns the matching notifications.
	 * @throws NotificationManagementException
	 */
	List<Notification> getAllNotifications(int tenantId) throws NotificationManagementException;

	Notification getNotification(int tenantId, int notificationId) throws NotificationManagementException;

	List<Notification> getAllNotifications(PaginationRequest request, int tenantId) throws NotificationManagementException;

	int getNotificationCount(int tenantId) throws NotificationManagementException;

	int getNotificationCountByStatus(Notification.Status status, int tenantId) throws NotificationManagementException;

	/**
	 * This method is used to get all notifications based on notification-status.
	 *
	 * @param status Notification.Status.
	 * @param tenantId tenant id.
	 * @return returns the matching notifications.
	 * @throws NotificationManagementException
	 */
	List<Notification> getNotificationsByStatus(Notification.Status status, int tenantId) throws
	                                                              NotificationManagementException;

	List<Notification> getNotificationsByStatus(PaginationRequest request, Notification.Status status, int tenantId) throws
			NotificationManagementException;

	/**
	 * Inserts a new notification entry into the notification database.
	 *
	 * @param tenantId         The ID of the tenant for whom the notification is being created.
	 * @param notificationConfigId The ID of the notification configuration to associate with the notification.
	 * @param type             The type of the notification.
	 * @param description      A description providing details of the notification.
	 * @return The ID of the newly inserted notification.
	 * @throws NotificationManagementException If an error occurs while inserting the notification.
	 */
	int insertNotification(int tenantId, int notificationConfigId, String type, String description)
			throws NotificationManagementException;

	/**
	 * Inserts user-specific actions related to a given notification.
	 *
	 * @param notificationId The ID of the notification for which user actions are being inserted.
	 * @param usernames      A list of usernames to associate with the notification actions.
	 * @throws NotificationManagementException If an error occurs while inserting the user actions.
	 */
	void insertNotificationUserActions(int notificationId, List<String> usernames) throws NotificationManagementException;

	/**
	 * Retrieves the count of unread notifications for a specific user.
	 *
	 * @param username The username for which to retrieve the count of unread notifications.
	 * @return The number of unread notifications for the given user.
	 * @throws NotificationManagementException if a database access error occurs
	 *         or the query execution fails.
	 */
	int getUnreadNotificationCountForUser(String username) throws NotificationManagementException;
}
