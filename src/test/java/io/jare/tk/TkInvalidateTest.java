/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.tk;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link TkInvalidate}.
 *
 * @since 1.0
 */
final class TkInvalidateTest {

    /**
     * TkInvalidate can build a CloudFront client with an explicit region.
     */
    @Test
    void buildsCloudFrontClientWithExplicitRegion() {
        MatcherAssert.assertThat(
            TkInvalidate.client("-key-", "-secret-"),
            Matchers.notNullValue()
        );
    }
}
