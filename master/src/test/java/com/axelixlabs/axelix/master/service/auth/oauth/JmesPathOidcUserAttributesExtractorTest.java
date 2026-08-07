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
package com.axelixlabs.axelix.master.service.auth.oauth;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import com.axelixlabs.axelix.master.service.auth.oauth.JmesPathOidcUserAttributesExtractor.OidcUserAttributes;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link JmesPathOidcUserAttributesExtractor}.
 *
 * @author Mikhail Polivakha
 */
@ExtendWith(OutputCaptureExtension.class)
class JmesPathOidcUserAttributesExtractorTest {

    @Test
    void shouldExtractConfiguredAttributes() {
        // given.
        var subject = new JmesPathOidcUserAttributesExtractor("employment.jobTitle", "employment.organizationalUnit");
        Map<String, Object> claims =
                Map.of("employment", Map.of("jobTitle", "Software Engineer", "organizationalUnit", "Engineering"));

        // when.
        OidcUserAttributes attributes = subject.extract(claims);

        // then.
        assertThat(attributes.jobTitle()).isEqualTo("Software Engineer");
        assertThat(attributes.organizationalUnit()).isEqualTo("Engineering");
    }

    @Test
    void shouldSkipAttributesWhenPathsAreNotConfigured(CapturedOutput output) {
        // given.
        var subject = new JmesPathOidcUserAttributesExtractor(null, null);

        // when.
        OidcUserAttributes attributes = subject.extract(Map.of());

        // then.
        assertThat(attributes.jobTitle()).isNull();
        assertThat(attributes.organizationalUnit()).isNull();
        assertThat(output).doesNotContain("did not resolve");
    }

    @Test
    void shouldWarnAndReturnNullWhenConfiguredPathDoesNotResolve(CapturedOutput output) {
        // given.
        var subject = new JmesPathOidcUserAttributesExtractor("employment.jobTitle", null);

        // when.
        OidcUserAttributes attributes = subject.extract(Map.of());

        // then.
        assertThat(attributes.jobTitle()).isNull();
        assertThat(attributes.organizationalUnit()).isNull();
        assertThat(output).contains("employment.jobTitle").contains("did not resolve to a textual value for job title");
    }
}
