package io.entgra.device.mgt.core.notification.mgt.core.config.datasource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DeviceMgtConfiguration")
public class NotificationDeviceMgtConfiguration {
    private NotificationManagementRepository ceaPolicyManagementRepository;

    public NotificationManagementRepository getNotificationManagementRepository() {
        return ceaPolicyManagementRepository;
    }

    @XmlElement(name = "ManagementRepository", nillable = false)
    public void setNotificationManagementRepository(NotificationManagementRepository ceaPolicyManagementRepository) {
        this.ceaPolicyManagementRepository = ceaPolicyManagementRepository;
    }
}