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

package com.intel.podm.business.redfish.services.assembly;

import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;

import java.security.SecureRandom;

import javax.annotation.PostConstruct;

import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.intel.podm.common.types.Chap;
import com.intel.podm.common.types.Ref;
import com.intel.podm.config.base.DynamicHolder;
import com.intel.podm.config.base.dto.ChapConfig;

@Component
public class ChapConfigurator {

    @Autowired
//    @Config
    private DynamicHolder<ChapConfig> chapConfigHolder;

    private RandomStringGenerator randomStringGenerator;

    @PostConstruct
    public void init() {
        randomStringGenerator = new RandomStringGenerator.Builder()
            .usingRandom(new SecureRandom()::nextInt)
            .withinRange('0', 'z')
            .filteredBy(LETTERS, DIGITS)
            .build();
    }

    public Chap generateChap() {
        Chap chap = new Chap();

        chap.setUsername(Ref.of(generateUsername()));
        chap.setSecret(Ref.of(generateSecret()));
        chap.setMutualUsername(Ref.of(generateUsername()));
        chap.setMutualSecret(Ref.of(generateSecret()));

        return chap;
    }

    public String generateUsername() {
        final int usernameLength = chapConfigHolder.get(ChapConfig.class).getUsernameLength();

        return randomStringGenerator.generate(usernameLength);
    }

    public String generateSecret() {
        final int secretLength = chapConfigHolder.get(ChapConfig.class).getSecretLength();

        return randomStringGenerator.generate(secretLength);
    }
}
