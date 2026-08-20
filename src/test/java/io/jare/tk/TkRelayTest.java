/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.tk;

import io.jare.fake.FkBase;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.Take;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.http.FtRemote;
import org.takes.rq.RqFake;
import org.takes.rq.RqHref;
import org.takes.rs.RsPrint;
import org.takes.rs.RsText;
import org.takes.tk.TkText;
import org.takes.tk.TkWithHeaders;

/**
 * Test case for {@link TkRelay}.
 * @since 1.0
 */
final class TkRelayTest {

    /**
     * TkRelay can send the request through.
     * @throws Exception If some problem inside
     */
    @Test
    void sendsRequestThroughToHome() throws Exception {
        new FtRemote(
            new TkFork(
                new FkRegex(
                    "/alpha/.*",
                    (Take) req -> new RsText(
                        new RqHref.Base(req).href().toString()
                    )
                )
            )
        ).exec(
            home -> MatcherAssert.assertThat(
                new RsPrint(
                    new TkRelay(new FkBase()).act(
                        TkRelayTest.fake(
                            home, "/alpha/%D0%B4%D0%B0?abc=cde"
                        )
                    )
                ).printBody(),
                Matchers.equalTo(
                    String.format(
                        "%s/alpha/%%D0%%B4%%D0%%B0?abc=cde",
                        home
                    )
                )
            )
        );
    }

    /**
     * TkRelay can fail if URL is not valid (space is not allowed).
     * @throws Exception If some problem inside
     */
    @Test
    void catchesInvalidUrls() throws Exception {
        Assertions.assertThrows(
            HttpException.class,
            () -> new TkRelay(new FkBase()).act(
                new RqFake(
                    Arrays.asList(
                        "GET /?u=http://www.yegor256.com/a+b",
                        "Host: 127.0.0.1"
                    ),
                    ""
                )
            )
        );
    }

    /**
     * TkRelay can set cache headers to "forever".
     * @throws Exception If some problem inside
     */
    @Test
    void setsCachingHeaders() throws Exception {
        new FtRemote(
            new TkWithHeaders(
                new TkText("cacheable forever"),
                "age: 600",
                "cache-control: max-age=600",
                "expires: Thu, 08 Dec 2016 22:51:37 GMT"
            )
        ).exec(
            home -> MatcherAssert.assertThat(
                new RsPrint(
                    new TkRelay(new FkBase()).act(
                        TkRelayTest.fake(home, "/&whatever")
                    )
                ).print(),
                Matchers.allOf(
                    Matchers.containsString("Age: 31536000"),
                    Matchers.containsString("Cache-Control: max-age=31536000"),
                    Matchers.containsString("Expires: Sun, 19 Jul 2020 18:06:32 GMT"),
                    Matchers.not(Matchers.containsString("Cache-Control: max-age=600"))
                )
            )
        );
    }

    private static Request fake(final URI home, final String path) {
        return new RqFake(
            Arrays.asList(
                String.format(
                    "GET /?u=%s",
                    URLEncoder.encode(
                        home.resolve(path).toString(),
                        StandardCharsets.UTF_8
                    )
                ),
                "Host: localhost"
            ),
            ""
        );
    }
}
