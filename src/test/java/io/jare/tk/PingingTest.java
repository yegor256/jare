/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.tk;

import io.jare.fake.FkBase;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.facets.hamcrest.HmRsStatus;
import org.takes.rq.RqFake;

/**
 * Test case for {@link TkApp}.
 * @since 1.0
 */
final class PingingTest {

    /**
     * App can render the URL.
     * @throws Exception If some problem inside
     */
    @Test
    void rendersAllPossibleUrls() throws Exception {
        final String[] urls = {
            "/?x=y",
            "/robots.txt",
            "/xsl/layout.xsl",
            "/css/main.css",
            "/images/logo.svg",
            "/images/logo.png",
        };
        for (final String url : urls) {
            MatcherAssert.assertThat(
                url,
                new TkApp(new FkBase()).act(new RqFake("INFO", url)),
                Matchers.not(
                    new HmRsStatus(
                        HttpURLConnection.HTTP_NOT_FOUND
                    )
                )
            );
        }
    }
}
