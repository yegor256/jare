/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.tk;

import io.jare.fake.FkBase;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.codecs.CcPlain;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;

/**
 * Integration case for {@link TkInvalidate}.
 *
 * @since 1.0
 */
final class TkInvalidateITCase {

    /**
     * TkInvalidate can invalidate URL.
     *
     * @throws Exception If some problem inside
     */
    @Test
    @Disabled
    void invalidatesUrl() throws Exception {
        MatcherAssert.assertThat(
            new TkInvalidate(new FkBase(), "-key-", "-secret-").act(
                new RqWithHeader(
                    new RqFake(
                        "GET",
                        String.format(
                            "/invalidate?url=%s",
                            "http://www.yegor256.com/images/yegor-bugayenko-192x192.png"
                        )
                    ),
                    "TkAuth",
                    new String(
                        new CcPlain().encode(
                            new Identity.Simple(
                                "urn:test:1",
                                Collections.singletonMap(
                                    "login", "name:test:1"
                                )
                            )
                        ),
                        StandardCharsets.UTF_8
                    )
                )
            ).toString(),
            Matchers.containsString("InProgress")
        );
    }
}
