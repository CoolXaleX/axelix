/*
 * Copyright (C) 2025-2026 Axelix Labs
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.axelixlabs.axelix.master.utils.auth;

import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.axelixlabs.axelix.master.utils.InvalidAuthScenario;
import com.axelixlabs.axelix.master.utils.TestRestTemplateBuilder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link BeforeTestExecutionCallback} that actually performs the test. I know that we're performing test
 * in the BeforeTest callback essentially, and that has some minor consequences, but that is not that
 * much of a big deal TBH. Callback failure still going to fail CI so we're generally good.
 *
 * @author Mikhail Polivakha
 */
public class BadTokenTestExecutionCallback implements BeforeTestExecutionCallback {

    private final InvalidAuthScenario invalidAuthScenario;
    private final String path;
    private final String method;

    public BadTokenTestExecutionCallback(InvalidAuthScenario invalidAuthScenario, String path, String method) {
        this.invalidAuthScenario = invalidAuthScenario;
        this.path = path;
        this.method = method;
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        ApplicationContext applicationContext = SpringExtension.getApplicationContext(context);

        TestRestTemplateBuilder testRestTemplateBuilder = applicationContext.getBean(TestRestTemplateBuilder.class);

        ResponseEntity<Void> result = invalidAuthScenario
                .getModifier()
                .apply(testRestTemplateBuilder)
                .exchange(path, org.springframework.http.HttpMethod.valueOf(method), null, Void.class);

        assertThat(result.getStatusCode().value()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}
