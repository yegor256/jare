/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.fake;

import com.jcabi.log.Logger;
import io.jare.model.Usage;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Fake usage.
 *
 * @since 0.7
 */
public final class FkUsage implements Usage {

    @Override
    public void add(final Date date, final long bytes) {
        Logger.info(this, "usage, date=%s, bytes=%d", date, bytes);
    }

    @Override
    public long total() {
        return 1L;
    }

    @Override
    public SortedMap<Date, Long> history() {
        return new TreeMap<>();
    }
}
