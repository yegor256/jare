/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.tk;

import java.net.URI;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link Destination}.
 * @since 0.4
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class DestinationTest {

    /**
     * Destination can build a path.
     * @throws Exception If some problem inside
     */
    @Test
    public void buildsPath() throws Exception {
        MatcherAssert.assertThat(
            new Destination(
                new URI(
                    "http://www.google.com/a/b/%D0%B4%D0%B0?z=%D0%B0#%D0%B0"
                )
            ).path(),
            Matchers.equalTo("/a/b/%D0%B4%D0%B0?z=%D0%B0#%D0%B0")
        );
    }

    /**
     * Destination can build an empty path.
     * @throws Exception If some problem inside
     */
    @Test
    public void buildsEmptyPath() throws Exception {
        MatcherAssert.assertThat(
            new Destination(new URI("http://www.google.com")).path(),
            Matchers.equalTo("/")
        );
    }

    /**
     * Destination can build path with params.
     * @throws Exception If some problem inside
     */
    @Test
    public void buildsPathWithParams() throws Exception {
        MatcherAssert.assertThat(
            new Destination(
                new URI("http://www.google.com?%D0%B0=1")
            ).path(),
            Matchers.equalTo("/?%D0%B0=1")
        );
    }

    /**
     * Destination can build path with fragment.
     * @throws Exception If some problem inside
     */
    @Test
    public void buildsPathWithFragment() throws Exception {
        MatcherAssert.assertThat(
            new Destination(
                new URI("http://www.google.com/%D0%B0?%D0%B0=1#t")
            ).path(),
            Matchers.equalTo("/%D0%B0?%D0%B0=1#t")
        );
    }

}
