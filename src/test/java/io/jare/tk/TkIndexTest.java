/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.tk;

import com.jcabi.matchers.XhtmlMatchers;
import io.jare.fake.FkBase;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.takes.Take;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkIndex}.
 * @since 1.0
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class TkIndexTest {

    /**
     * TkHome can render home page.
     * @throws Exception If some problem inside
     */
    @Test
    public void rendersHomePage() throws Exception {
        final Take take = new TkAppAuth(new TkIndex(new FkBase()));
        MatcherAssert.assertThat(
            XhtmlMatchers.xhtml(
                new RsPrint(
                    take.act(
                        new RqWithHeader(
                            new RqFake("GET", "/"),
                            "Accept",
                            "text/xml"
                        )
                    )
                ).printBody()
            ),
            XhtmlMatchers.hasXPaths(
                "/page/millis",
                "/page/identity/urn",
                "/page/version",
                "/page/links/link[@rel='home']",
                "/page/links/link[@rel='self']",
                "/page/links/link[@rel='takes:logout']",
                "/page/domains/domain[name and owner and usage]"
            )
        );
    }

    /**
     * TkIndex can render home page in HTML.
     * @throws Exception If some problem inside
     */
    @Test
    public void rendersHomePageInHtml() throws Exception {
        final Take take = new TkAppAuth(new TkIndex(new FkBase()));
        MatcherAssert.assertThat(
            XhtmlMatchers.xhtml(
                new RsPrint(
                    take.act(new RqFake("GET", "/"))
                ).printBody()
            ),
            XhtmlMatchers.hasXPaths(
                "/xhtml:html",
                "/xhtml:html/xhtml:body"
            )
        );
    }

}
