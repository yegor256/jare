/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2020 Yegor Bugayenko
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
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
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
