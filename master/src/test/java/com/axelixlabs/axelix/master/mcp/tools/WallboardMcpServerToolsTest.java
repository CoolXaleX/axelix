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

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import com.axelixlabs.axelix.master.service.state.InstanceRegistry;
import com.axelixlabs.axelix.master.utils.TestInstanceFactory;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link WallboardMcpServerTools}.
 *
 * @author Nikita Kirillov
 */
class WallboardMcpServerToolsTest {

    private final InstanceRegistry instanceRegistry = mock(InstanceRegistry.class);

    private final WallboardMcpServerTools subject = new WallboardMcpServerTools(new JsonMapper(), instanceRegistry);

    @Nested
    class GetWallboard {

        @Test
        void shouldReturnAllInstancesWhenQueryIsNull() {
            // given.
            when(instanceRegistry.getAll())
                    .thenReturn(List.of(
                            TestInstanceFactory.create("instance-1"), TestInstanceFactory.create("instance-2")));

            // when.
            String result = subject.getWallboard(null);

            // then.
            assertThatJson(result).isArray().hasSize(2);
            verify(instanceRegistry).getAll();
            verifyNoMoreInteractions(instanceRegistry);
        }

        @Test
        void shouldReturnAllInstancesWhenQueryIsBlank() {
            // given.
            when(instanceRegistry.getAll()).thenReturn(List.of(TestInstanceFactory.create("instance-1")));

            // when.
            String result = subject.getWallboard("   ");

            // then.
            assertThatJson(result).isArray().hasSize(1);
            verify(instanceRegistry).getAll();
            verifyNoMoreInteractions(instanceRegistry);
        }

        @Test
        void shouldReturnOnlyMatchingInstancesWhenQueryIsProvided() {
            // given.
            when(instanceRegistry.findByQuery("invoice"))
                    .thenReturn(Set.of(TestInstanceFactory.withName("instance-1", "invoice-internal-process")));

            // when.
            String result = subject.getWallboard("invoice");

            // then.
            assertThatJson(result).isArray().hasSize(1);
            assertThatJson(result).node("[0].name").isEqualTo("invoice-internal-process");
            verify(instanceRegistry).findByQuery("invoice");
            verifyNoMoreInteractions(instanceRegistry);
        }
    }
}
