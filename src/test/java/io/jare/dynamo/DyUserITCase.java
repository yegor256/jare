/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.dynamo;

import io.jare.model.Base;
import io.jare.model.User;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Integration case for {@link DyUser}.
 * @since 1.0
 */
final class DyUserITCase {

    /**
     * DyUser can add a domain.
     * @throws Exception If some problem inside
     */
    @Test
    void addsDomain() throws Exception {
        final Base base = new DyBase(new Dynamo());
        final User user = base.user("jeffrey");
        final String name = "google.com";
        user.add(name);
        MatcherAssert.assertThat(
            base.domain(name).iterator().next().name(),
            Matchers.equalTo(name)
        );
    }

    /**
     * DyUser can remove a domain.
     * @throws Exception If some problem inside
     */
    @Test
    void removesDomain() throws Exception {
        final Base base = new DyBase(new Dynamo());
        final User user = base.user("jeffrey");
        final String name = "google-removed.com";
        user.add(name);
        base.domain(name).iterator().next().delete();
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
    void listsMineDomains() throws Exception {
        final Base base = new DyBase(new Dynamo());
        final User user = base.user("willy");
        for (int idx = 0; idx < 10; ++idx) {
            user.add(String.format("facebook-%d.com", idx));
        }
        MatcherAssert.assertThat(
            user.mine(),
            Matchers.iterableWithSize(10)
        );
    }

    /**
     * DyUser can reject if domain is occupied.
     * @throws Exception If some problem inside
     */
    @Test
    void rejectsIfOccupied() throws Exception {
        final Base base = new DyBase(new Dynamo());
        base.user("melissa").add("yahoo.com");
        final User alex = base.user("alex");
        Assertions.assertThrows(
            IOException.class,
            () -> alex.add("Yahoo.com")
        );
    }
}
