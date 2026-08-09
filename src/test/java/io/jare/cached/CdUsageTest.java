/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.cached;

import io.jare.fake.FkBase;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link CdUsage}.
 * @since 0.7
 */
final class CdUsageTest {

    /**
     * CdUsage can make objects right.
     * @throws Exception If some problem inside
     */
    @Test
    void makesObjects() throws Exception {
        MatcherAssert.assertThat(
            new CdBase(new FkBase()).domain("")
                .iterator().next().usage().total(),
            Matchers.notNullValue()
        );
    }
}
