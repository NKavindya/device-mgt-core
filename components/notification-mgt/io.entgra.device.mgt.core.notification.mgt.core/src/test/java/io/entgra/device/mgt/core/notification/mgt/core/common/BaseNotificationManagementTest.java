/*
 * Copyright (c) 2018 - 2025, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
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

package io.entgra.device.mgt.core.notification.mgt.core.common;

import io.entgra.device.mgt.core.device.mgt.common.exceptions.DeviceManagementException;
import io.entgra.device.mgt.core.device.mgt.core.authorization.DeviceAccessAuthorizationServiceImpl;
import io.entgra.device.mgt.core.device.mgt.core.metadata.mgt.MetadataManagementServiceImpl;
import io.entgra.device.mgt.core.notification.mgt.common.exception.NotificationManagementException;
import io.entgra.device.mgt.core.notification.mgt.core.config.NotificationConfigurationManager;
import io.entgra.device.mgt.core.device.mgt.core.dao.DeviceManagementDAOFactory;
import io.entgra.device.mgt.core.notification.mgt.core.config.archive.NotificationArchiveConfigManager;
import io.entgra.device.mgt.core.notification.mgt.core.config.archive.datasource.NotificationDeviceMgtArchConfig;
import io.entgra.device.mgt.core.notification.mgt.core.config.datasource.NotificationDatasourceConfiguration;
import io.entgra.device.mgt.core.notification.mgt.core.dao.factory.archive.NotificationArchivalDestDAOFactory;
import io.entgra.device.mgt.core.notification.mgt.core.dao.factory.archive.NotificationArchivalSourceDAOFactory;
import io.entgra.device.mgt.core.notification.mgt.core.internal.NotificationManagementDataHolder;
import io.entgra.device.mgt.core.device.mgt.core.internal.DeviceManagementServiceComponent;
import io.entgra.device.mgt.core.device.mgt.core.notification.mgt.dao.NotificationManagementDAOFactory;
import io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderService;
import io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderServiceImpl;
import io.entgra.device.mgt.core.device.mgt.core.util.DeviceManagerUtil;
import io.entgra.device.mgt.core.notification.mgt.core.mock.MockDataSource;
import io.entgra.device.mgt.core.notification.mgt.core.util.NotificationManagerUtil;
import io.entgra.device.mgt.core.notification.mgt.core.util.TestUtils;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.wso2.carbon.registry.api.RegistryService;
import org.wso2.carbon.registry.core.config.RegistryContext;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.w3c.dom.Document;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.registry.core.internal.RegistryDataHolder;
import org.wso2.carbon.registry.core.jdbc.realm.InMemoryRealmService;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.utils.ConfigurationContextService;

import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Base test class for notification management tests
 */
public abstract class BaseNotificationManagementTest {

    protected static final String DATASOURCE_EXT = ".xml";
    private DataSource dataSource;
    NotificationConfigurationManager notificationConfigManager =
            NotificationConfigurationManager.getInstance();
    NotificationArchiveConfigManager notificationArchConfigManager =
            NotificationArchiveConfigManager.getInstance();
    private static String datasourceLocation;
    private static boolean mock;

    @BeforeSuite
    @Parameters({"datasource", "isMock"})
    public void setupDataSource(@Optional("src/test/resources/config/datasource/data-source-config") String datasource,
                                @Optional("false") boolean isMock)
            throws Exception {
        datasourceLocation = datasource;
        mock = isMock;
        this.initDataSource();
        this.initSQLScript();
        this.initializeCarbonContext();
        this.initServices();
    }

    protected void initDataSource() throws Exception {
        this.dataSource = this.getDataSource(this.
                readDataSourceConfig(datasourceLocation + DATASOURCE_EXT));
        NotificationManagementDAOFactory.init(dataSource);
        NotificationArchivalDestDAOFactory.init(dataSource);
        NotificationArchivalSourceDAOFactory.init(dataSource);
    }

