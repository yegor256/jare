/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge,  to any person obtaining
 * a copy  of  this  software  and  associated  documentation files  (the
 * "Software"),  to deal in the Software  without restriction,  including
 * without limitation the rights to use,  copy,  modify,  merge, publish,
 * distribute,  sublicense,  and/or sell  copies of the Software,  and to
 * permit persons to whom the Software is furnished to do so,  subject to
 * the  following  conditions:   the  above  copyright  notice  and  this
 * permission notice  shall  be  included  in  all copies or  substantial
 * portions of the Software.  The software is provided  "as is",  without
 * warranty of any kind, express or implied, including but not limited to
 * the warranties  of merchantability,  fitness for  a particular purpose
 * and non-infringement.  In  no  event shall  the  authors  or copyright
 * holders be liable for any claim,  damages or other liability,  whether
 * in an action of contract,  tort or otherwise,  arising from, out of or
 * in connection with the software or  the  use  or other dealings in the
 * software.
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
