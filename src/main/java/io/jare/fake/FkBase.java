/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.fake;

import io.jare.model.Base;
import io.jare.model.Domain;
import io.jare.model.User;
import java.util.Collections;

/**
 * Fake Base.
 *
 * @since 1.0
 */
public final class FkBase implements Base {

    @Override
    public User user(final String name) {
        return new FkUser();
    }

    @Override
    public Iterable<Domain> domain(final String name) {
        return Collections.<Domain>singleton(new FkDomain());
    }

    @Override
    public Iterable<Domain> all() {
        return Collections.<Domain>singleton(new FkDomain());
    }

}
