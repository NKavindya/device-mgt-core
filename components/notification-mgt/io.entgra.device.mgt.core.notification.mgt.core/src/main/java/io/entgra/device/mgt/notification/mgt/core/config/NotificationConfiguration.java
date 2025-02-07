package io.entgra.device.mgt.notification.mgt.core.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "NotificationConfiguration")
public class NotificationConfiguration {

    private String datasourceName;
    private String notificationEndpoint;
    private int retryCount;
    private int timeout;

    @XmlElement(name = "DatasourceName", required = true)
    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }

    @XmlElement(name = "NotificationEndpoint", required = true)
    public String getNotificationEndpoint() {
        return notificationEndpoint;
    }

    public void setNotificationEndpoint(String notificationEndpoint) {
        this.notificationEndpoint = notificationEndpoint;
    }

    @XmlElement(name = "RetryCount", required = false)
    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    @XmlElement(name = "Timeout", required = false)
    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}

