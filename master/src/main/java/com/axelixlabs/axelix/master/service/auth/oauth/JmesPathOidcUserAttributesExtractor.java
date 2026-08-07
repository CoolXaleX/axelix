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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.burt.jmespath.Expression;
import io.burt.jmespath.jackson.JacksonRuntime;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extracts optional user attributes from validated OIDC ID token claims.
 *
 * @author Mikhail Polivakha
 */
public class JmesPathOidcUserAttributesExtractor {

    private static final Logger log = LoggerFactory.getLogger(JmesPathOidcUserAttributesExtractor.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Nullable
    private final ConfiguredExpression jobTitleExpression;

    @Nullable
    private final ConfiguredExpression organizationalUnitExpression;

    public JmesPathOidcUserAttributesExtractor(
            @Nullable String jobTitleAttributePath, @Nullable String organizationalUnitAttributePath) {
        this.jobTitleExpression = compile(jobTitleAttributePath);
        this.organizationalUnitExpression = compile(organizationalUnitAttributePath);
    }

    public OidcUserAttributes extract(Map<String, Object> idTokenClaims) {
        JsonNode claims = OBJECT_MAPPER.valueToTree(idTokenClaims);
        return new OidcUserAttributes(
                extractText(claims, jobTitleExpression, "job title"),
                extractText(claims, organizationalUnitExpression, "organizational unit"));
    }

    @Nullable
    private ConfiguredExpression compile(@Nullable String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        return new ConfiguredExpression(path, new JacksonRuntime().compile(path));
    }

    @Nullable
    private String extractText(
            JsonNode claims, @Nullable ConfiguredExpression configuredExpression, String attributeName) {
        if (configuredExpression == null) {
            return null;
        }

        try {
            JsonNode result = configuredExpression.expression().search(claims);
            if (result != null && result.isTextual() && !result.asText().isBlank()) {
                return result.asText();
            }
        } catch (Exception e) {
            log.warn(
                    "Failed to extract OIDC {} using ID token JMESPath '{}': {}",
                    attributeName,
                    configuredExpression.path(),
                    e.getMessage());
            return null;
        }

        log.warn(
                "OIDC ID token JMESPath '{}' did not resolve to a textual value for {}",
                configuredExpression.path(),
                attributeName);
        return null;
    }

    public record OidcUserAttributes(
            @Nullable String jobTitle, @Nullable String organizationalUnit) {}

    private record ConfiguredExpression(String path, Expression<JsonNode> expression) {}
}
