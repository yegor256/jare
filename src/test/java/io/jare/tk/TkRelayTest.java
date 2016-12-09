/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 jare.io
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
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.HttpException;
import org.takes.Take;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.facets.hamcrest.HmRsHeader;
import org.takes.http.FtRemote;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;
import org.takes.tk.TkText;
import org.takes.tk.TkWithHeaders;

/**
 * Test case for {@link TkRelay}.
 * @author Yegor Bugayenko (yegor@teamed.io)
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
    public void sendsRequestThrough() throws Exception {
        final Take target = new TkFork(
            new FkRegex("/alpha/beta", new TkText("it's success"))
        );
        new FtRemote(target).exec(
            home -> MatcherAssert.assertThat(
                new RsPrint(
                    new TkRelay(new FkBase()).act(
                        new RqFake(
                            Arrays.asList(
                                String.format("GET /?u=%s/alpha/beta", home),
                                "Host: localhost"
                            ),
                            ""
                        )
                    )
                ).printBody(),
                Matchers.containsString("success")
            )
        );
    }

    /**
     * TkRelay can send the request through.
     * @throws Exception If some problem inside
     */
    @Test
    public void sendsRequestThroughToHome() throws Exception {
        final Take target = new TkFork(
            new FkRegex("/", new TkText("it's home"))
        );
        new FtRemote(target).exec(
            home -> MatcherAssert.assertThat(
                new RsPrint(
                    new TkRelay(new FkBase()).act(
                        new RqFake(
                            Arrays.asList(
                                String.format("GET /?u=%s", home),
                                "Host: localhost "
                            ),
                            ""
                        )
                    )
                ).printBody(),
                Matchers.containsString("home")
            )
        );
    }

    /**
     * TkRelay can tolerate complex URLs.
     * @throws Exception If some problem inside
     */
    @Test(expected = HttpException.class)
    public void catchesInvalidUrls() throws Exception {
        new TkRelay(new FkBase()).act(
            new RqFake(
                Arrays.asList(
                    "GET /?u=http://www.yegor256.com/i+%D1%85%D0%BC",
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
            "Age: 600",
            "Cache-control: max-age=600",
            "Expires: Thu, 08 Dec 2016 22:51:37 GMT"
        );
        new FtRemote(target).exec(
            home -> MatcherAssert.assertThat(
                new RsPrint(
                    new TkRelay(new FkBase()).act(
                        new RqFake(
                            Arrays.asList(
                                String.format("GET /?u=%s&whatever", home),
                                "Host: test.jare.io"
                            ),
                            ""
                        )
                    )
                ),
                Matchers.allOf(
                    new HmRsHeader("Age", "31536000"),
                    new HmRsHeader("Cache-Control", "max-age=31536000"),
                    new HmRsHeader("Expires", "Sun, 19 Jul 2020 18:06:32 GMT")
                )
            )
        );
    }

}
