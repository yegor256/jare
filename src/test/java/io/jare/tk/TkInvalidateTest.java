/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.tk;

import io.jare.fake.FkBase;
import io.jare.model.Base;
import io.jare.model.Domain;
import io.jare.model.User;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.Request;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.codecs.CcPlain;
import org.takes.facets.forward.RsForward;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkInvalidate}.
 *
 * @since 1.0
 */
final class TkInvalidateTest {

    /**
     * TkInvalidate can reject a request without the url parameter.
     *
     * @throws Exception If some problem inside
     */
    @Test
    void rejectsMissingUrlParameter() throws Exception {
        MatcherAssert.assertThat(
            TkInvalidateTest.forward(
                new FkBase(),
                new RqFake("GET", "/invalidate")
            ),
            Matchers.allOf(
                Matchers.containsString("HTTP/1.1 303"),
                Matchers.containsString("RsFlash=url+parameter+is+required")
            )
        );
    }

    /**
     * TkInvalidate can reject a URL that has no host.
     *
     * @throws Exception If some problem inside
     */
    @Test
    void rejectsUrlWithoutHost() throws Exception {
        MatcherAssert.assertThat(
            TkInvalidateTest.forward(
                new FkBase(),
                new RqFake("GET", "/invalidate?url=nonhost")
            ),
            Matchers.containsString(
                "RsFlash=URL+%22nonhost%22+is+not+valid"
            )
        );
    }

    /**
     * TkInvalidate can reject a URL of a domain that doesn't exist.
     *
     * @throws Exception If some problem inside
     */
    @Test
    void rejectsUnknownDomain() throws Exception {
        MatcherAssert.assertThat(
            TkInvalidateTest.forward(
                new TkInvalidateTest.EmptyBase(),
                new RqFake("GET", "/invalidate?url=https://example.com/a.png")
            ),
            Matchers.containsString(
                "RsFlash=domain+%22example.com%22+doesn%27t+exist"
            )
        );
    }

    /**
     * TkInvalidate can reject a URL of a domain that is not ours.
     *
     * @throws Exception If some problem inside
     */
    @Test
    void rejectsForeignDomain() throws Exception {
        MatcherAssert.assertThat(
            TkInvalidateTest.forward(
                new FkBase(),
                TkInvalidateTest.authenticated(
                    "/invalidate?url=https://example.com/a.png", "mike"
                )
            ),
            Matchers.containsString(
                "RsFlash=domain+%22example.com%22+is+not+yours"
            )
        );
    }

    // TkInvalidate forwards, when it can't invalidate
    private static String forward(final Base base, final Request req)
        throws IOException {
        try {
            new TkInvalidate(base, "-", "-").act(req);
        } catch (final RsForward ex) {
            return new RsPrint(ex).printHead();
        }
        throw new IllegalStateException("no redirect");
    }

    // Authenticated request of a user with this login
    private static Request authenticated(final String target,
        final String login) throws IOException {
        return new RqWithHeader(
            new RqFake("GET", target),
            "TkAuth",
            new String(
                new CcPlain().encode(
                    new Identity.Simple(
                        "urn:test:1",
                        Collections.singletonMap("login", login)
                    )
                ),
                StandardCharsets.UTF_8
            )
        );
    }

    /**
     * Base without any domains.
     *
     * @since 1.0
     */
    private static final class EmptyBase implements Base {

        @Override
        public User user(final String name) {
            throw new UnsupportedOperationException("#user()");
        }

        @Override
        public Iterable<Domain> domain(final String name) {
            return Collections.emptyList();
        }

        @Override
        public Iterable<Domain> all() {
            return Collections.emptyList();
        }
    }
}
