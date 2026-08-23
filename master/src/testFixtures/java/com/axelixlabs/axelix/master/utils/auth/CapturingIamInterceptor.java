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
package com.axelixlabs.axelix.master.utils.auth;

import jakarta.servlet.http.HttpServletRequest;

import org.jspecify.annotations.Nullable;

import com.axelixlabs.axelix.common.auth.core.User;
import com.axelixlabs.axelix.master.service.auth.MasterWebEndpoint;
import com.axelixlabs.axelix.master.service.auth.intercept.web.OnAccessDenied;
import com.axelixlabs.axelix.master.service.auth.intercept.web.OnInvalidTokenInRequest;
import com.axelixlabs.axelix.master.service.auth.intercept.web.OnSuccessfulResult;

/**
 * Test IAM interceptor that records the {@link MasterWebEndpoint} each auth callback is invoked with. It lets
 * {@link AbstractProtectedEndpointTest} assert that {@code WebRequestContextInitFilter} resolved the request to
 * the correct endpoint and propagated it down the auth filter chain.
 *
 * @author Mikhail Polivakha
 */
public class CapturingIamInterceptor implements OnInvalidTokenInRequest, OnAccessDenied, OnSuccessfulResult {

    private @Nullable MasterWebEndpoint invalidTokenEndpoint;
    private @Nullable MasterWebEndpoint accessDeniedEndpoint;
    private @Nullable MasterWebEndpoint successfulEndpoint;

    @Override
    public void onInvalidToken(MasterWebEndpoint target, HttpServletRequest request) {
        this.invalidTokenEndpoint = target;
    }

    @Override
    public void onAccessDenied(MasterWebEndpoint target, HttpServletRequest request, User user) {
        this.accessDeniedEndpoint = target;
    }

    @Override
    public void onSuccess(MasterWebEndpoint target, HttpServletRequest request, User user) {
        this.successfulEndpoint = target;
    }

    /**
     * Forget everything captured so far. Meant to be called before each test so assertions never observe a
     * callback triggered by a previous test sharing the same (cached) application context.
     */
    public void reset() {
        this.invalidTokenEndpoint = null;
        this.accessDeniedEndpoint = null;
        this.successfulEndpoint = null;
    }

    public @Nullable MasterWebEndpoint invalidTokenEndpoint() {
        return invalidTokenEndpoint;
    }

    public @Nullable MasterWebEndpoint accessDeniedEndpoint() {
        return accessDeniedEndpoint;
    }

    public @Nullable MasterWebEndpoint successfulEndpoint() {
        return successfulEndpoint;
    }
}
