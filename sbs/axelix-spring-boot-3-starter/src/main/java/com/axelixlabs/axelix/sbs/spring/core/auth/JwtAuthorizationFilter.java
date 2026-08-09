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
package com.axelixlabs.axelix.sbs.spring.core.auth;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

import com.axelixlabs.axelix.common.auth.core.AuthenticationSchemes;
import com.axelixlabs.axelix.common.auth.core.Authority;
import com.axelixlabs.axelix.common.auth.core.DefaultSecurityContext;
import com.axelixlabs.axelix.common.auth.core.SecurityContextExecutor;
import com.axelixlabs.axelix.common.auth.core.User;
import com.axelixlabs.axelix.common.auth.exception.AuthorizationException;
import com.axelixlabs.axelix.common.auth.exception.ExpiredJwtTokenException;
import com.axelixlabs.axelix.common.auth.exception.InvalidJwtTokenException;
import com.axelixlabs.axelix.common.auth.exception.JwtParsingException;
import com.axelixlabs.axelix.common.domain.http.HttpMethod;
import com.axelixlabs.axelix.sbs.spring.core.config.DirectAccessProperties;

/**
 * A custom servlet filter that restricts access to Actuator endpoints based on JWT token presence, validity,
 * and mapped {@link Authority} authorities.
 * <p>
 * Rejects unauthorized requests before they reach the application logic.
 *
 * @author Nikita Kirillov
 * @author Mikhail Polivakha
 * @since 29.07.2025
 */
@SuppressWarnings("NullAway") // TODO: Pending issue GH-42 – introduce exception translator and refactor this filter
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final WebIdentityAccessManager webIdentityAccessManager;
    private final SecurityContextExecutor securityContextExecutor;
    private final String baseActuatorPath;
    private final DirectAccessProperties directAccessProperties;

    public JwtAuthorizationFilter(
            WebIdentityAccessManager webIdentityAccessManager,
            SecurityContextExecutor securityContextExecutor,
            String baseActuatorPath,
            DirectAccessProperties directAccessProperties) {

        this.webIdentityAccessManager = webIdentityAccessManager;
        this.securityContextExecutor = securityContextExecutor;
        this.baseActuatorPath = baseActuatorPath;
        this.directAccessProperties = directAccessProperties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        if (!servletPath.startsWith(baseActuatorPath + "/axelix-")) {
            return true;
        }

        String endpointId = resolveEndpointId(servletPath);
        if (isReadOperation(request.getMethod())) {
            DirectAccessProperties.AccessMode diagnostics = directAccessProperties.getDiagnostics();
            return diagnostics.isEnabled() && diagnostics.getEndpoints().contains(endpointId);
        }

        if (isControlOperation(request.getMethod())) {
            DirectAccessProperties.AccessMode control = directAccessProperties.getControl();
            return control.isEnabled() && control.getEndpoints().contains(endpointId);
        }
        return false;
    }

    private String resolveEndpointId(String servletPath) {
        String endpointPath = servletPath.substring(baseActuatorPath.length() + 1);
        int nestedPathIndex = endpointPath.indexOf('/');
        return nestedPathIndex < 0 ? endpointPath : endpointPath.substring(0, nestedPathIndex);
    }

    private static boolean isReadOperation(String method) {
        return "GET".equals(method) || "HEAD".equals(method);
    }

    private static boolean isControlOperation(String method) {
        return "POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method) || "DELETE".equals(method);
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = resolveToken(request);

            HttpMethod requestHttpMethod = HttpMethod.valueOf(request.getMethod());

            String relativePath = request.getServletPath().substring(baseActuatorPath.length());

            User user = webIdentityAccessManager.verifyAccess(relativePath, requestHttpMethod, token);

            securityContextExecutor.runWithinSecurityContext(
                    () -> filterChain.doFilter(request, response), new DefaultSecurityContext(user, token));

        } catch (JwtParsingException | ExpiredJwtTokenException | InvalidJwtTokenException e) {
            respondWith(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        } catch (AuthorizationException e) {
            respondWith(response, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Nullable
    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String bearerSchemeCode = AuthenticationSchemes.BEARER.prefix();
        if (header != null) {
            if (header.startsWith(bearerSchemeCode)) {
                return header.substring(bearerSchemeCode.length());
            }
        }
        return null;
    }

    private void respondWith(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.getWriter().write(message);
        response.getWriter().flush();
    }
}
