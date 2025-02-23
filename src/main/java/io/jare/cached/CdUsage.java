/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.cached;

import com.jcabi.aspects.Cacheable;
import io.jare.model.Usage;
import java.io.IOException;
import java.util.Date;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Cached Usage.
 *
 * @since 0.7
 */
@ToString
@EqualsAndHashCode(of = "origin")
final class CdUsage implements Usage {

    /**
     * Original.
     */
    private final transient Usage origin;

    /**
     * Ctor.
     * @param usage Original
     */
    CdUsage(final Usage usage) {
        this.origin = usage;
    }

    @Override
    @Cacheable.FlushBefore
    public void add(final Date date, final long bytes) throws IOException {
        this.origin.add(date, bytes);
    }

    @Override
    @Cacheable(lifetime = 1, unit = TimeUnit.HOURS)
    public long total() throws IOException {
        return this.origin.total();
    }

    @Override
    @Cacheable(lifetime = 1, unit = TimeUnit.HOURS)
    public SortedMap<Date, Long> history() throws IOException {
        return this.origin.history();
    }
}
