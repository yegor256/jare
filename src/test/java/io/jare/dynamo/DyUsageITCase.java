/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.dynamo;

import io.jare.model.Base;
import io.jare.model.Domain;
import io.jare.model.Usage;
import io.jare.model.User;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Integration case for {@link DyUsage}.
 * @since 0.7
 */
final class DyUsageITCase {

    /**
     * DyUsage can be record usage.
     * @throws Exception If some problem inside
     */
    @Test
    void recordsUsage() throws Exception {
        final Base base = new DyBase(new Dynamo());
        final User user = base.user("Erik");
        final String name = "yegor256.com";
        user.add(name);
        final Domain domain = base.domain(name).iterator().next();
        final Usage usage = domain.usage();
        usage.add(LocalDate.now(ZoneOffset.UTC), 1L);
        usage.add(LocalDate.now(ZoneOffset.UTC), 1L);
        MatcherAssert.assertThat(usage.total(), Matchers.equalTo(2L));
        domain.delete();
    }
}
