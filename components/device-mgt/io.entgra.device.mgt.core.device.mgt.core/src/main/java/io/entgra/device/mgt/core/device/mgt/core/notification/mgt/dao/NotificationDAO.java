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
}
