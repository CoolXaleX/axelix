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
package com.axelixlabs.axelix.master.autoconfiguration.auth;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import com.axelixlabs.axelix.common.auth.core.DefaultAuthority;
import com.axelixlabs.axelix.common.domain.http.HttpMethod;
import com.axelixlabs.axelix.master.api.external.ApiPaths;
import com.axelixlabs.axelix.master.service.auth.MasterAuthorityBinding;

/**
 * Auto-configuration that declares the {@link MasterAuthorityBinding} beans for the master endpoints.
 *
 * @author Mikhail Polivakha
 */
@AutoConfiguration
public class AuthorityBindingsAutoConfiguration {

    // Users -> USERS_MANAGEMENT
    @Bean
    public MasterAuthorityBinding usersCreateAuthorityBinding() {
        return MasterAuthorityBinding.of(
                HttpMethod.POST, ApiPaths.UsersManagementApi.USERS_CREATE, DefaultAuthority.USERS_MANAGEMENT);
    }

    @Bean
    public MasterAuthorityBinding usersDeleteAuthorityBinding() {
        return MasterAuthorityBinding.of(
                HttpMethod.DELETE, ApiPaths.UsersManagementApi.USERS_DELETE, DefaultAuthority.USERS_MANAGEMENT);
    }

    @Bean
    public MasterAuthorityBinding usersStatusAuthorityBinding() {
        return MasterAuthorityBinding.of(
                HttpMethod.PUT, ApiPaths.UsersManagementApi.USERS_STATUS, DefaultAuthority.USERS_MANAGEMENT);
    }

    @Bean
    public MasterAuthorityBinding usersUpdateAuthorityBinding() {
        return MasterAuthorityBinding.of(
                HttpMethod.PUT, ApiPaths.UsersManagementApi.USERS_UPDATE, DefaultAuthority.USERS_MANAGEMENT);
    }

    // Users -> USERS_VIEW

    @Bean
    public MasterAuthorityBinding usersFeedAuthorityBinding() {
        return MasterAuthorityBinding.of(HttpMethod.GET, ApiPaths.UsersApi.USERS_FEED, DefaultAuthority.USERS_VIEW);
    }

    // Caches -> CACHES_TOGGLE

    @Bean
    public MasterAuthorityBinding disableCacheAuthorityBinding() {
        return MasterAuthorityBinding.of(
                HttpMethod.POST, ApiPaths.CachesApi.DISABLE_CACHE, DefaultAuthority.CACHES_TOGGLE);
    }

    @Bean
    public MasterAuthorityBinding enableCacheAuthorityBinding() {
        return MasterAuthorityBinding.of(
                HttpMethod.POST, ApiPaths.CachesApi.ENABLE_CACHE, DefaultAuthority.CACHES_TOGGLE);
    }

    @Bean
    public MasterAuthorityBinding disableCacheManagerAuthorityBinding() {
        return MasterAuthorityBinding.of(
                HttpMethod.POST, ApiPaths.CachesApi.DISABLE_CACHE_MANAGER, DefaultAuthority.CACHES_TOGGLE);
    }

    @Bean
    public MasterAuthorityBinding enableCacheManagerAuthorityBinding() {
        return MasterAuthorityBinding.of(
                HttpMethod.POST, ApiPaths.CachesApi.ENABLE_CACHE_MANAGER, DefaultAuthority.CACHES_TOGGLE);
    }

    // Caches -> CACHES_CLEAR

    @Bean
    public MasterAuthorityBinding clearCacheAuthorityBinding() {
        return MasterAuthorityBinding.of(
                HttpMethod.DELETE, ApiPaths.CachesApi.CACHE_NAME, DefaultAuthority.CACHES_CLEAR);
    }

    @Bean
    public MasterAuthorityBinding clearInstanceCachesAuthorityBinding() {
        return MasterAuthorityBinding.of(
                HttpMethod.DELETE, ApiPaths.CachesApi.INSTANCE_ID, DefaultAuthority.CACHES_CLEAR);
    }

    // Garbage Collector -> GARBAGE_COLLECTOR

    @Bean
    public MasterAuthorityBinding disableGcLoggingAuthorityBinding() {
        return MasterAuthorityBinding.of(
                HttpMethod.POST, ApiPaths.GcLogFileApi.DISABLE_GC_LOGGING, DefaultAuthority.GARBAGE_COLLECTOR);
    }

    @Bean
    public MasterAuthorityBinding enableGcLoggingAuthorityBinding() {
        return MasterAuthorityBinding.of(
                HttpMethod.POST, ApiPaths.GcLogFileApi.ENABLE_GC_LOGGING, DefaultAuthority.GARBAGE_COLLECTOR);
    }

    @Bean
    public MasterAuthorityBinding triggerGcAuthorityBinding() {
        return MasterAuthorityBinding.of(
                HttpMethod.POST, ApiPaths.GcLogFileApi.TRIGGER_GC, DefaultAuthority.GARBAGE_COLLECTOR);
    }

    // Scheduled Tasks -> SCHEDULED_TASKS_MODIFY

    @Bean
    public MasterAuthorityBinding disableScheduledTaskAuthorityBinding() {
        return MasterAuthorityBinding.of(
                HttpMethod.POST, ApiPaths.ScheduledTasksApi.DISABLE_TASK, DefaultAuthority.SCHEDULED_TASKS_MODIFY);
    }

    @Bean
    public MasterAuthorityBinding enableScheduledTaskAuthorityBinding() {
        return MasterAuthorityBinding.of(
                HttpMethod.POST, ApiPaths.ScheduledTasksApi.ENABLE_TASK, DefaultAuthority.SCHEDULED_TASKS_MODIFY);
    }

    @Bean
    public MasterAuthorityBinding executeScheduledTaskAuthorityBinding() {
        return MasterAuthorityBinding.of(
                HttpMethod.POST, ApiPaths.ScheduledTasksApi.EXECUTE, DefaultAuthority.SCHEDULED_TASKS_MODIFY);
    }

    @Bean
    public MasterAuthorityBinding modifyScheduledTaskCronExpressionAuthorityBinding() {
        return MasterAuthorityBinding.of(
                HttpMethod.POST,
                ApiPaths.ScheduledTasksApi.MODIFY_CRON_EXPRESSION,
                DefaultAuthority.SCHEDULED_TASKS_MODIFY);
    }

    @Bean
    public MasterAuthorityBinding modifyScheduledTaskIntervalAuthorityBinding() {
        return MasterAuthorityBinding.of(
                HttpMethod.POST, ApiPaths.ScheduledTasksApi.MODIFY_INTERVAL, DefaultAuthority.SCHEDULED_TASKS_MODIFY);
    }
}
