/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.model;

import java.io.IOException;

/**
 * Domain.
 *
 * @since 1.0
 */
public interface Domain {

    /**
     * Owner of it.
     * @return The owner's GitHub handle
     * @throws IOException If fails
     */
    String owner() throws IOException;

    /**
     * Name.
     * @return The name
     * @throws IOException If fails
     */
    String name() throws IOException;

    /**
     * Delete it.
     * @throws IOException If fails
     */
    void delete() throws IOException;

    /**
     * Usage.
     * @return Usage
     * @throws IOException If fails
     */
    Usage usage() throws IOException;

}
