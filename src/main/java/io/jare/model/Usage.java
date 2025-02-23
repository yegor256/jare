/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.model;

import java.io.IOException;
import java.util.Date;
import java.util.SortedMap;

/**
 * Usage.
 *
 * @since 0.7
 */
public interface Usage {

    /**
     * Add more usage in bytes.
     * @param date When did it happen
     * @param bytes How many bytes
     * @throws IOException If fails
     */
    void add(Date date, long bytes) throws IOException;

    /**
     * Total, over the last ten days.
     * @return The total in bytes
     * @throws IOException If fails
     */
    long total() throws IOException;

    /**
     * History.
     * @return Full usage history
     * @throws IOException If fails
     */
    SortedMap<Date, Long> history() throws IOException;

}
