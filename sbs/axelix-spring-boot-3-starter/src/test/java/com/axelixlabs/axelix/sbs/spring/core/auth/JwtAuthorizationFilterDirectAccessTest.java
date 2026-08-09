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
package com.axelixlabs.axelix.sbs.spring.core.auth;

import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockHttpServletRequest;

import com.axelixlabs.axelix.common.auth.core.SecurityContextExecutor;
import com.axelixlabs.axelix.sbs.spring.core.config.DirectAccessProperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class JwtAuthorizationFilterDirectAccessTest {

    private static final String ACTUATOR_BASE_PATH = "/actuator";

    @Test
    void shouldRequireJwtForAllAxelixEndpointsByDefault() {
        // given.
        TestJwtAuthorizationFilter subject = subject(new DirectAccessProperties());

        // when.
        boolean metadataSkipped = subject.shouldSkip(request("GET", "/actuator/axelix-metadata"));
        boolean schedulerSkipped = subject.shouldSkip(request("POST", "/actuator/axelix-scheduled-tasks/execute"));

        // then.
        assertThat(metadataSkipped).isFalse();
        assertThat(schedulerSkipped).isFalse();
    }

    @Test
    void shouldAllowOnlyReadOperationsForDirectDiagnostics() {
        // given.
        DirectAccessProperties properties = new DirectAccessProperties();
        properties.getDiagnostics().setEnabled(true);
        TestJwtAuthorizationFilter subject = subject(properties);

        // when.
        boolean metadataReadSkipped = subject.shouldSkip(request("GET", "/actuator/axelix-metadata"));
        boolean metricsReadSkipped = subject.shouldSkip(request("GET", "/actuator/axelix-metrics/jvm.memory.used"));
        boolean schedulerWriteSkipped = subject.shouldSkip(request("POST", "/actuator/axelix-scheduled-tasks/execute"));

        // then.
        assertThat(metadataReadSkipped).isTrue();
        assertThat(metricsReadSkipped).isTrue();
        assertThat(schedulerWriteSkipped).isFalse();
    }

    @Test
    void shouldAllowScheduledTaskWritesWhenDirectControlEnabled() {
        // given.
        DirectAccessProperties properties = new DirectAccessProperties();
        properties.getControl().setEnabled(true);
        TestJwtAuthorizationFilter subject = subject(properties);

        // when.
        boolean schedulerWriteSkipped = subject.shouldSkip(request("POST", "/actuator/axelix-scheduled-tasks/execute"));
        boolean schedulerReadSkipped = subject.shouldSkip(request("GET", "/actuator/axelix-scheduled-tasks"));
        boolean schedulerOptionsSkipped = subject.shouldSkip(request("OPTIONS", "/actuator/axelix-scheduled-tasks"));

        // then.
        assertThat(schedulerWriteSkipped).isTrue();
        assertThat(schedulerReadSkipped).isFalse();
        assertThat(schedulerOptionsSkipped).isFalse();
    }

    @Test
    void shouldKeepUnlistedControlEndpointsProtected() {
        // given.
        DirectAccessProperties properties = new DirectAccessProperties();
        properties.getControl().setEnabled(true);
        TestJwtAuthorizationFilter subject = subject(properties);

        // when.
        boolean cachesWriteSkipped = subject.shouldSkip(request("DELETE", "/actuator/axelix-caches/clear"));
        boolean gcWriteSkipped = subject.shouldSkip(request("POST", "/actuator/axelix-gc/trigger"));
        boolean loggersWriteSkipped =
                subject.shouldSkip(request("POST", "/actuator/axelix-loggers/logger/root/change-level"));

        // then.
        assertThat(cachesWriteSkipped).isFalse();
        assertThat(gcWriteSkipped).isFalse();
        assertThat(loggersWriteSkipped).isFalse();
    }

    private static TestJwtAuthorizationFilter subject(DirectAccessProperties properties) {
        return new TestJwtAuthorizationFilter(
                mock(WebIdentityAccessManager.class), mock(SecurityContextExecutor.class), properties);
    }

    private static MockHttpServletRequest request(String method, String servletPath) {
        var request = new MockHttpServletRequest(method, servletPath);
        request.setServletPath(servletPath);
        return request;
    }

    private static class TestJwtAuthorizationFilter extends JwtAuthorizationFilter {

        TestJwtAuthorizationFilter(
                WebIdentityAccessManager webIdentityAccessManager,
                SecurityContextExecutor securityContextExecutor,
                DirectAccessProperties directAccessProperties) {
            super(webIdentityAccessManager, securityContextExecutor, ACTUATOR_BASE_PATH, directAccessProperties);
        }

        boolean shouldSkip(MockHttpServletRequest request) {
            return shouldNotFilter(request);
        }
    }
}
