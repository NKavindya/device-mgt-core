/*
 * Copyright (c) 2023, Entgra Pvt Ltd. (http://www.wso2.org) All Rights Reserved.
 *
 * Entgra Pvt Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.entgra.device.mgt.core.operation.template.mock;

import io.entgra.device.mgt.core.operation.template.DataSourceConfig;
import io.entgra.device.mgt.core.operation.template.dao.OperationTemplateDAOFactory;
import io.entgra.device.mgt.core.operation.template.util.ConnectionManagerUtils;
import io.entgra.device.mgt.core.operation.template.TestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.w3c.dom.Document;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.exceptions.DeviceManagementException;
import org.wso2.carbon.device.mgt.core.util.DeviceManagerUtil;

import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Statement;

public abstract class BaseOperationTemplatePluginTest {

    private static final Log log = LogFactory.getLog(BaseOperationTemplatePluginTest.class);

    private static final String datasourceLocation = "src/test/resources/carbon-home/repository/conf/" +
            "datasources/data-source-config.xml";

    private static boolean mock;

    @BeforeSuite
    @Parameters({"isMock"})
    public void setup(@Optional("false") boolean isMock) throws Exception {
        log.info("Setting up test suite");
        this.initDataSource();
        this.initSQLScript();
        this.initializeCarbonContext();
        this.initServices();
        mock = isMock;
        log.info("Setting up test suite done!");
    }

    protected void initDataSource() throws Exception {
        DataSource dataSource = this.getDataSource(this.readDataSourceConfig());
        Class<?> clazz1 = ConnectionManagerUtils.class;
        Field f1 = clazz1.getDeclaredField("dataSource");
        f1.setAccessible(true);
        f1.set(clazz1, dataSource);

        Class<?> clazz2 = OperationTemplateDAOFactory.class;
        Field f2 = clazz2.getDeclaredField("databaseEngine");
        f2.setAccessible(true);
        f2.set(clazz2, DeviceManagementConstants.DataBaseTypes.DB_TYPE_H2);
    }

    private void initServices() {

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
    }

    protected DataSourceConfig readDataSourceConfig() throws DeviceManagementException {
        try {
            File file = new File(BaseOperationTemplatePluginTest.datasourceLocation);
            Document doc = DeviceManagerUtil.convertToDocument(file);
            JAXBContext testDBContext = JAXBContext.newInstance(DataSourceConfig.class);
            Unmarshaller unmarshaller = testDBContext.createUnmarshaller();
            return (DataSourceConfig) unmarshaller.unmarshal(doc);
        } catch (JAXBException e) {
            throw new DeviceManagementException("Error occurred while reading data source configuration", e);
        }
    }

    private void initSQLScript() throws Exception {
        Connection conn = null;
        Statement stmt = null;
        try {
            ConnectionManagerUtils.beginDBTransaction();
            conn = ConnectionManagerUtils.getDBConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate("RUNSCRIPT FROM './src/test/resources/carbon-home/dbscripts/dm-db-h2.sql'");
        } finally {
            TestUtils.cleanupResources(conn, stmt, null);
        }
    }

    protected boolean isMock() {
        return mock;
    }

}