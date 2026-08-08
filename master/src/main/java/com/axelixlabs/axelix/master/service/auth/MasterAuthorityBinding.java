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

import org.springframework.web.util.pattern.PathPattern;

import com.axelixlabs.axelix.common.auth.core.Authority;
import com.axelixlabs.axelix.common.domain.http.HttpMethod;

/**
 * A special handle that binds the {@link Authority} with the specific API on the master side, identified by
 * {@link HttpMethod} and {@link PathPattern}.
 *
 * @author Mikhail Polivakha
 */
public record MasterAuthorityBinding(HttpMethod method, PathPattern pathPattern, Authority authority) {}
