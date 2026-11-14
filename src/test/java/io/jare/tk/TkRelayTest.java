/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.tk;

import io.jare.fake.FkBase;
import java.net.HttpURLConnection;
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
 *
 * @since 1.0
 */
final class TkRelayTest {

    /**
     * TkRelay can send the request through.
     *
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
     *
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
     *
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

    /**
     * TkRelay can relay a target URL that has a host.
     *
     * @throws Exception If some problem inside
     */
    @Test
    void relaysTargetWithHost() throws Exception {
        new FtRemote(
            new TkFork(
                new FkRegex(
                    "/alpha/.*",
                    (Take) req -> new RsText("body")
                )
            )
        ).exec(
            home -> MatcherAssert.assertThat(
                new RsPrint(
                    new TkRelay(new FkBase()).act(
                        TkRelayTest.fake(home, "/alpha/logo.png")
                    )
                ).printBody(),
                Matchers.equalTo("body")
            )
        );
    }

    /**
     * TkRelay can reject a target without a host, e.g. "?u=foo".
     *
     * @throws Exception If some problem inside
     */
    @Test
    void rejectsTargetWithoutHost() throws Exception {
        TkRelayTest.assertRejectedAsBadRequest("foo");
    }

    /**
     * TkRelay can reject a file:// target, which has no host.
     *
     * @throws Exception If some problem inside
     */
    @Test
    void rejectsFileTargetWithoutHost() throws Exception {
        TkRelayTest.assertRejectedAsBadRequest("file:///etc/passwd");
    }

    /**
     * TkRelay can reject a target with an underscore in the authority,
     * which makes {@link URI#getHost()} return NULL.
     *
     * @throws Exception If some problem inside
     */
    @Test
    void rejectsTargetWithUnderscoreInHost() throws Exception {
        TkRelayTest.assertRejectedAsBadRequest("http://my_site.com/a.png");
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

    private static void assertRejectedAsBadRequest(final String target) {
        final HttpException err = Assertions.assertThrows(
            HttpException.class,
            () -> new TkRelay(new FkBase()).act(
                new RqFake(
                    Arrays.asList(
                        String.format(
                            "GET /?u=%s",
                            URLEncoder.encode(
                                target,
                                StandardCharsets.UTF_8
                            )
                        ),
                        "Host: 127.0.0.1"
                    ),
                    ""
                )
            )
        );
        MatcherAssert.assertThat(
            err.code(),
            Matchers.equalTo(HttpURLConnection.HTTP_BAD_REQUEST)
        );
        MatcherAssert.assertThat(
            err.getMessage(),
            Matchers.containsString(
                String.format("Target URL \"%s\" has no host", target)
            )
        );
    }
}
