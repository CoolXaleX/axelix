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

import java.util.Set;

import com.axelixlabs.axelix.common.auth.core.DefaultRole;
import com.axelixlabs.axelix.common.auth.core.Role;
import com.axelixlabs.axelix.master.autoconfiguration.web.WebAutoConfiguration;
import com.axelixlabs.axelix.master.service.auth.MasterWebEndpoint;
import com.axelixlabs.axelix.master.utils.InvalidAuthScenario;
import com.axelixlabs.axelix.master.utils.TestRestTemplateBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Shared base for the cross-cutting authentication/authorization contract that every protected Axelix Master
 * external API endpoint must honour.
 *
 * @author Mikhail Polivakha
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(CapturingIamInterceptor.class)
public abstract class AbstractProtectedEndpointTest {

    @Autowired
    private TestRestTemplateBuilder restTemplate;

    @Autowired
    private CapturingIamInterceptor capturingIamInterceptor;

    /**
     * The endpoint whose cross-cutting auth behaviour is under test. Subclasses construct it inline.
     */
    protected abstract TestableMasterWebEndpoint endpointUnderTest();

    @BeforeEach
    void resetCapturingInterceptor() {
        capturingIamInterceptor.reset();
    }

    @ParameterizedTest
    @EnumSource(InvalidAuthScenario.class)
    void shouldRejectRequestWithInvalidToken(InvalidAuthScenario scenario) {
        ResponseEntity<Void> response = scenario.getModifier()
                .apply(restTemplate)
                .exchange(endpointUnderTest().url(), httpMethod(), null, Void.class);

        assertThat(capturingIamInterceptor.invalidTokenEndpoint())
            .isNotNull()
            .extracting(MasterWebEndpoint::operationCode)
            .isEqualTo(endpointUnderTest().target().operationCode());

        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldRejectRequestWithInsufficientAuthority() {
        assumeTrue(endpointUnderTest().target().authority() != null, "Endpoint requires no authority; nothing to deny");

        ResponseEntity<Void> response = restTemplate
                .withRole(roleWithoutRequiredAuthority())
                .exchange(endpointUnderTest().url(), httpMethod(), null, Void.class);

        assertThat(capturingIamInterceptor.accessDeniedEndpoint())
            .isNotNull()
            .extracting(MasterWebEndpoint::operationCode)
            .isEqualTo(endpointUnderTest().target().operationCode());

        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    /**
     * A syntactically valid, authenticated role that simply lacks every authority — so any endpoint that requires
     * one denies access with {@code 403}.
     */
    private Role roleWithoutRequiredAuthority() {
        return new DefaultRole("PROTECTED_ENDPOINT_TEST_ROLE", Set.of());
    }

    private HttpMethod httpMethod() {
        return HttpMethod.valueOf(endpointUnderTest().target().method().name());
    }

    /**
     * @param target the target {@link MasterWebEndpoint} to call.
     * @param url the real URL to be used ({@link MasterWebEndpoint} has only the template of the URL).
     */
    public record TestableMasterWebEndpoint(MasterWebEndpoint target, String url) {}
}
