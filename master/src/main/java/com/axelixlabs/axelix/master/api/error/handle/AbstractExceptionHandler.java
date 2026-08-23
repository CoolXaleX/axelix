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
package com.axelixlabs.axelix.master.api.error.handle;

import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.data.util.ProxyUtils;

import com.axelixlabs.axelix.common.auth.exception.AuthorizationException;
import com.axelixlabs.axelix.master.filter.auth.requestcontext.ExternalWebRequestContext;
import com.axelixlabs.axelix.master.filter.auth.requestcontext.MasterRequestContextInitFilter;
import com.axelixlabs.axelix.master.filter.auth.requestcontext.McpRequestContext;
import com.axelixlabs.axelix.master.service.auth.intercept.web.OnAccessDenied;
import com.axelixlabs.axelix.master.service.auth.intercept.web.OnAuthenticationFailure;
import com.axelixlabs.axelix.master.service.auth.intercept.web.OnWebIamEventInterceptor;

/**
 * Abstract implementation of {@link ExceptionHandler}.
 *
 * @author Mikhail Polivakha
 */
public abstract class AbstractExceptionHandler<T extends Exception> implements ExceptionHandler<T> {

    private final List<OnAccessDenied> onAccessDeniedInterceptors;
    private final List<OnAuthenticationFailure> onAuthenticationFailureInterceptors;

    protected AbstractExceptionHandler(List<OnWebIamEventInterceptor> interceptors) {
        this.onAccessDeniedInterceptors = getInterceptorsOfType(interceptors, OnAccessDenied.class);
        this.onAuthenticationFailureInterceptors = getInterceptorsOfType(interceptors, OnAuthenticationFailure.class);
    }

    protected void fireOnAccessDenied(HttpServletRequest request, AuthorizationException exception) {
        Optional<ExternalWebRequestContext> webRequestContext = MasterRequestContextInitFilter.getWebRequestContext();

        webRequestContext.ifPresent(context -> {
            onAccessDeniedInterceptors.forEach(interceptor -> {
                interceptor.onAccessDenied(context.masterWebEndpoint(), request, exception.getUser());
            });
        });

        Optional<McpRequestContext> mcpRequestContext = MasterRequestContextInitFilter.getMcpRequestContext();

        mcpRequestContext.ifPresent(context -> {
            // TODO:
        });
    }

    protected void fireOnAuthenticationFailure(HttpServletRequest request) {
        Optional<ExternalWebRequestContext> webRequestContext = MasterRequestContextInitFilter.getWebRequestContext();

        webRequestContext.ifPresent(context -> {
            onAuthenticationFailureInterceptors.forEach(interceptor -> {
                interceptor.onAuthenticationFailure(context.masterWebEndpoint(), request);
            });
        });

        Optional<McpRequestContext> mcpRequestContext = MasterRequestContextInitFilter.getMcpRequestContext();

        mcpRequestContext.ifPresent(context -> {
            // TODO:
        });
    }

    private static <T> List<T> getInterceptorsOfType(
            List<OnWebIamEventInterceptor> interceptors, Class<T> interceptorType) {
        return interceptors.stream()
                .filter(it -> interceptorType.isAssignableFrom(ProxyUtils.getUserClass(it.getClass())))
                .map(interceptorType::cast)
                .toList();
    }
}
