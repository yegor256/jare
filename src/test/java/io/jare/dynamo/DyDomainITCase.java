/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.dynamo;

import io.jare.model.Base;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Integration case for {@link DyDomain}.
 * @since 1.0
 */
final class DyDomainITCase {

    /**
     * DyDomain can report its own name.
     * @throws Exception If some problem inside
     */
    @Test
    void reportsName() throws Exception {
        final Base base = new DyBase(new Dynamo());
        final String name = "dydomain-name.com";
        base.user("john").add(name);
        MatcherAssert.assertThat(
            base.domain(name).iterator().next().name(),
            Matchers.equalTo(name)
        );
    }

    /**
     * DyDomain can report its own owner.
     * @throws Exception If some problem inside
     */
    @Test
    void reportsOwner() throws Exception {
        final Base base = new DyBase(new Dynamo());
        final String john = "john";
        base.user(john).add("dydomain-owner.com");
        MatcherAssert.assertThat(
            base.domain("dydomain-owner.com").iterator().next().owner(),
            Matchers.equalTo(john)
        );
    }

    /**
     * DyDomain can be removed.
     * @throws Exception If some problem inside
     */
    @Test
    void isRemovedAfterDelete() throws Exception {
        final Base base = new DyBase(new Dynamo());
        final String name = "dydomain-removed.com";
        base.user("john").add(name);
        base.domain(name).iterator().next().delete();
        MatcherAssert.assertThat(
            base.domain(name).iterator().hasNext(),
            Matchers.equalTo(false)
        );
    }
}
