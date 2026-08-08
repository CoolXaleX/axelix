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
package com.axelixlabs.axelix.master.service.auth;

import java.util.List;
import java.util.Optional;

import org.springframework.http.server.PathContainer;

import com.axelixlabs.axelix.common.auth.core.Authority;
import com.axelixlabs.axelix.common.auth.service.AuthorityResolver;
import com.axelixlabs.axelix.common.domain.http.HttpMethod;

/**
 * Implementation of {@link AuthorityResolver} that is supposed to resolve the {@link Authority}
 * for Axelix Master endpoints.
 *
 * @author Mikhail Polivakha
 * @author Sergey Cherkasov
 */
public class MasterAuthorityResolver implements AuthorityResolver {

    private final List<MasterAuthorityBinding> bindings;

    public MasterAuthorityResolver(List<MasterAuthorityBinding> bindings) {
        this.bindings = bindings;
    }

    @Override
    public Optional<Authority> resolve(String relativeRequestPath, HttpMethod httpMethod) {

        PathContainer pathContainer = PathContainer.parsePath(relativeRequestPath);

        for (MasterAuthorityBinding binding : bindings) {
            // TODO: I am not sure that this one will perform good.
            //  We may try to introduce some sort of the cache here. Does it even make sense?
            if (binding.method() == httpMethod && binding.pathPattern().matches(pathContainer)) {
                return Optional.of(binding.authority());
            }
        }

        return Optional.empty();
    }
}
