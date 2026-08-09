/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.tk;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.facets.auth.RqWithAuth;

/**
 * Test case for {@link RqUser}.
 * @since 0.2
 */
final class RqUserTest {

    /**
     * RqUser can fetch user name.
     * @throws Exception If some problem inside
     */
    @Test
    void fetchesUserName() throws Exception {
        MatcherAssert.assertThat(
            new RqUser(new RqWithAuth("urn:github:yegor256")).name(),
            Matchers.nullValue()
        );
    }
}
