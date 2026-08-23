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
package com.axelixlabs.axelix.master.filter.auth.requestcontext;

import java.io.IOException;
import java.util.Optional;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jspecify.annotations.Nullable;

import org.springframework.ai.mcp.server.common.autoconfigure.properties.McpServerStreamableHttpProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import com.axelixlabs.axelix.common.domain.http.HttpMethod;
import com.axelixlabs.axelix.common.utils.Assert;
import com.axelixlabs.axelix.master.autoconfiguration.web.WebAutoConfiguration;
import com.axelixlabs.axelix.master.filter.FiltersOrder;
import com.axelixlabs.axelix.master.service.auth.MasterWebEndpoint;
import com.axelixlabs.axelix.master.service.auth.MasterWebEndpointResolver;

/**
 * {@link OncePerRequestFilter} whose job is to record the profile of the request that was attempted to
 * be executed.
 *
 * @author Mikhail Polivakha
 */
@Order(FiltersOrder.REQUEST_PROFILE_FILTER)
public class MasterRequestContextInitFilter extends OncePerRequestFilter {

    private static final ScopedValue<MasterRequestContext> WEBS_REQUEST_CONTEXT = ScopedValue.newInstance();

    private final MasterWebEndpointResolver masterWebEndpointResolver;
    private final @Nullable McpServerStreamableHttpProperties mcpProperties;

    public MasterRequestContextInitFilter(
            MasterWebEndpointResolver masterWebEndpointResolver,
            ObjectProvider<McpServerStreamableHttpProperties> mcpProperties) {
        this.mcpProperties = mcpProperties.getIfAvailable();
        this.masterWebEndpointResolver = masterWebEndpointResolver;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !shouldFilter(request);
    }

    private boolean shouldFilter(HttpServletRequest request) {
        String servletPath = request.getServletPath();

        return servletPath.startsWith(WebAutoConfiguration.EXTERNAL_API_PATH)
                || (mcpProperties != null && servletPath.startsWith(mcpProperties.getMcpEndpoint()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String relativePath = request.getServletPath().substring(WebAutoConfiguration.EXTERNAL_API_PATH.length());

        Optional<MasterWebEndpoint> masterWebEndpoint =
                masterWebEndpointResolver.resolveEndpoint(relativePath, HttpMethod.valueOf(request.getMethod()));

        if (masterWebEndpoint.isPresent()) {
            MasterWebEndpoint endpoint = masterWebEndpoint.get();

            try {
                ScopedValue.where(WEBS_REQUEST_CONTEXT, new WebRequestContext(endpoint))
                        .call(() -> {
                            filterChain.doFilter(request, response);
                            return null;
                        });
            } catch (IOException | ServletException e) {
                throw e;
            } catch (Exception e) {
                throw new ServletException(e);
            }
        } else {
            throw new ServletException("Unrecognized endpoint got invoked. Failing request fast");
        }
    }

    public static WebRequestContext requireWebRequestContext() {
        return requireContextOfType(WebRequestContext.class);
    }

    public static McpRequestContext requireMcpRequestContext() {
        return requireContextOfType(McpRequestContext.class);
    }

    private static <T> T requireContextOfType(Class<T> contextType) {
        MasterRequestContext masterRequestContext = WEBS_REQUEST_CONTEXT.get();

        Assert.state(() -> masterRequestContext != null, "RequestContext cannot be null at this point");
        Assert.state(
                () -> contextType.isInstance(masterRequestContext),
                "Expected RequestContext to be of type " + contextType.getSimpleName());

        return contextType.cast(masterRequestContext);
    }
}
