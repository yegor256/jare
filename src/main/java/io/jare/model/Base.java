/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.model;

/**
 * Base.
 *
 * @since 1.0
 */
public interface Base {

    /**
     * Get user by GitHub handle.
     * @param name GitHub name of the user
     * @return The user
     */
    User user(String name);

    /**
     * Find domain by hostname.
     * @param name The name
     * @return The domain
     */
    Iterable<Domain> domain(String name);

    /**
     * All domains.
     * @return Full list of all domains
     */
    Iterable<Domain> all();

}
