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
package com.axelixlabs.axelix.sbs.spring.core.master;

import java.util.Properties;

import static com.axelixlabs.axelix.sbs.spring.core.utils.StringUtils.emptyIfNull;

/**
 * The build/git properties generated into {@code META-INF/axelix-info.properties} by the axelix
 * Maven/Gradle plugins.
 *
 * @author Nikita Kirillov
 */
public final class BuildGitInfoProperties {

    private static final String BUILD_GROUP = "build.group";
    private static final String BUILD_NAME = "build.name";
    private static final String BUILD_TIME = "build.time";
    private static final String BUILD_VERSION = "build.version";
    private static final String GIT_BRANCH = "git.branch";
    private static final String GIT_COMMIT_ID_ABBREV = "git.commit.id.abbrev";
    private static final String GIT_COMMIT_TIME = "git.commit.time";
    private static final String GIT_COMMIT_USER_NAME = "git.commit.user.name";
    private static final String GIT_COMMIT_USER_EMAIL = "git.commit.user.email";

    private final Properties properties;

    public BuildGitInfoProperties(Properties properties) {
        this.properties = properties;
    }

    public String getGroupId() {
        return emptyIfNull(properties.getProperty(BUILD_GROUP));
    }

    public String getArtifactId() {
        return emptyIfNull(properties.getProperty(BUILD_NAME));
    }

    public String getBuildTimestamp() {
        return emptyIfNull(properties.getProperty(BUILD_TIME));
    }

    public String getServiceVersion() {
        return emptyIfNull(properties.getProperty(BUILD_VERSION));
    }

    public String getBranch() {
        return emptyIfNull(properties.getProperty(GIT_BRANCH));
    }

    public String getCommitShaShort() {
        return emptyIfNull(properties.getProperty(GIT_COMMIT_ID_ABBREV));
    }

    public String getCommitTimestamp() {
        return emptyIfNull(properties.getProperty(GIT_COMMIT_TIME));
    }

    public String getCommitUserName() {
        return emptyIfNull(properties.getProperty(GIT_COMMIT_USER_NAME));
    }

    public String getCommitUserEmail() {
        return emptyIfNull(properties.getProperty(GIT_COMMIT_USER_EMAIL));
    }
}
