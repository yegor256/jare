/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.cached;

import io.jare.fake.FkBase;
import io.jare.model.Usage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link CdUsage}.
 * @since 0.7
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class CdUsageTest {

    /**
     * CdUsage can make objects right.
     * @throws Exception If some problem inside
     */
    @Test
    public void makesObjects() throws Exception {
        final Usage usage = new CdBase(new FkBase()).domain("")
            .iterator().next().usage();
        MatcherAssert.assertThat(usage.total(), Matchers.notNullValue());
    }

}
