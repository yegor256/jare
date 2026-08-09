/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.fake;

import com.jcabi.log.Logger;
import io.jare.model.Usage;
import java.time.LocalDate;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Fake usage.
 * @since 0.7
 */
public final class FkUsage implements Usage {

    @Override
    public void add(final LocalDate date, final long bytes) {
        Logger.info(this, "usage, date=%s, bytes=%d", date, bytes);
    }

    @Override
    public long total() {
        return 1L;
    }

    @Override
    public SortedMap<LocalDate, Long> history() {
        return new TreeMap<>();
    }
}
