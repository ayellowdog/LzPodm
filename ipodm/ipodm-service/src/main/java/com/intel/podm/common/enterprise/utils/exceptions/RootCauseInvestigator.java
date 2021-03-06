/*
 * Copyright (c) 2017-2018 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intel.podm.common.enterprise.utils.exceptions;

import static org.apache.commons.lang3.exception.ExceptionUtils.getThrowableList;

import java.util.Optional;

import com.intel.podm.common.types.redfish.ExternalServiceError;
import com.intel.podm.common.types.redfish.ExternalServiceErrorCarryingException;

public final class RootCauseInvestigator {
    private RootCauseInvestigator() {
    }

    public static Optional<ExternalServiceError> tryGetExternalServiceErrorInExceptionStack(Throwable e) {
        return tryGetRedfishErrorResponseCarryingException(e).map(ExternalServiceErrorCarryingException::getExternalServiceError);
    }

    @SuppressWarnings({"unchecked"})
    private static Optional<ExternalServiceErrorCarryingException> tryGetRedfishErrorResponseCarryingException(Throwable e) {
        return getThrowableList(e).stream()
            .filter(throwable -> throwable instanceof ExternalServiceErrorCarryingException)
            .map(throwable -> (ExternalServiceErrorCarryingException) throwable)
            .findFirst();
    }
}
