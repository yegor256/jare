/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2022 Yegor Bugayenko
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
import io.jare.model.Base;
import io.jare.model.Domain;
import io.jare.model.User;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.iterable.Mapped;

/**
 * Cached Base.
 *
 * @since 1.0
 */
@ToString
@EqualsAndHashCode(of = "origin")
public final class CdBase implements Base {

    /**
     * Original.
     */
    private final transient Base origin;

    /**
     * Ctor.
     * @param base Original
     */
    public CdBase(final Base base) {
        this.origin = base;
    }

    @Override
    @Cacheable(forever = true)
    public User user(final String name) {
        return this.origin.user(name);
    }

    @Override
    @Cacheable(lifetime = 1, unit = TimeUnit.MINUTES)
    public Iterable<Domain> domain(final String name) {
        return new Mapped<>(
            CdDomain::new,
            this.origin.domain(name)
        );
    }

    @Override
    @Cacheable(unit = TimeUnit.HOURS, lifetime = 1)
    public Iterable<Domain> all() {
        return new Mapped<>(
            CdDomain::new,
            this.origin.all()
        );
    }
}
