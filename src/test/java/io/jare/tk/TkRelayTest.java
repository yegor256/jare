/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017 Yegor Bugayenko
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
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.Take;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.facets.hamcrest.HmRsHeader;
import org.takes.http.FtRemote;
import org.takes.rq.RqFake;
import org.takes.rq.RqHref;
import org.takes.rs.RsPrint;
import org.takes.rs.RsText;
import org.takes.tk.TkText;
import org.takes.tk.TkWithHeaders;

/**
 * Test case for {@link TkRelay}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class TkRelayTest {

    /**
     * TkRelay can send the request through.
     * @throws Exception If some problem inside
     */
    @Test
    public void sendsRequestThroughToHome() throws Exception {
        final Take target = new TkFork(
            new FkRegex(
                "/alpha/.*",
                (Take) req -> new RsText(
                    new RqHref.Base(req).href().toString()
                )
            )
        );
        new FtRemote(target).exec(
            home -> MatcherAssert.assertThat(
                new RsPrint(
                    new TkRelay(new FkBase()).act(
                        TkRelayTest.fake(
                            home, "/alpha/%D0%B4%D0%B0?%D0%B0=%D0%B0"
                        )
                    )
                ).printBody(),
                Matchers.equalTo(
                    String.format(
                        "%s/alpha/%%D0%%B4%%D0%%B0?%%D0%%B0=%%D0%%B0", home
                    )
                )
            )
        );
    }

    /**
     * TkRelay can fail if URL is not valid (space is not allowed).
     * @throws Exception If some problem inside
     */
    @Test(expected = HttpException.class)
    public void catchesInvalidUrls() throws Exception {
        new TkRelay(new FkBase()).act(
            new RqFake(
                Arrays.asList(
                    "GET /?u=http://www.yegor256.com/a+b",
                    "Host: 127.0.0.1"
                ),
                ""
            )
        );
    }

    /**
     * TkRelay can set cache headers to "forever".
     * @throws Exception If some problem inside
     */
    @Test
    public void setsCachingHeaders() throws Exception {
        final Take target = new TkWithHeaders(
            new TkText("cacheable forever"),
            "age: 600",
            "cache-control: max-age=600",
            "expires: Thu, 08 Dec 2016 22:51:37 GMT"
        );
        new FtRemote(target).exec(
            home -> MatcherAssert.assertThat(
                new RsPrint(
                    new TkRelay(new FkBase()).act(
                        TkRelayTest.fake(home, "/&whatever")
                    )
                ),
                Matchers.allOf(
                    new HmRsHeader("Age", "31536000"),
                    new HmRsHeader("Cache-control", "max-age=31536000"),
                    new HmRsHeader("Expires", "Sun, 19 Jul 2020 18:06:32 GMT"),
                    Matchers.not(
                        new HmRsHeader("Cache-Control", "max-age=600")
                    )
                )
            )
        );
    }

    /**
     * Fake request.
     * @param home Base URI
     * @param path Path
     * @return Request
     * @throws UnsupportedEncodingException If fails
     */
    private static Request fake(final URI home, final String path)
        throws UnsupportedEncodingException {
        return new RqFake(
            Arrays.asList(
                String.format(
                    "GET /?u=%s",
                    URLEncoder.encode(
                        home.resolve(path).toString(),
                        "UTF-8"
                    )
                ),
                "Host: localhost"
            ),
            ""
        );
    }

}
