/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.tk;

import io.jare.fake.FkBase;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Collection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.takes.Take;
import org.takes.facets.hamcrest.HmRsStatus;
import org.takes.rq.RqFake;

/**
 * Test case for {@link TkApp}.
 * @since 1.0
 */
@RunWith(Parameterized.class)
public final class PingingTest {

    /**
     * The URL to ping.
     */
    private final transient String url;

    /**
     * Ctor.
     * @param addr The URL to test
     */
    public PingingTest(final String addr) {
        this.url = addr;
    }

    /**
     * Params for JUnit.
     * @return Parameters
     */
    @Parameterized.Parameters
    public static Collection<Object[]> params() {
        return Arrays.asList(
            new Object[][] {
                {"/?x=y"},
                {"/robots.txt"},
                {"/xsl/layout.xsl"},
                {"/css/main.css"},
                {"/images/logo.svg"},
                {"/images/logo.png"},
            }
        );
    }

    /**
     * App can render the URL.
     * @throws Exception If some problem inside
     */
    @Test
    public void rendersAllPossibleUrls() throws Exception {
        final Take take = new TkApp(new FkBase());
        MatcherAssert.assertThat(
            this.url,
            take.act(new RqFake("INFO", this.url)),
            Matchers.not(
                new HmRsStatus(
                    HttpURLConnection.HTTP_NOT_FOUND
                )
            )
        );
    }

}