    private void initServices() throws NotificationManagementException, RegistryException {
        NotificationConfigurationManager.getInstance().initConfig();
        RealmService realmService = new InMemoryRealmService();
        DeviceManagementProviderService deviceMgtService = new DeviceManagementProviderServiceImpl();
        DeviceManagementServiceComponent.notifyStartupListeners();
        NotificationManagementDataHolder.getInstance().setDeviceManagementProviderService(deviceMgtService);
        NotificationManagementDataHolder.getInstance().setRealmService(realmService);
        NotificationManagementDataHolder.getInstance().setMetaDataManagementService(new MetadataManagementServiceImpl());
        NotificationManagementDataHolder.getInstance().setTaskService(null);
    }

    private RegistryService getRegistryService() throws RegistryException {
        RealmService realmService = new InMemoryRealmService();
        RegistryDataHolder.getInstance().setRealmService(realmService);
        NotificationManagementDataHolder.getInstance().setRealmService(realmService);
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("carbon-home/repository/conf/registry.xml");
        RegistryContext context = RegistryContext.getBaseInstance(is, realmService);
        context.setSetup(true);
        return context.getEmbeddedRegistryService();
    }

    private ConfigurationContextService getConfigContextService() throws RegistryException, AxisFault {
        ConfigurationContext context = ConfigurationContextFactory.createConfigurationContextFromFileSystem
                ("src/test/resources/carbon-home/repository/conf/axis2/axis2.xml");
        return new ConfigurationContextService(context, null);
    }

    @BeforeClass
    public abstract void init() throws Exception;

    protected DataSource getDataSource(DataSourceConfig config) {
        if (!isMock()) {
            PoolProperties properties = new PoolProperties();
            properties.setUrl(config.getUrl());
            properties.setDriverClassName(config.getDriverClassName());
            properties.setUsername(config.getUser());
            properties.setPassword(config.getPassword());
            return new org.apache.tomcat.jdbc.pool.DataSource(properties);
        } else {
            return new MockDataSource(config.getUrl());
        }
    }

    private void initializeCarbonContext() {
        if (System.getProperty("carbon.home") == null) {
            File file = new File("src/test/resources/carbon-home");
            if (file.exists()) {
                System.setProperty("carbon.home", file.getAbsolutePath());
            }
            file = new File("../resources/carbon-home");
            if (file.exists()) {
                System.setProperty("carbon.home", file.getAbsolutePath());
            }
            file = new File("../../resources/carbon-home");
            if (file.exists()) {
                System.setProperty("carbon.home", file.getAbsolutePath());
            }
            file = new File("../../../resources/carbon-home");
            if (file.exists()) {
                System.setProperty("carbon.home", file.getAbsolutePath());
            }
        }
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(MultitenantConstants
                .SUPER_TENANT_DOMAIN_NAME);
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(MultitenantConstants.SUPER_TENANT_ID);
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setUsername("admin");
    }

    protected DataSourceConfig readDataSourceConfig(String configLocation) throws NotificationManagementException {
        try {
            File file = new File(configLocation);
            Document doc = NotificationManagerUtil.convertToDocument(file);
            JAXBContext testDBContext = JAXBContext.newInstance(DataSourceConfig.class);
            Unmarshaller unmarshaller = testDBContext.createUnmarshaller();
            return (DataSourceConfig) unmarshaller.unmarshal(doc);
        } catch (JAXBException e) {
            throw new NotificationManagementException("Error occurred while reading data source configuration", e);
        }
    }

    private void initSQLScript() throws Exception {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = this.getDataSource().getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate("RUNSCRIPT FROM './src/test/resources/sql/h2.sql'");
        } finally {
            TestUtils.cleanupResources(conn, stmt, null);
        }
    }

    protected DataSource getDataSource() {
        return dataSource;
    }

    protected String getDatasourceLocation() throws Exception {
        if (datasourceLocation == null) {
            throw new Exception("Data source location is null!!!");
        }
        return datasourceLocation;
    }

    protected boolean isMock() {
        return mock;
    }

    // Assuming a method to get a database connection is already implemented
    protected Connection getConnection() throws SQLException {
        return DeviceManagementDAOFactory.getConnection();
    }

    /**
     * Executes an SQL update query (INSERT, UPDATE, DELETE).
     *
     * @param sql The SQL query to execute.
     * @throws SQLException If an error occurs while executing the query.
     */
    protected void executeUpdate(String sql) throws SQLException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }
} 