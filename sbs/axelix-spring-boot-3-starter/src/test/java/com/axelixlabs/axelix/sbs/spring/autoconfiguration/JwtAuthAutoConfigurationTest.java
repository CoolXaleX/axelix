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
package com.axelixlabs.axelix.sbs.spring.autoconfiguration;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import com.axelixlabs.axelix.common.auth.core.SecurityContextExecutor;
import com.axelixlabs.axelix.common.auth.service.Authorizer;
import com.axelixlabs.axelix.common.auth.service.JwtDecoderService;
import com.axelixlabs.axelix.common.auth.service.JwtEncoderService;
import com.axelixlabs.axelix.sbs.spring.core.auth.AuthorityResolver;
import com.axelixlabs.axelix.sbs.spring.core.auth.WebIdentityAccessManager;
import com.axelixlabs.axelix.sbs.spring.core.config.DirectAccessProperties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link JwtAuthAutoConfiguration}
 *
 * @since 10.02.2026
 * @author Nikita Kirillov
 * @author Mikhail Polivakha
 */
class JwtAuthAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withPropertyValues(
                    "axelix.sbs.auth.jwt",
                    "axelix.sbs.auth.jwt.algorithm=HMAC512",
                    "axelix.sbs.auth.jwt.signing-key=secret")
            .withConfiguration(AutoConfigurations.of(
                    JwtAuthAutoConfiguration.class, SecurityContextExecutorAutoConfiguration.class));

    @Test
    void shouldCreateAllBeansInDefaultScenario() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(JwtAuthAutoConfiguration.class);
            assertThat(context).hasSingleBean(JwtDecoderService.class);
            assertThat(context).hasSingleBean(JwtEncoderService.class);
            assertThat(context).hasSingleBean(AuthorityResolver.class);
            assertThat(context).hasSingleBean(Authorizer.class);
            assertThat(context).hasSingleBean(WebIdentityAccessManager.class);
            assertThat(context).hasSingleBean(FilterRegistrationBean.class);
            assertThat(context).hasSingleBean(SecurityContextExecutor.class);
            assertThat(context).hasSingleBean(DirectAccessProperties.class);
            DirectAccessProperties directAccessProperties = context.getBean(DirectAccessProperties.class);
            assertThat(directAccessProperties.getDiagnostics().isEnabled()).isFalse();
            assertThat(directAccessProperties.getDiagnostics().getEndpoints())
                    .containsExactlyInAnyOrder(
                            "axelix-metadata", "axelix-details", "axelix-metrics", "axelix-conditions", "axelix-feign");
            assertThat(directAccessProperties.getControl().isEnabled()).isFalse();
            assertThat(directAccessProperties.getControl().getEndpoints()).containsExactly("axelix-scheduled-tasks");
        });
    }

    @Test
    void shouldBindDirectAccessModesWithoutDisablingJwtInfrastructure() {
        // given.
        ApplicationContextRunner configuredContextRunner = contextRunner.withPropertyValues(
                "axelix.sbs.auth.direct-access.diagnostics.enabled=true",
                "axelix.sbs.auth.direct-access.diagnostics.endpoints=axelix-metadata,axelix-details",
                "axelix.sbs.auth.direct-access.control.enabled=true",
                "axelix.sbs.auth.direct-access.control.endpoints=axelix-scheduled-tasks");

        // when.
        configuredContextRunner.run(context -> {
            DirectAccessProperties properties = context.getBean(DirectAccessProperties.class);

            // then.
            assertThat(properties.getDiagnostics().isEnabled()).isTrue();
            assertThat(properties.getDiagnostics().getEndpoints()).containsExactly("axelix-metadata", "axelix-details");
            assertThat(properties.getControl().isEnabled()).isTrue();
            assertThat(properties.getControl().getEndpoints()).containsExactly("axelix-scheduled-tasks");
            assertThat(context).hasSingleBean(JwtDecoderService.class);
            assertThat(context).hasSingleBean(JwtEncoderService.class);
            assertThat(context).hasSingleBean(FilterRegistrationBean.class);
        });
    }

    @Test
    void shouldFail_whenAlgorithmPropertyIsMissing() {
        new ApplicationContextRunner()
                // "axelix.sbs.auth.jwt.algorithm" is missing
                .withPropertyValues("axelix.sbs.auth.jwt", "axelix.sbs.auth.jwt.signing-key=secret")
                .withConfiguration(AutoConfigurations.of(
                        JwtAuthAutoConfiguration.class, SecurityContextExecutorAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure()).isInstanceOf(BeanCreationException.class);
                });
    }

    @Test
    void shouldFail_whenSigningKeyPropertyIsMissing() {
        new ApplicationContextRunner()
                // "axelix.sbs.auth.jwt.signing-key" is missing
                .withPropertyValues("axelix.sbs.auth.jwt", "axelix.sbs.auth.jwt.algorithm=HMAC512")
                .withConfiguration(AutoConfigurations.of(
                        JwtAuthAutoConfiguration.class, SecurityContextExecutorAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure()).isInstanceOf(BeanCreationException.class);
                });
    }

    @Test
    void shouldFail_whenAlgorithmIsNotSupported() {
        new ApplicationContextRunner()
                // "axelix.sbs.auth.jwt.algorithm=RSA512" algorithm not supported
                .withPropertyValues(
                        "axelix.sbs.auth.jwt",
                        "axelix.sbs.auth.jwt.algorithm=RSA512",
                        "axelix.sbs.auth.jwt.signing-key=secret")
                .withConfiguration(AutoConfigurations.of(
                        JwtAuthAutoConfiguration.class, SecurityContextExecutorAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure()).isInstanceOf(BeanCreationException.class);
                });
    }
}
