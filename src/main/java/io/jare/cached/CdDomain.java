/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.cached;

import com.jcabi.aspects.Cacheable;
import io.jare.model.Domain;
import io.jare.model.Usage;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Cached Domain.
 *
 * @since 1.0
 */
@ToString
@EqualsAndHashCode(of = "origin")
final class CdDomain implements Domain {

    /**
     * Original.
     */
    private final transient Domain origin;

    /**
     * Ctor.
     * @param domain Original
     */
    CdDomain(final Domain domain) {
        this.origin = domain;
    }

    @Override
    @Cacheable(forever = true)
    public String owner() throws IOException {
        return this.origin.owner();
    }

    @Override
    @Cacheable(forever = true)
    public String name() throws IOException {
        return this.origin.name();
    }

    @Override
    public void delete() throws IOException {
        this.origin.delete();
    }

    @Override
    public Usage usage() throws IOException {
        return new CdUsage(this.origin.usage());
    }
}
