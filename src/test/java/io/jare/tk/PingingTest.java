/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge,  to any person obtaining
 * a copy  of  this  software  and  associated  documentation files  (the
 * "Software"),  to deal in the Software  without restriction,  including
 * without limitation the rights to use,  copy,  modify,  merge, publish,
 * distribute,  sublicense,  and/or sell  copies of the Software,  and to
 * permit persons to whom the Software is furnished to do so,  subject to
 * the  following  conditions:   the  above  copyright  notice  and  this
 * permission notice  shall  be  included  in  all copies or  substantial
 * portions of the Software.  The software is provided  "as is",  without
 * warranty of any kind, express or implied, including but not limited to
 * the warranties  of merchantability,  fitness for  a particular purpose
 * and non-infringement.  In  no  event shall  the  authors  or copyright
 * holders be liable for any claim,  damages or other liability,  whether
 * in an action of contract,  tort or otherwise,  arising from, out of or
 * in connection with the software or  the  use  or other dealings in the
 * software.
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
