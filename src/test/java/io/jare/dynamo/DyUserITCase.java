/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.dynamo;

import io.jare.model.Base;
import io.jare.model.Domain;
import io.jare.model.User;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Integration case for {@link DyUser}.
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class DyUserITCase {

    /**
     * DyUser can add and remove domains.
     * @throws Exception If some problem inside
     */
    @Test
    public void addsAndRemoveDomains() throws Exception {
        final Base base = new DyBase(new Dynamo());
        final User user = base.user("jeffrey");
        final String name = "google.com";
        user.add(name);
        final Domain domain = base.domain(name).iterator().next();
        MatcherAssert.assertThat(
            domain.name(),
            Matchers.equalTo(name)
        );
        domain.delete();
        MatcherAssert.assertThat(
            base.domain(name).iterator().hasNext(),
            Matchers.equalTo(false)
        );
    }

    /**
     * DyUser can list domains.
     * @throws Exception If some problem inside
     */
    @Test
    public void listsMineDomains() throws Exception {
        final Base base = new DyBase(new Dynamo());
        final User user = base.user("willy");
        for (int idx = 0; idx < 10; ++idx) {
            user.add(String.format("facebook-%d.com", idx));
        }
        final Iterable<Domain> list = user.mine();
        MatcherAssert.assertThat(
            list,
            Matchers.iterableWithSize(10)
        );
        MatcherAssert.assertThat(
            list,
            Matchers.iterableWithSize(10)
        );
    }

    /**
     * DyUser can reject if domain is occupied.
     * @throws Exception If some problem inside
     */
    @Test(expected = IOException.class)
    public void rejectsIfOccupied() throws Exception {
        final Base base = new DyBase(new Dynamo());
        base.user("melissa").add("yahoo.com");
        final User alex = base.user("alex");
        alex.add("Yahoo.com");
    }

}
