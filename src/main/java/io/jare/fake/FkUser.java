/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.fake;

import io.jare.model.Domain;
import io.jare.model.User;
import java.util.Collections;

/**
 * Fake user.
 *
 * @since 1.0
 */
public final class FkUser implements User {

    @Override
    public Iterable<Domain> mine() {
        return Collections.emptyList();
    }

    @Override
    public void add(final String name) {
        // nothing
    }

}
