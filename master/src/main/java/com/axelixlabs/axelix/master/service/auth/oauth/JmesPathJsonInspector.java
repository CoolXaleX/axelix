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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.burt.jmespath.Expression;
import io.burt.jmespath.jackson.JacksonRuntime;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Evaluates JMESPath expressions against JSON values.
 *
 * @author Mikhail Polivakha
 * @author Nikita Kirillov
 */
public class JmesPathJsonInspector {

    private static final Logger log = LoggerFactory.getLogger(JmesPathJsonInspector.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final JacksonRuntime JMES_PATH_RUNTIME = new JacksonRuntime();

    @Nullable
    public String extract(String json, String jmesPath) {
        try {
            JsonNode root = OBJECT_MAPPER.readTree(json);
            Expression<JsonNode> expression = JMES_PATH_RUNTIME.compile(jmesPath);
            JsonNode result = expression.search(root);
            if (result != null && result.isTextual()) {
                return result.asText();
            }

            log.warn("JMESPath '{}' did not resolve to a textual value", jmesPath);
        } catch (Exception e) {
            log.warn("Failed to evaluate JSON using JMESPath '{}': {}", jmesPath, e.getMessage());
        }
        return null;
    }
}
