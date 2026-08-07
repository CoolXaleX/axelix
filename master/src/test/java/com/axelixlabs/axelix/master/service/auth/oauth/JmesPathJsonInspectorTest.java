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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link JmesPathJsonInspector}.
 *
 * @author Mikhail Polivakha
 * @author Nikita Kirillov
 */
@ExtendWith(OutputCaptureExtension.class)
class JmesPathJsonInspectorTest {

    @Test
    void shouldExtractTextUsingVariousPaths() {
        // given.
        var subject = new JmesPathJsonInspector();
        String json = """
                {
                    "employment": {
                        "jobTitle": "Software Engineer",
                        "organizationalUnit": "Engineering"
                    }
                }""";

        // when.
        String jobTitle = subject.extract(json, "employment.jobTitle");
        String organizationalUnit = subject.extract(json, "employment.organizationalUnit");

        // then.
        assertThat(jobTitle).isEqualTo("Software Engineer");
        assertThat(organizationalUnit).isEqualTo("Engineering");
    }

    @Test
    void shouldReturnBlankExtractedString() {
        // given.
        var subject = new JmesPathJsonInspector();

        // when.
        String result = subject.extract("{\"value\": \"\"}", "value");

        // then.
        assertThat(result).isEmpty();
    }

    @Test
    void shouldWarnAndReturnNullWhenPathDoesNotResolve(CapturedOutput output) {
        // given.
        var subject = new JmesPathJsonInspector();

        // when.
        String result = subject.extract("{}", "employment.jobTitle");

        // then.
        assertThat(result).isNull();
        assertThat(output).contains("employment.jobTitle").contains("did not resolve to a textual value");
    }
}
