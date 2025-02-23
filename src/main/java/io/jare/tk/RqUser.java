/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.tk;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.takes.Request;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.RqAuth;
import org.takes.rq.RqWrap;

/**
 * User in request.
 *
 * @since 1.0
 */
public final class RqUser extends RqWrap {

    /**
     * Pattern to fetch name.
     */
    private static final Pattern PTN = Pattern.compile(
        "urn:(?:github|test):(.*)"
    );

    /**
     * Ctor.
     * @param req Request
     */
    public RqUser(final Request req) {
        super(req);
    }

    /**
     * Get user name (GitHub handle).
     * @return Name
     * @throws IOException If fails
     */
    public String name() throws IOException {
        final Identity identity = new RqAuth(this).identity();
        final String urn = identity.urn();
        final Matcher mtr = RqUser.PTN.matcher(urn);
        if (!mtr.matches()) {
            throw new IllegalArgumentException(
                String.format("URN \"%s\" is not from GitHub", urn)
            );
        }
        return identity.properties().get("login");
    }

}
