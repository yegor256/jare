/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.dynamo;

import io.jare.model.Base;
import io.jare.model.Domain;
import io.jare.model.Usage;
import io.jare.model.User;
import java.util.Date;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Integration case for {@link DyUsage}.
 * @since 0.7
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class DyUsageITCase {

    /**
     * DyUsage can be record usage.
     * @throws Exception If some problem inside
     */
    @Test
    public void recordsUsage() throws Exception {
        final Base base = new DyBase(new Dynamo());
        final User user = base.user("Erik");
        final String name = "yegor256.com";
        user.add(name);
        final Domain domain = base.domain(name).iterator().next();
        final Usage usage = domain.usage();
        usage.add(new Date(), 1L);
        usage.add(new Date(), 1L);
        MatcherAssert.assertThat(usage.total(), Matchers.equalTo(2L));
        domain.delete();
    }

}
