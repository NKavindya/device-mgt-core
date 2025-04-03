package io.entgra.device.mgt.core.device.mgt.api.jaxrs.service.impl;

import io.entgra.device.mgt.core.device.mgt.api.jaxrs.beans.ErrorResponse;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.beans.OperationList;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.service.api.DeviceFeatureOperationService;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.service.impl.util.InputValidationException;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.service.impl.util.RequestValidationUtil;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.util.DeviceMgtAPIUtils;
import io.entgra.device.mgt.core.device.mgt.common.DeviceIdentifier;
import io.entgra.device.mgt.core.device.mgt.common.OperationLogFilters;
import io.entgra.device.mgt.core.device.mgt.common.PaginationRequest;
import io.entgra.device.mgt.core.device.mgt.common.PaginationResult;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.DeviceManagementException;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.DeviceTypeNotFoundException;
import io.entgra.device.mgt.core.device.mgt.common.operation.mgt.Operation;
import io.entgra.device.mgt.core.device.mgt.common.operation.mgt.OperationManagementException;
import io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/devices")
public class DeviceFeatureOperationServiceImpl implements DeviceFeatureOperationService {

    private static final Log log = LogFactory.getLog(DeviceFeatureOperationServiceImpl.class);

    @GET
    @Path("/{type}/{id}/operations")
    @Override
    public Response getDeviceOperations(
            @PathParam("type") @Size(max = 45) String type,
            @PathParam("id") @Size(max = 45) String id,
            @HeaderParam("If-Modified-Since") String ifModifiedSince,
            @QueryParam("offset") int offset,
            @QueryParam("limit") int limit,
            @QueryParam("owner") String owner,
            @QueryParam("ownership") String ownership,
            @QueryParam("createdFrom") Long createdFrom,
            @QueryParam("createdTo") Long createdTo,
            @QueryParam("updatedFrom") Long updatedFrom,
            @QueryParam("updatedTo") Long updatedTo,
            @QueryParam("operationCode") List<String> operationCode,
            @QueryParam("operationStatus") List<String> status) {
        OperationList operationsList = new OperationList();
        RequestValidationUtil requestValidationUtil = new RequestValidationUtil();
        RequestValidationUtil.validatePaginationParameters(offset, limit);
        PaginationRequest request = new PaginationRequest(offset, limit);
        if(owner != null){
            request.setOwner(owner);
        }
        try {
            //validating the operation log filters
            OperationLogFilters olf = requestValidationUtil.validateOperationLogFilters(operationCode, createdFrom,
                    createdTo, updatedFrom, updatedTo, status, type);
            request.setOperationLogFilters(olf);
            RequestValidationUtil.validateDeviceIdentifier(type, id);
            DeviceManagementProviderService dms = DeviceMgtAPIUtils.getDeviceManagementService();
            if (!StringUtils.isBlank(ownership)) {
                request.setOwnership(ownership);
            }
            PaginationResult result = dms.getOperations(new DeviceIdentifier(id, type), request);
            operationsList.setList((List<? extends Operation>) result.getData());
            operationsList.setCount(result.getRecordsTotal());
            return Response.status(Response.Status.OK).entity(operationsList).build();
        } catch (OperationManagementException e) {
            String msg = "Error occurred while fetching the operations for the '" + type + "' device, which " +
                    "carries the id '" + id + "'";
            log.error(msg, e);
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(msg).build()).build();
        } catch (InputValidationException e) {
            String msg = "Error occurred while fetching the operations for the type : " + type + " device";
            log.error(msg, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).build();
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while retrieving the list of [" + type + "] features with params " +
                    "{featureType: operation, hidden: true}";
            log.error(msg, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).build();
        } catch (DeviceTypeNotFoundException e) {
            String msg = "No device type found with name : " + type ;
            log.error(msg, e);
            return Response.status(Response.Status.NOT_FOUND).entity(msg).build();
        }
    }
}
