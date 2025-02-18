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

package io.entgra.device.mgt.core.certificate.mgt.core.impl;

import io.entgra.device.mgt.core.certificate.mgt.core.cache.CertificateCacheManager;
import io.entgra.device.mgt.core.certificate.mgt.core.common.BaseDeviceManagementCertificateTest;
import io.entgra.device.mgt.core.certificate.mgt.core.dao.CertificateManagementDAOFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;

/**
 * This class tests CertificateCache manager methods
 */
public class CertificateCacheManagerImplTests extends BaseDeviceManagementCertificateTest {

    private CertificateCacheManager manager;

    @BeforeClass
    @Override
    public void init() throws Exception {
        initDataSource();
        CertificateManagementDAOFactory.init(this.getDataSource());
        manager = io.entgra.device.mgt.core.certificate.mgt.core.cache.impl.CertificateCacheManagerImpl.getInstance();
        Assert.assertNotNull(manager);
    }

}
