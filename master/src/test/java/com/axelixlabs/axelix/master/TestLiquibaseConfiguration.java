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
package com.axelixlabs.axelix.master;

import org.springframework.boot.liquibase.autoconfigure.LiquibaseProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.axelixlabs.axelix.master.autoconfiguration.database.ConditionalOnCommunityRdbms;
import com.axelixlabs.axelix.master.domain.database.CommunityRDBMS;

/**
 * Test-only counterpart of the {@code LiquibaseProperties} beans that {@code PersistenceAutoConfiguration}
 * used to provide in production. In the unified distribution, the real changelog root files (and the beans
 * pointing at them) live in {@code master-enterprise} - {@code :master} on its own never runs in production,
 * only its own test suite boots a standalone context and needs a schema to run against. This class is only on
 * the test classpath (registered via {@code src/test/resources/META-INF/spring/...AutoConfiguration.imports}),
 * so it never leaks into {@code master-enterprise}, where it would otherwise clash with the enterprise
 * equivalent.
 *
 * @author Nikita Kirillov
 */
@Configuration
public class TestLiquibaseConfiguration {

    @Configuration
    @ConditionalOnCommunityRdbms(CommunityRDBMS.SQLITE)
    public static class SQLiteTestLiquibaseAutoConfiguration {

        @Bean
        @Primary
        public LiquibaseProperties sqliteLiquibaseProperties() {
            LiquibaseProperties liquibaseProperties = new LiquibaseProperties();
            liquibaseProperties.setChangeLog("db/changelog/sqlite/db.changelog.master.sqlite.xml");
            return liquibaseProperties;
        }
    }

    @Configuration
    @ConditionalOnCommunityRdbms(CommunityRDBMS.POSTGRES)
    public static class PostgresTestLiquibaseAutoConfiguration {

        @Bean
        @Primary
        public LiquibaseProperties postgresLiquibaseProperties() {
            LiquibaseProperties liquibaseProperties = new LiquibaseProperties();
            liquibaseProperties.setChangeLog("db/changelog/postgres/db.changelog.master.postgresql.xml");
            return liquibaseProperties;
        }
    }

    @Configuration
    @ConditionalOnCommunityRdbms(CommunityRDBMS.MYSQL)
    public static class MySqlTestLiquibaseAutoConfiguration {

        @Bean
        @Primary
        public LiquibaseProperties mysqlLiquibaseProperties() {
            LiquibaseProperties liquibaseProperties = new LiquibaseProperties();
            liquibaseProperties.setChangeLog("db/changelog/mysql/db.changelog.master.mysql.xml");
            return liquibaseProperties;
        }
    }
}
