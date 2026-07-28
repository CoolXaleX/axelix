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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads {@link AxelixInfoProperties} from the raw {@code axelix-info.properties} content.
 *
 * @author Nikita Kirillov
 */
public class AxelixInfoPropertiesLoader {

    public static final String AXELIX_INFO_LOCATION =
            "${axelix.info.location:classpath:META-INF/axelix-info.properties}";

    private AxelixInfoPropertiesLoader() {}

    /**
     * @param inputStream the stream over the raw {@code axelix-info.properties} content. Not closed
     *                    by this method - the caller remains responsible for it.
     * @return the parsed properties.
     */
    public static AxelixInfoProperties load(InputStream inputStream) throws IOException {
        Properties properties = new Properties();
        properties.load(inputStream);
        return new AxelixInfoProperties(properties);
    }

    public static AxelixInfoProperties empty() {
        return new AxelixInfoProperties(new Properties());
    }
}
