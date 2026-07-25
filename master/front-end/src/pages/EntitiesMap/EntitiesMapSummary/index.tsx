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
import { useTranslation } from "react-i18next";

import styles from "./styles.module.css";

interface IProps {
    /**
     * The total number of mapped entities.
     */
    analyzed: number;

    /**
     * The number of entities with at least one flagged association.
     */
    problematic: number;

    /**
     * The number of clean entities.
     */
    clean: number;
}

export const EntitiesMapSummary = ({ analyzed, problematic, clean }: IProps) => {
    const { t } = useTranslation();

    return (
        <>
            <div className={styles.MainWrapper}>
                <div className={styles.Card}>
                    <span className={`TextUltraSmall ${styles.Label}`}>{t("EntitiesMap.summary.mapped")}</span>
                    <span className="TextLarge">{analyzed}</span>
                </div>
                <div className={styles.Card}>
                    <span className={`TextUltraSmall ${styles.Label}`}>{t("EntitiesMap.summary.withProblems")}</span>
                    <span className="TextLarge">{problematic}</span>
                </div>
                <div className={styles.Card}>
                    <span className={`TextUltraSmall ${styles.Label}`}>{t("EntitiesMap.summary.clean")}</span>
                    <span className="TextLarge">{clean}</span>
                </div>
            </div>
        </>
    );
};
