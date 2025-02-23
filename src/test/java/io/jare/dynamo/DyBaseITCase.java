/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.dynamo;

import io.jare.model.Base;
import io.jare.model.Domain;
import io.jare.model.User;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Integration case for {@link DyBase}.
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class DyBaseITCase {

    /**
     * DyBase can list domains.
     * @throws Exception If some problem inside
     */
    @Test
    public void listsAllDomains() throws Exception {
        final Base base = new DyBase(new Dynamo());
        final String erik = "erik";
        final User user = base.user(erik);
        final String name = "www.example.com";
        user.add(name);
        final Iterable<Domain> list = base.all();
        MatcherAssert.assertThat(
            list,
            Matchers.iterableWithSize(Matchers.greaterThan(0))
        );
        MatcherAssert.assertThat(
            list,
            Matchers.iterableWithSize(Matchers.greaterThan(0))
        );
    }

    /**
     * DyBase can list domain by name.
     * @throws Exception If some problem inside
     */
    @Test
    public void listsDomainByName() throws Exception {
        final Base base = new DyBase(new Dynamo());
        final String john = "johnny";
        final User user = base.user(john);
        final String name = "www-1.example.com";
        user.add(name);
        final Iterable<Domain> list = base.domain(name);
        MatcherAssert.assertThat(
            list,
            Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            list,
            Matchers.iterableWithSize(1)
        );
    }

}
