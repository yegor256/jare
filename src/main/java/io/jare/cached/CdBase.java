/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
