/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.tk;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;

/**
 * Integration case for {@link TkInvalidate}.
 * @since 1.0
 */
final class TkInvalidateITCase {

    /**
     * TkInvalidate can invalidate URL.
     * @throws Exception If some problem inside
     */
    @Test
    @Disabled
    void invalidatesUrl() throws Exception {
        MatcherAssert.assertThat(
            new TkInvalidate("-key-", "-secret-").act(
                new RqFake(
                    "GET",
                    String.format(
                        "/invalidate?url=%s",
                        "http://www.yegor256.com/images/yegor-bugayenko-192x192.png"
                    )
                )
            ).toString(),
            Matchers.containsString("InProgress")
        );
    }
}
