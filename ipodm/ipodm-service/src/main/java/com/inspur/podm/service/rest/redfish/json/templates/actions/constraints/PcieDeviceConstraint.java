/*
 * Copyright (c) 2016-2018 inspur Corporation
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

package com.inspur.podm.service.rest.redfish.json.templates.actions.constraints;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import com.inspur.podm.service.rest.redfish.json.templates.actions.PcieDevicePartialRepresentation;

@Constraint(validatedBy = PcieDeviceConstraint.PcieDeviceConstraintValidator.class)
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface PcieDeviceConstraint {
    String message() default "Cannot update PCIeDevice with empty values";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class PcieDeviceConstraintValidator implements ConstraintValidator<PcieDeviceConstraint, PcieDevicePartialRepresentation> {
        @Override
        public void initialize(PcieDeviceConstraint constraintAnnotation) {
        }

        @Override
        public boolean isValid(PcieDevicePartialRepresentation value, ConstraintValidatorContext context) {
            return value != null && value.getAssetTag() != null;
        }
    }
}
