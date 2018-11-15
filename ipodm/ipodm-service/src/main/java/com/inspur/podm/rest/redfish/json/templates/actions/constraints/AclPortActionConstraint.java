/*
 * Copyright (c) 2017-2018 inspur Corporation
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

package com.inspur.podm.rest.redfish.json.templates.actions.constraints;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import com.inspur.podm.rest.redfish.json.templates.actions.AclPortActionJson;

@Constraint(validatedBy = AclPortActionConstraint.AclPortActionConstraintValidator.class)
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface AclPortActionConstraint {
    String message() default "Port must be specified";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class AclPortActionConstraintValidator implements ConstraintValidator<AclPortActionConstraint, AclPortActionJson> {
        @Override
        public void initialize(AclPortActionConstraint constraint) {
        }

        @Override
        public boolean isValid(AclPortActionJson value, ConstraintValidatorContext context) {
            return value != null && value.getPortContext() != null;
        }
    }
}
