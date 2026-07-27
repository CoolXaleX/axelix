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
package com.axelixlabs.axelix.master.mcp.tools;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.axelixlabs.axelix.common.domain.ActuatorEndpoints;
import com.axelixlabs.axelix.common.domain.http.HttpPayload;
import com.axelixlabs.axelix.master.domain.InstanceId;
import com.axelixlabs.axelix.master.service.transport.EndpointInvoker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link CacheMcpServerTools}.
 *
 * @author Nikita Kirillov
 */
class CacheMcpServerToolsTest {

    private static final String INSTANCE_ID = "instance-1";

    private final EndpointInvoker endpointInvoker = mock(EndpointInvoker.class);

    private final CacheMcpServerTools subject = new CacheMcpServerTools(endpointInvoker);

    @Test
    void shouldSendTheCacheManagerAndCacheNameAsThePathVariables() {
        // when.
        subject.clearSpecificCacheEntity(INSTANCE_ID, "cacheManager", "owners");

        // then.
        ArgumentCaptor<HttpPayload> payloadCaptor = ArgumentCaptor.forClass(HttpPayload.class);
        verify(endpointInvoker)
                .invokeNoValue(
                        eq(InstanceId.of(INSTANCE_ID)),
                        eq(ActuatorEndpoints.CLEAR_SINGLE_CACHE),
                        payloadCaptor.capture());

        assertThat(payloadCaptor.getValue().pathVariableValues())
                .containsOnly(Map.entry("cacheManagerName", "cacheManager"), Map.entry("cacheName", "owners"));
    }
}
