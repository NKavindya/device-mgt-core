package io.entgra.device.mgt.notification.mgt.core.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;
import io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderService;


@Component(
        name = "io.entgra.device.mgt.core.notification.mgt.core.internal.NotificationManagementServiceComponent",
        immediate = true)
public class NotificationManagementServiceComponent {
    private static Log log = LogFactory.getLog(NotificationManagementServiceComponent.class);

    @SuppressWarnings("unused")
    @Activate
    protected void activate(ComponentContext componentContext) {
        BundleContext bundleContext = componentContext.getBundleContext();
        try {
            log.info("NotificationManagement core bundle has been successfully initialized");
        } catch (Throwable e) {
            log.error("Error occurred while initializing app management core bundle", e);
        }
    }

    @SuppressWarnings("unused")
    @Deactivate
    protected void deactivate(ComponentContext componentContext) {
        // Do nothing
    }

    @SuppressWarnings("unused")
    @Reference(
            name = "device.mgt.provider.service",
            service = io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetDeviceManagementService")
    protected void setDeviceManagementService(DeviceManagementProviderService deviceManagementProviderService) {
        if (log.isDebugEnabled()) {
            log.debug("Setting ApplicationDTO Management OSGI Manager");
        }
    }

    protected void unsetDeviceManagementService(DeviceManagementProviderService deviceManagementProviderService) {
        if (log.isDebugEnabled()) {
            log.debug("Unsetting ApplicationDTO Management OSGI Manager");
        }
    }
}
