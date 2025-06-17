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

package io.entgra.device.mgt.core.notification.mgt.core.common;

import io.entgra.device.mgt.core.notification.mgt.core.dao.NotificationManagementDAOFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.wso2.carbon.base.MultitenantConstants;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Base test class for notification management tests
 */
public class BaseNotificationManagementTest {

    private static final Log log = LogFactory.getLog(BaseNotificationManagementTest.class);
    protected DataSource dataSource;

    @BeforeClass
    public void init() throws Exception {
        initDataSource();
        NotificationManagementDAOFactory.init(this.dataSource);
    }

    protected void initDataSource() throws Exception {
        // Initialize test data source
        // This should be implemented based on your test database setup
    }

    protected void cleanupDatabase() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM DM_NOTIFICATION");
            stmt.execute("DELETE FROM DM_NOTIFICATION_CONFIG");
            stmt.execute("DELETE FROM DM_NOTIFICATION_ARCHIVAL");
        }
    }
} 