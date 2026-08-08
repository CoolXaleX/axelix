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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

import com.axelixlabs.axelix.common.auth.core.Authority;
import com.axelixlabs.axelix.common.auth.core.DefaultAuthority;
import com.axelixlabs.axelix.master.utils.InvalidAuthScenario;

/**
 * A specific extension for Junit 6 to provide {@link TestTemplateInvocationContext} instances for the given
 * {@link ProtectedEndpointTests} methods.
 *
 * @author Mikhail Polivakha
 */
public class ProtectedEndpointExtension implements TestTemplateInvocationContextProvider {

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return context.getTestMethod()
                .map(method -> method.isAnnotationPresent(ProtectedEndpointTests.class))
                .orElse(false);
    }

    @Override
    public Stream<? extends TestTemplateInvocationContext> provideTestTemplateInvocationContexts(
            ExtensionContext context) {
        Method requiredTestMethod = context.getRequiredTestMethod();

        var annotation = requiredTestMethod.getAnnotation(ProtectedEndpointTests.class);

        List<TestTemplateInvocationContext> base = new ArrayList<>();
        base.add(getBadTokenInvocationContext(annotation));

        TestTemplateInvocationContext badAuthorityContext = addBadAuthorityInvocationContext(annotation);

        if (badAuthorityContext != null) {
            base.add(badAuthorityContext);
        }

        return base.stream();
    }

    private @NonNull TestTemplateInvocationContext getBadTokenInvocationContext(ProtectedEndpointTests annotation) {
        return new TestTemplateInvocationContext() {

            @Override
            public List<Extension> getAdditionalExtensions() {
                return Arrays.stream(InvalidAuthScenario.values())
                        .map(it -> (Extension) new BadTokenTestExecutionCallback(
                                it, annotation.path(), annotation.method().name()))
                        .toList();
            }
        };
    }

    private @Nullable TestTemplateInvocationContext addBadAuthorityInvocationContext(
            ProtectedEndpointTests annotation) {
        DefaultAuthority[] authorities = annotation.requiredAuthority();

        if (authorities != null && authorities.length > 0) {
            List<Authority> authoritiesToBeDenied = EnumSet.complementOf(EnumSet.of(authorities[0])).stream()
                    .map(it -> (Authority) it)
                    .toList();

            return new TestTemplateInvocationContext() {

                @Override
                public List<Extension> getAdditionalExtensions() {
                    return authoritiesToBeDenied.stream()
                            .map(authority -> (Extension) new BadAuthorityTestExecutionCallback(
                                    authority,
                                    annotation.path(),
                                    annotation.method().name()))
                            .toList();
                }
            };
        } else {
            return null;
        }
    }
}
