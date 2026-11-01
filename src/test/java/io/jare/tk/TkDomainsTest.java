/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.tk;

import com.jcabi.xml.XMLDocument;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link TkDomains}.
 *
 * @since 1.0
 */
final class TkDomainsTest {

    @Test
    void usesStandardOutputMethodInStylesheet() throws Exception {
        MatcherAssert.assertThat(
            "domains stylesheet declares an output method that Firefox rejects",
            new XMLDocument(
                new TextOf(new ResourceOf("xsl/domains.xsl")).asString()
            ).xpath("/xsl:stylesheet/xsl:output/@method"),
            Matchers.everyItem(Matchers.is(Matchers.oneOf("xml", "html", "text")))
        );
    }
}
