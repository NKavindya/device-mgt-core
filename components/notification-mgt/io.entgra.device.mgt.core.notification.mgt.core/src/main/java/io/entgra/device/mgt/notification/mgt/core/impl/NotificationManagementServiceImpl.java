package io.entgra.device.mgt.notification.mgt.core.impl;

import io.entgra.device.mgt.core.notification.mgt.common.dto.Notification;
import io.entgra.device.mgt.core.notification.mgt.common.exception.NotificationManagementException;
import io.entgra.device.mgt.core.notification.mgt.common.service.NotificationManagementService;
import io.entgra.device.mgt.notification.mgt.core.dao.NotificationManagementDAO;
import io.entgra.device.mgt.notification.mgt.core.dao.factory.NotificationManagementDAOFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class NotificationManagementServiceImpl implements NotificationManagementService {
    private static final Log log = LogFactory.getLog(NotificationManagementServiceImpl.class);
    private final NotificationManagementDAO notificationDAO;

    public NotificationManagementServiceImpl() {
        notificationDAO = NotificationManagementDAOFactory.getNotificationManagementDAO();
    }

    @Override
    public List<Notification> getLatestNotifications() throws NotificationManagementException {
        return notificationDAO.getLatestNotifications();
    }
}
