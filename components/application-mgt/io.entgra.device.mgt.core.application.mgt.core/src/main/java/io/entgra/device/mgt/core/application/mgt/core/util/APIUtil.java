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

package io.entgra.device.mgt.core.application.mgt.core.util;

import io.entgra.device.mgt.core.application.mgt.common.ApplicationType;
import io.entgra.device.mgt.core.application.mgt.common.ErrorResponse;
import io.entgra.device.mgt.core.application.mgt.common.IdentityServerResponse;
import io.entgra.device.mgt.core.application.mgt.common.config.MDMConfig;
import io.entgra.device.mgt.core.application.mgt.common.dto.ApplicationDTO;
import io.entgra.device.mgt.core.application.mgt.common.dto.ApplicationReleaseDTO;
import io.entgra.device.mgt.core.application.mgt.common.dto.IdentityServerDTO;
import io.entgra.device.mgt.core.application.mgt.common.dto.IdentityServiceProviderDTO;
import io.entgra.device.mgt.core.application.mgt.common.exception.ApplicationManagementException;
import io.entgra.device.mgt.core.application.mgt.common.exception.InvalidConfigurationException;
import io.entgra.device.mgt.core.application.mgt.common.response.Application;
import io.entgra.device.mgt.core.application.mgt.common.response.ApplicationRelease;
import io.entgra.device.mgt.core.application.mgt.common.services.*;
import io.entgra.device.mgt.core.application.mgt.common.wrapper.*;
import io.entgra.device.mgt.core.application.mgt.core.config.ConfigurationManager;
import io.entgra.device.mgt.core.application.mgt.core.config.IdentityServiceProvider;
import io.entgra.device.mgt.core.application.mgt.core.exception.BadRequestException;
import io.entgra.device.mgt.core.application.mgt.core.exception.UnexpectedServerErrorException;
import io.entgra.device.mgt.core.application.mgt.core.internal.DataHolder;
import io.entgra.device.mgt.core.application.mgt.core.serviceprovider.ISServiceProviderApplicationService;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.DeviceManagementException;
import io.entgra.device.mgt.core.device.mgt.common.metadata.mgt.MetadataManagementService;
import io.entgra.device.mgt.core.device.mgt.core.dto.DeviceType;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.routines.UrlValidator;
import org.wso2.carbon.context.PrivilegedCarbonContext;

import javax.ws.rs.core.Response;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Holds util methods required for ApplicationDTO-Mgt API component.
 */
public class APIUtil {

    private static Log log = LogFactory.getLog(APIUtil.class);

    private static volatile SPApplicationManager SPApplicationManager;
    private static volatile ApplicationManager applicationManager;
    private static volatile ApplicationStorageManager applicationStorageManager;
    private static volatile SubscriptionManager subscriptionManager;
    private static volatile ReviewManager reviewManager;
    private static volatile AppmDataHandler appmDataHandler;
    private static volatile VPPApplicationManager vppApplicationManager;
    private static volatile MetadataManagementService metadataManagementService;
    private static volatile FileTransferService fileTransferService;

    public static SPApplicationManager getSPApplicationManager() {
        if (SPApplicationManager == null) {
            synchronized (APIUtil.class) {
                if (SPApplicationManager == null) {
                    PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
                    SPApplicationManager =
                            (SPApplicationManager) ctx.getOSGiService(SPApplicationManager.class, null);
                    if (SPApplicationManager == null) {
                        String msg = "ApplicationDTO Manager service has not initialized.";
                        log.error(msg);
                        throw new IllegalStateException(msg);
                    }
                }
            }
        }
        return SPApplicationManager;
    }

