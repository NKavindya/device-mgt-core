package io.entgra.device.mgt.core.notification.mgt.core.config.datasource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DataSourceConfiguration")
public class NotificationDatasourceConfiguration {
    private JNDILookupDefinition jndiLookupDefinition;

    public JNDILookupDefinition getJndiLookupDefinition() {
        return jndiLookupDefinition;
    }

    @XmlElement(name = "JndiLookupDefinition", nillable = true)
    public void setJndiLookupDefinition(JNDILookupDefinition jndiLookupDefinition) {
        this.jndiLookupDefinition = jndiLookupDefinition;
    }
}
