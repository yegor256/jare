/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.tk;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.takes.Response;
import org.takes.rq.RqFake;

/**
 * Integration case for {@link TkInvalidate}.
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class TkInvalidateITCase {

    /**
     * TkInvalidate can invalidate URL.
     * @throws Exception If some problem inside
     */
    @Test
    @Ignore
    public void invalidatesUrl() throws Exception {
        final String url =
            "http://www.yegor256.com/images/yegor-bugayenko-192x192.png";
        final Response rsp = new TkInvalidate(
            "-key-", "-secret-"
        ).act(new RqFake("GET", String.format("/invalidate?url=%s", url)));
        MatcherAssert.assertThat(
            rsp.toString(),
            Matchers.containsString("InProgress")
        );
    }

}
