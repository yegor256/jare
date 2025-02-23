/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.model;

import java.io.IOException;

/**
 * User.
 *
 * @since 1.0
 */
public interface User {

    /**
     * All my domains.
     * @return All domains
     */
    Iterable<Domain> mine();

    /**
     * Add a domain.
     * @param name The name of the domain
     * @throws IOException If fails
     */
    void add(String name) throws IOException;

}
