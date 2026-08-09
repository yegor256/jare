/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.dynamo;

import io.jare.model.Base;
import io.jare.model.User;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Integration case for {@link DyBase}.
 * @since 1.0
 */
final class DyBaseITCase {

    /**
     * DyBase can list domains.
     * @throws Exception If some problem inside
     */
    @Test
    void listsAllDomains() throws Exception {
        final Base base = new DyBase(new Dynamo());
        final User user = base.user("erik");
        user.add("www.example.com");
        MatcherAssert.assertThat(
            base.all(),
            Matchers.iterableWithSize(Matchers.greaterThan(0))
        );
    }

    /**
     * DyBase can list domain by name.
     * @throws Exception If some problem inside
     */
    @Test
    void listsDomainByName() throws Exception {
        final Base base = new DyBase(new Dynamo());
        final User user = base.user("johnny");
        final String name = "www-1.example.com";
        user.add(name);
        MatcherAssert.assertThat(
            base.domain(name),
            Matchers.iterableWithSize(1)
        );
    }
}
