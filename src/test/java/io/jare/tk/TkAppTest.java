/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.tk;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import com.jcabi.http.response.XmlResponse;
import com.jcabi.http.wire.VerboseWire;
import com.jcabi.matchers.XhtmlMatchers;
import io.jare.fake.FkBase;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.http.FtRemote;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkApp}.
 * @since 1.0
 */
final class TkAppTest {

    /**
     * App can render front page.
     * @throws Exception If some problem inside
     */
    @Test
    void rendersHomePage() throws Exception {
        MatcherAssert.assertThat(
            XhtmlMatchers.xhtml(
                new RsPrint(
                    new TkApp(new FkBase()).act(
                        new RqWithHeader(
                            new RqFake("GET", "/"),
                            "Accept",
                            "text/xml"
                        )
                    )
                ).printBody()
            ),
            XhtmlMatchers.hasXPaths(
                "/page/@date",
                "/page/@sla",
                "/page/millis",
                "/page/links/link[@rel='takes:logout']"
            )
        );
    }

    /**
     * App can render front page.
     * @throws Exception If some problem inside
     */
    @Test
    void rendersHomePageViaHttp() throws Exception {
        new FtRemote(new TkApp(new FkBase())).exec(
            home -> {
                new JdkRequest(home)
                    .header("Accept", "text/html")
                    .fetch()
                    .as(RestResponse.class)
                    .assertStatus(HttpURLConnection.HTTP_OK)
                    .as(XmlResponse.class)
                    .assertXPath("/xhtml:html");
                new JdkRequest(home)
                    .through(VerboseWire.class)
                    .header("Accept", "application/xml")
                    .fetch()
                    .as(RestResponse.class)
                    .assertStatus(HttpURLConnection.HTTP_OK)
                    .as(XmlResponse.class)
                    .assertXPath("/page/version");
            }
        );
    }

    /**
     * App can render not found.
     * @throws Exception If some problem inside
     */
    @Test
    void rendersNotFoundPage() throws Exception {
        MatcherAssert.assertThat(
            new RsPrint(
                new TkApp(new FkBase()).act(new RqFake("HEAD", "/not-found"))
            ).printBody(),
            Matchers.equalTo("Page not found")
        );
    }
}
