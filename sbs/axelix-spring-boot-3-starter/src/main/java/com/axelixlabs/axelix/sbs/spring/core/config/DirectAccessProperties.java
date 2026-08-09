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
package com.axelixlabs.axelix.sbs.spring.core.config;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Configuration for accessing Axelix Actuator endpoints without JWT authorization.
 */
public class DirectAccessProperties {

    public static final String CONFIG_PROPS_PREFIX = "axelix.sbs.auth.direct-access";

    private AccessMode diagnostics = new AccessMode(
            Set.of("axelix-metadata", "axelix-details", "axelix-metrics", "axelix-conditions", "axelix-feign"));

    private AccessMode control = new AccessMode(Set.of("axelix-scheduled-tasks"));

    public AccessMode getDiagnostics() {
        return diagnostics;
    }

    public void setDiagnostics(AccessMode diagnostics) {
        this.diagnostics = diagnostics;
    }

    public AccessMode getControl() {
        return control;
    }

    public void setControl(AccessMode control) {
        this.control = control;
    }

    public static class AccessMode {

        private boolean enabled;

        private Set<String> endpoints;

        public AccessMode() {
            this(Set.of());
        }

        private AccessMode(Set<String> endpoints) {
            this.endpoints = new LinkedHashSet<>(endpoints);
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Set<String> getEndpoints() {
            return endpoints;
        }

        public void setEndpoints(Set<String> endpoints) {
            this.endpoints = endpoints;
        }
    }
}
