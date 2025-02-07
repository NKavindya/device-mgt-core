package io.entgra.device.mgt.notification.mgt.core.config.datasource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ManagementRepository")
public class NotificationManagementRepository {
    private NotificationDatasourceConfiguration notificationDatasourceConfiguration;

    @XmlElement(name = "DataSourceConfiguration", nillable = false)
    public NotificationDatasourceConfiguration getDataSourceConfig() {
        return notificationDatasourceConfiguration;
    }

    public void setDataSourceConfig(NotificationDatasourceConfiguration notificationDatasourceConfiguration) {
        this.notificationDatasourceConfiguration = notificationDatasourceConfiguration;
    }
}