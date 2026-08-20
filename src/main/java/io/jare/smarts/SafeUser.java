/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.smarts;

import io.jare.model.Domain;
import io.jare.model.User;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Safe user.
 * @since 1.0
 */
public final class SafeUser implements User {

    /**
     * Pattern to match.
     */
    private static final Pattern PTN = Pattern.compile(
        "(?=^.{4,253}$)(^((?!-)[a-zA-Z0-9-]{1,63}(?<!-)\\.)+[a-zA-Z]{2,63}$)"
    );

    /**
     * Original one.
     */
    private final transient User origin;

    /**
     * Ctor.
     * @param user Original user
     */
    public SafeUser(final User user) {
        this.origin = user;
    }

    @Override
    public Iterable<Domain> mine() {
        return this.origin.mine();
    }

    @Override
    public void add(final String name) throws IOException {
        if (!SafeUser.PTN.matcher(name).matches()) {
            throw new InvalidNameException(name);
        }
        this.origin.add(name);
    }
}
