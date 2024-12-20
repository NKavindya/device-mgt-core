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
package io.entgra.device.mgt.core.certificate.mgt.cert.admin.api.util;

import io.entgra.device.mgt.core.certificate.mgt.cert.admin.api.InputValidationException;
import io.entgra.device.mgt.core.certificate.mgt.cert.admin.api.beans.ErrorResponse;

public class RequestValidationUtil {

    public static void validateSerialNumber(String serialNumber) {
        if (serialNumber == null || serialNumber.isEmpty()) {
            throw new InputValidationException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(
                            "Serial number cannot be null or empty").build());
        }
    }

    public static void validatePaginationInfo(int offset, int limit) {
        if (offset < 0) {
            throw new InputValidationException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(
                            "Offset number cannot be negative").build());
        }
        if (limit < 0) {
            throw new InputValidationException(
                    new ErrorResponse.ErrorResponseBuilder().setCode(400l).setMessage(
                            "Limit number cannot be negative").build());
        }
    }


}