    public static ApplicationManager getApplicationManager() {
        try {
            if (applicationManager == null) {
                synchronized (APIUtil.class) {
                    if (applicationManager == null) {
                        applicationManager = ApplicationManagementUtil.getApplicationManagerInstance();
                        if (applicationManager == null) {
                            String msg = "ApplicationDTO Manager service has not initialized.";
                            log.error(msg);
                            throw new IllegalStateException(msg);
                        }
                    }
                }
            }
        } catch (Exception e) {
            String msg = "Error occurred while getting the application manager";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return applicationManager;
    }

    public static MetadataManagementService getMetadataManager() {
        if (metadataManagementService == null) {
            synchronized (APIUtil.class) {
                if (metadataManagementService == null) {
                    PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
                    metadataManagementService =
                            (MetadataManagementService) ctx.getOSGiService(MetadataManagementService.class, null);
                    if (metadataManagementService == null) {
                        String msg = "MetadataManagement Manager service has not initialized.";
                        log.error(msg);
                        throw new IllegalStateException(msg);
                    }
                }
            }
        }
        return metadataManagementService;
    }

    /**
     * To get the ApplicationDTO Storage Manager from the osgi context.
     * @return ApplicationStoreManager instance in the current osgi context.
     */
    public static ApplicationStorageManager getApplicationStorageManager() {

        try {
            if (applicationStorageManager == null) {
                synchronized (DAOUtil.class) {
                    if (applicationStorageManager == null) {
                        applicationStorageManager = ApplicationManagementUtil
                                .getApplicationStorageManagerInstance();
                        if (applicationStorageManager == null) {
                            String msg = "ApplicationDTO Storage Manager service has not initialized.";
                            log.error(msg);
                            throw new IllegalStateException(msg);
                        }
                    }
                }
            }
        } catch (Exception e) {
            String msg = "Error occurred while getting the application store manager";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return applicationStorageManager;
    }

    public static Response getResponse(Exception ex, Response.Status status) {
        return getResponse(ex.getMessage(), status);
    }

    public static Response getResponse(String message, Response.Status status) {
        ErrorResponse errorMessage = new ErrorResponse();
        errorMessage.setMessage(message);
        if (status == null) {
            status = Response.Status.INTERNAL_SERVER_ERROR;
        }
        errorMessage.setCode(status.getStatusCode());
        return Response.status(status).entity(errorMessage).build();
    }

    /**
     * To get the Subscription Manager from the osgi context.
     * @return SubscriptionManager instance in the current osgi context.
     */
    public static SubscriptionManager getSubscriptionManager() {
        if (subscriptionManager == null) {
            synchronized (APIUtil.class) {
                if (subscriptionManager == null) {
                    PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
                    subscriptionManager =
                            (SubscriptionManager) ctx.getOSGiService(SubscriptionManager.class, null);
                    if (subscriptionManager == null) {
                        String msg = "Subscription Manager service has not initialized.";
                        log.error(msg);
                        throw new IllegalStateException(msg);
                    }
                }
            }
        }

        return subscriptionManager;
    }

    /**
     * To get the Review Manager from the osgi context.
     * @return ReviewManager instance in the current osgi context.
     */
    public static ReviewManager getReviewManager() {
        if (reviewManager == null) {
            synchronized (APIUtil.class) {
                if (reviewManager == null) {
                    PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
                    reviewManager =
                            (ReviewManager) ctx.getOSGiService(ReviewManager.class, null);
                    if (reviewManager == null) {
                        String msg = "Review Manager service has not initialized.";
                        log.error(msg);
                        throw new IllegalStateException(msg);
                    }
                }
            }
        }

        return reviewManager;
    }

    public static VPPApplicationManager getVPPManager() {
        try {
            if (vppApplicationManager == null) {
                synchronized (APIUtil.class) {
                    if (vppApplicationManager == null) {
                        vppApplicationManager = ApplicationManagementUtil.getVPPManagerInstance();
                        if (vppApplicationManager == null) {
                            String msg = "Vpp Manager service has not initialized.";
                            log.error(msg);
                            throw new IllegalStateException(msg);
                        }
                    }
                }
            }
        } catch (Exception e) {
            String msg = "Error occurred while getting the vpp manager";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return vppApplicationManager;
    }


    /**
     * To get the DataHandler from the osgi context.
     * @return AppmDataHandler instance in the current osgi context.
     */
    public static AppmDataHandler getDataHandler() {
        if (appmDataHandler == null) {
            synchronized (APIUtil.class) {
                if (appmDataHandler == null) {
                    PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
                    appmDataHandler =
                            (AppmDataHandler) ctx.getOSGiService(AppmDataHandler.class, null);
                    if (appmDataHandler == null) {
                        String msg = "Config Manager service has not initialized.";
                        log.error(msg);
                        throw new IllegalStateException(msg);
                    }
                }
            }
        }

        return appmDataHandler;
    }

    public static <T> DeviceType getDeviceTypeData(T deviceTypeAttr)
            throws BadRequestException, UnexpectedServerErrorException {
        List<DeviceType> deviceTypes;
        try {
            deviceTypes = DataHolder.getInstance().getDeviceManagementService().getDeviceTypes();
            if (deviceTypeAttr instanceof String) {
                for (DeviceType dt : deviceTypes) {
                    if (dt.getName().equals(deviceTypeAttr)) {
                        return dt;
                    }
                }
            } else if (deviceTypeAttr instanceof Integer) {
                for (DeviceType dt : deviceTypes) {
                    if (dt.getId() == (Integer) deviceTypeAttr) {
                        return dt;
                    }
                }
            } else {
                String msg = "Invalid device type class is received. Device type class: " + deviceTypeAttr.getClass()
                        .getName();
                log.error(msg);
                throw new BadRequestException(msg);
            }
            String msg = "Invalid device type Attribute is found with the request. Device Type attribute: "
                    + deviceTypeAttr;
            log.error(msg);
            throw new BadRequestException(msg);
        } catch (DeviceManagementException e) {
            String msg = "Error occured when getting device types which are supported by the Entgra IoTS";
            log.error(msg, e);
            throw new UnexpectedServerErrorException(msg, e);
        }
    }

    public static <T> ApplicationDTO convertToAppDTO(T param)
            throws BadRequestException, UnexpectedServerErrorException {
        ApplicationDTO applicationDTO = new ApplicationDTO();
        List<ApplicationReleaseDTO> applicationReleaseEntities;

        if (param instanceof ApplicationWrapper){
            ApplicationWrapper applicationWrapper = (ApplicationWrapper) param;
            DeviceType deviceType = getDeviceTypeData(applicationWrapper.getDeviceType());
            applicationDTO.setName(ApplicationManagementUtil.sanitizeName(applicationWrapper.getName(),
                    Constants.ApplicationProperties.NAME));
            applicationDTO.setDescription(applicationWrapper.getDescription());
            applicationDTO.setAppCategories(applicationWrapper.getCategories());
            applicationDTO.setType(ApplicationType.ENTERPRISE.toString());
            applicationDTO.setSubType(applicationWrapper.getSubMethod());
            applicationDTO.setPaymentCurrency(applicationWrapper.getPaymentCurrency());
            applicationDTO.setTags(applicationWrapper.getTags());
            applicationDTO.setUnrestrictedRoles(applicationWrapper.getUnrestrictedRoles());
            applicationDTO.setDeviceTypeId(deviceType.getId());
            applicationReleaseEntities = applicationWrapper.getEntAppReleaseWrappers()
                    .stream().map(APIUtil::releaseWrapperToReleaseDTO).collect(Collectors.toList());
            applicationDTO.setApplicationReleaseDTOs(applicationReleaseEntities);
        } else if (param instanceof WebAppWrapper){
            WebAppWrapper webAppWrapper = (WebAppWrapper) param;
            applicationDTO.setName(ApplicationManagementUtil.sanitizeName(webAppWrapper.getName(),
                    Constants.ApplicationProperties.NAME));
            applicationDTO.setDescription(webAppWrapper.getDescription());
            applicationDTO.setAppCategories(webAppWrapper.getCategories());
            applicationDTO.setSubType(webAppWrapper.getSubMethod());
            applicationDTO.setPaymentCurrency(webAppWrapper.getPaymentCurrency());
            applicationDTO.setType(webAppWrapper.getType());
            applicationDTO.setTags(webAppWrapper.getTags());
            applicationDTO.setUnrestrictedRoles(webAppWrapper.getUnrestrictedRoles());
            applicationReleaseEntities = webAppWrapper.getWebAppReleaseWrappers()
                    .stream().map(APIUtil::releaseWrapperToReleaseDTO).collect(Collectors.toList());
            applicationDTO.setApplicationReleaseDTOs(applicationReleaseEntities);
        } else if (param instanceof PublicAppWrapper) {
            PublicAppWrapper publicAppWrapper = (PublicAppWrapper) param;
            DeviceType deviceType = getDeviceTypeData(publicAppWrapper.getDeviceType());
            applicationDTO.setName(ApplicationManagementUtil.sanitizeName(publicAppWrapper.getName(),
                    Constants.ApplicationProperties.NAME));
            applicationDTO.setDescription(publicAppWrapper.getDescription());
            applicationDTO.setAppCategories(publicAppWrapper.getCategories());
            applicationDTO.setType(ApplicationType.PUBLIC.toString());
            applicationDTO.setSubType(publicAppWrapper.getSubMethod());
            applicationDTO.setPaymentCurrency(publicAppWrapper.getPaymentCurrency());
            applicationDTO.setTags(publicAppWrapper.getTags());
            applicationDTO.setUnrestrictedRoles(publicAppWrapper.getUnrestrictedRoles());
            applicationDTO.setDeviceTypeId(deviceType.getId());
            applicationReleaseEntities = publicAppWrapper.getPublicAppReleaseWrappers()
                    .stream().map(APIUtil::releaseWrapperToReleaseDTO).collect(Collectors.toList());
            applicationDTO.setApplicationReleaseDTOs(applicationReleaseEntities);
        } else if (param instanceof CustomAppWrapper){
            CustomAppWrapper customAppWrapper = (CustomAppWrapper) param;
            DeviceType deviceType = getDeviceTypeData(customAppWrapper.getDeviceType());
            applicationDTO.setName(ApplicationManagementUtil.sanitizeName(customAppWrapper.getName(),
                    Constants.ApplicationProperties.NAME));
            applicationDTO.setDescription(customAppWrapper.getDescription());
            applicationDTO.setAppCategories(customAppWrapper.getCategories());
            applicationDTO.setType(ApplicationType.CUSTOM.toString());
            applicationDTO.setSubType(customAppWrapper.getSubMethod());
            applicationDTO.setPaymentCurrency(customAppWrapper.getPaymentCurrency());
            applicationDTO.setTags(customAppWrapper.getTags());
            applicationDTO.setUnrestrictedRoles(customAppWrapper.getUnrestrictedRoles());
            applicationDTO.setDeviceTypeId(deviceType.getId());
            applicationReleaseEntities = customAppWrapper.getCustomAppReleaseWrappers()
                    .stream().map(APIUtil::releaseWrapperToReleaseDTO).collect(Collectors.toList());
            applicationDTO.setApplicationReleaseDTOs(applicationReleaseEntities);
        }
        return applicationDTO;
    }

    public static <T> ApplicationReleaseDTO releaseWrapperToReleaseDTO(T param){
        ApplicationReleaseDTO applicationReleaseDTO = new ApplicationReleaseDTO();
        if (param instanceof EntAppReleaseWrapper){
            EntAppReleaseWrapper entAppReleaseWrapper = (EntAppReleaseWrapper) param;
            applicationReleaseDTO.setDescription(entAppReleaseWrapper.getDescription());
            applicationReleaseDTO.setReleaseType(entAppReleaseWrapper.getReleaseType());
            applicationReleaseDTO.setPrice(entAppReleaseWrapper.getPrice());
            applicationReleaseDTO.setIsSharedWithAllTenants(entAppReleaseWrapper.getIsSharedWithAllTenants());
            applicationReleaseDTO.setMetaData(entAppReleaseWrapper.getMetaData());
            applicationReleaseDTO.setSupportedOsVersions(entAppReleaseWrapper.getSupportedOsVersions());
            //Setting version number value specifically for windows type and in an instance of android and ios it will be null
            applicationReleaseDTO.setVersion(entAppReleaseWrapper.getVersion());
            //Setting package name value specifically for windows type and in an instance of android and ios it will be null
            applicationReleaseDTO.setPackageName(entAppReleaseWrapper.getPackageName());
        } else if (param instanceof WebAppReleaseWrapper){
            WebAppReleaseWrapper webAppReleaseWrapper = (WebAppReleaseWrapper) param;
            applicationReleaseDTO.setDescription(webAppReleaseWrapper.getDescription());
            applicationReleaseDTO.setReleaseType(webAppReleaseWrapper.getReleaseType());
            applicationReleaseDTO.setVersion(webAppReleaseWrapper.getVersion());
            applicationReleaseDTO.setPrice(webAppReleaseWrapper.getPrice());
            applicationReleaseDTO.setInstallerName(webAppReleaseWrapper.getUrl());
            applicationReleaseDTO.setIsSharedWithAllTenants(webAppReleaseWrapper.getIsSharedWithAllTenants());
            applicationReleaseDTO.setSupportedOsVersions(Constants.ANY);
            applicationReleaseDTO.setPackageName(Constants.DEFAULT_PCK_NAME);
            applicationReleaseDTO.setMetaData(webAppReleaseWrapper.getMetaData());
        } else if (param instanceof PublicAppReleaseWrapper) {
            PublicAppReleaseWrapper publicAppReleaseWrapper = (PublicAppReleaseWrapper) param;
            applicationReleaseDTO.setDescription(publicAppReleaseWrapper.getDescription());
            applicationReleaseDTO.setReleaseType(publicAppReleaseWrapper.getReleaseType());
            applicationReleaseDTO.setVersion(publicAppReleaseWrapper.getVersion());
            applicationReleaseDTO.setPackageName(publicAppReleaseWrapper.getPackageName());
            applicationReleaseDTO.setPrice(publicAppReleaseWrapper.getPrice());
            applicationReleaseDTO.setIsSharedWithAllTenants(publicAppReleaseWrapper.getIsSharedWithAllTenants());
            applicationReleaseDTO.setMetaData(publicAppReleaseWrapper.getMetaData());
            applicationReleaseDTO.setSupportedOsVersions(publicAppReleaseWrapper.getSupportedOsVersions());
        } else if (param instanceof CustomAppReleaseWrapper) {
            CustomAppReleaseWrapper customAppReleaseWrapper = (CustomAppReleaseWrapper) param;
            applicationReleaseDTO.setDescription(customAppReleaseWrapper.getDescription());
            applicationReleaseDTO.setReleaseType(customAppReleaseWrapper.getReleaseType());
            applicationReleaseDTO.setVersion(customAppReleaseWrapper.getVersion());
            applicationReleaseDTO.setSupportedOsVersions(Constants.ANY);
            applicationReleaseDTO.setPackageName(customAppReleaseWrapper.getPackageName());
            applicationReleaseDTO.setPrice(customAppReleaseWrapper.getPrice());
            applicationReleaseDTO.setIsSharedWithAllTenants(customAppReleaseWrapper.getIsSharedWithAllTenants());
            applicationReleaseDTO.setMetaData(customAppReleaseWrapper.getMetaData());
        }
        return applicationReleaseDTO;
    }

    public static IdentityServiceProviderDTO identityServiceProviderToDTO(IdentityServiceProvider identityServiceProvider)
            throws InvalidConfigurationException {
        ISServiceProviderApplicationService serviceProviderApplicationService =
                ISServiceProviderApplicationService.of(identityServiceProvider.getProviderName());
        IdentityServiceProviderDTO identityServiceProviderDTO = new IdentityServiceProviderDTO();
        identityServiceProviderDTO.setName(identityServiceProvider.getProviderName());
        identityServiceProviderDTO.setRequiredApiParams(serviceProviderApplicationService.getRequiredApiParams());
        return identityServiceProviderDTO;
    }

    public static IdentityServerResponse identityServerDtoToIdentityServerResponse(IdentityServerDTO identityServerDTO) {
        IdentityServerResponse identityServer = new IdentityServerResponse();
        identityServer.setId(identityServerDTO.getId());
        identityServer.setProviderName(identityServerDTO.getProviderName());
        identityServer.setName(identityServerDTO.getName());
        identityServer.setDescription(identityServerDTO.getDescription());
        identityServer.setUrl(identityServerDTO.getUrl());
        identityServer.setApiParamList(identityServerDTO.getApiParams());
        identityServer.setUsername(identityServerDTO.getUsername());
        IdentityServiceProvider identityServiceProvider = ConfigurationManager.getInstance().getIdentityServerConfiguration()
                .getIdentityServerDetailByProviderName(identityServerDTO.getProviderName());
        String serviceProviderAppsUrl = identityServerDTO.getUrl() + identityServiceProvider.getServiceProvidersPageUri();
        identityServer.setServiceProviderAppsUrl(serviceProviderAppsUrl);
        return identityServer;
    }

    public static Application appDtoToAppResponse(ApplicationDTO applicationDTO) throws ApplicationManagementException {

        Application application = new Application();
        if (!ApplicationType.WEB_CLIP.toString().equals(applicationDTO.getType())) {
            DeviceType deviceType = getDeviceTypeData(applicationDTO.getDeviceTypeId());
            application.setDeviceType(deviceType.getName());
        } else {
            application.setDeviceType(Constants.ANY);
        }
        application.setId(applicationDTO.getId());
        application.setName(applicationDTO.getName());
        application.setDescription(applicationDTO.getDescription());
        application.setCategories(applicationDTO.getAppCategories());
        application.setType(applicationDTO.getType());
        application.setSubMethod(applicationDTO.getSubType());
        application.setPaymentCurrency(applicationDTO.getPaymentCurrency());
        application.setTags(applicationDTO.getTags());
        application.setUnrestrictedRoles(applicationDTO.getUnrestrictedRoles());
        application.setRating(applicationDTO.getAppRating());
        application.setFavourite(applicationDTO.isFavourite());
        if (applicationDTO.getApplicationReleaseDTOs() != null && !applicationDTO.getApplicationReleaseDTOs().isEmpty()) {
            application.setInstallerName(applicationDTO.getApplicationReleaseDTOs().get(0).getInstallerName());
        }
        List<ApplicationRelease> applicationReleases = new ArrayList<>();
        if (ApplicationType.PUBLIC.toString().equals(applicationDTO.getType()) && application.getCategories()
                .contains(Constants.GOOGLE_PLAY_SYNCED_APP)) {
            application.setAndroidEnterpriseApp(true);
        }
        if (ApplicationType.PUBLIC.toString().equals(applicationDTO.getType()) && application.getCategories()
                .contains(Constants.ApplicationProperties.APPLE_STORE_SYNCED_APP_CATEGORY)) {
            application.setExternalAppStoreApp(true);
        }
        for (ApplicationReleaseDTO applicationReleaseDTO : applicationDTO.getApplicationReleaseDTOs()) {
            applicationReleases.add(releaseDtoToRelease(applicationReleaseDTO));
        }
        application.setApplicationReleases(applicationReleases);
        application.setPackageName(applicationDTO.getPackageName());
        return application;
    }

    public static ApplicationRelease releaseDtoToRelease(ApplicationReleaseDTO applicationReleaseDTO)
            throws ApplicationManagementException {
        int tenantId = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId(true);
        String basePath = getArtifactDownloadBaseURL() + tenantId + Constants.FORWARD_SLASH + applicationReleaseDTO
                .getAppHashValue() + Constants.FORWARD_SLASH;

        List<String> screenshotPaths = new ArrayList<>();
        ApplicationRelease applicationRelease = new ApplicationRelease();

        applicationRelease.setDescription(applicationReleaseDTO.getDescription());
        applicationRelease.setVersion(applicationReleaseDTO.getVersion());
        applicationRelease.setPackageName(applicationReleaseDTO.getPackageName());
        applicationRelease.setUuid(applicationReleaseDTO.getUuid());
        applicationRelease.setReleaseType(applicationReleaseDTO.getReleaseType());
        applicationRelease.setPrice(applicationReleaseDTO.getPrice());
        applicationRelease.setIsSharedWithAllTenants(applicationReleaseDTO.getIsSharedWithAllTenants());
        applicationRelease.setMetaData(applicationReleaseDTO.getMetaData());
        applicationRelease.setCurrentStatus(applicationReleaseDTO.getCurrentState());
        applicationRelease.setIsSharedWithAllTenants(applicationReleaseDTO.getIsSharedWithAllTenants());
        applicationRelease.setSupportedOsVersions(applicationReleaseDTO.getSupportedOsVersions());
        applicationRelease.setRating(applicationReleaseDTO.getRating());
        applicationRelease.setIconPath(
                basePath + Constants.ICON_ARTIFACT + Constants.FILE_NAME_PARAM +
                        URLEncoder.encode(applicationReleaseDTO.getIconName(), StandardCharsets.UTF_8));
        if (!StringUtils.isEmpty(applicationReleaseDTO.getBannerName())) {
            applicationRelease.setBannerPath(
                    basePath + Constants.BANNER_ARTIFACT + Constants.FILE_NAME_PARAM +
                            URLEncoder.encode(applicationReleaseDTO.getBannerName(), StandardCharsets.UTF_8));
        }
        applicationRelease.setInstallerPath(
                constructInstallerPath(applicationReleaseDTO.getInstallerName(), applicationReleaseDTO.getAppHashValue()));
        if (!StringUtils.isEmpty(applicationReleaseDTO.getScreenshotName1())) {
            screenshotPaths.add(
                    basePath + Constants.SCREENSHOT_ARTIFACT + 1 + Constants.FILE_NAME_PARAM +
                            URLEncoder.encode(applicationReleaseDTO.getScreenshotName1(), StandardCharsets.UTF_8));
        }
        if (!StringUtils.isEmpty(applicationReleaseDTO.getScreenshotName2())) {
            screenshotPaths.add(
                    basePath + Constants.SCREENSHOT_ARTIFACT + 2 + Constants.FILE_NAME_PARAM +
                            URLEncoder.encode(applicationReleaseDTO.getScreenshotName2(), StandardCharsets.UTF_8));
        }
        if (!StringUtils.isEmpty(applicationReleaseDTO.getScreenshotName3())) {
            screenshotPaths.add(
                    basePath + Constants.SCREENSHOT_ARTIFACT + 3 + Constants.FILE_NAME_PARAM +
                            URLEncoder.encode(applicationReleaseDTO.getScreenshotName3(), StandardCharsets.UTF_8));
        }
        applicationRelease.setScreenshots(screenshotPaths);
        return applicationRelease;
    }

    /**
     * Construct installer path
     * @param installerName Installer name
     * @param appHash Application hash
     * @return Constructed installer path value
     * @throws ApplicationManagementException Throws when error encountered while constructing installer path
     */
    public static String constructInstallerPath(String installerName, String appHash) throws ApplicationManagementException {
        int tenantId = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId(true);
        UrlValidator urlValidator = new UrlValidator();
        String basePath = getArtifactDownloadBaseURL() + tenantId + Constants.FORWARD_SLASH +
                appHash + Constants.FORWARD_SLASH;
        return urlValidator.isValid(installerName)
                ? installerName
                : basePath + Constants.APP_ARTIFACT + Constants.FILE_NAME_PARAM +
                URLEncoder.encode(installerName, StandardCharsets.UTF_8);
    }

    public static String getArtifactDownloadBaseURL() throws ApplicationManagementException {
        String host = System.getProperty(Constants.IOT_CORE_HOST);
        MDMConfig mdmConfig = ConfigurationManager.getInstance().getConfiguration().getMdmConfig();
        String port;
        if (Constants.HTTP_PROTOCOL.equals(mdmConfig.getArtifactDownloadProtocol())){
            port = System.getProperty(Constants.IOT_CORE_HTTP_PORT);
        } else if( Constants.HTTPS_PROTOCOL.equals(mdmConfig.getArtifactDownloadProtocol())){
            port = System.getProperty(Constants.IOT_CORE_HTTPS_PORT);
        } else {
            String msg = "In order to download application artifacts invalid protocols are defined.";
            log.error(msg);
            throw new ApplicationManagementException(msg);
        }
        String artifactDownloadEndpoint = mdmConfig.getArtifactDownloadEndpoint();
        return mdmConfig.getArtifactDownloadProtocol() + "://" + host + ":" + port
                + artifactDownloadEndpoint + Constants.FORWARD_SLASH;
    }

    /**
     * To create the application icon path.
     *
     * @param applicationReleaseDTO {@link ApplicationReleaseDTO}
     * @param tenantId tenant ID
     * @return iconPath constructed icon path.
     */
    public static String createAppIconPath(ApplicationReleaseDTO applicationReleaseDTO, int tenantId) throws ApplicationManagementException {
        String basePath = getArtifactDownloadBaseURL() + tenantId + Constants.FORWARD_SLASH + applicationReleaseDTO
                .getAppHashValue() + Constants.FORWARD_SLASH;
        return basePath + Constants.ICON_ARTIFACT + Constants.FORWARD_SLASH + applicationReleaseDTO.getIconName();
    }

    public static MetadataManagementService getMetadataManagementService() {
        if (metadataManagementService == null) {
            synchronized (APIUtil.class) {
                if (metadataManagementService == null) {
                    PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
                    metadataManagementService = (MetadataManagementService) ctx.getOSGiService(
                            MetadataManagementService.class, null);
                    if (metadataManagementService == null) {
                        throw new IllegalStateException("Metadata Management service not initialized.");
                    }
                }
            }
        }
        return metadataManagementService;
    }

    public static FileTransferService getFileTransferService() {
        if (fileTransferService == null) {
            synchronized (APIUtil.class) {
                if (fileTransferService == null) {
                    PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
                    fileTransferService = (FileTransferService) ctx.getOSGiService(FileTransferService.class, null);
                }
            }
        }
        return fileTransferService;
    }
}
