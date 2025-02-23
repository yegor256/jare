/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.fake;

import io.jare.model.Domain;
import io.jare.model.Usage;
import java.io.IOException;

/**
 * Fake domain.
 *
 * @since 1.0
 */
public final class FkDomain implements Domain {

    @Override
    public String owner() {
        return "name:test:1";
    }

    @Override
    public String name() {
        return "jare.io";
    }

    @Override
    public void delete() {
        // nothing
    }

    @Override
    public Usage usage() throws IOException {
        return new FkUsage();
    }
}
