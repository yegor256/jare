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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Integration case for {@link DyDomain}.
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class DyDomainITCase {

    /**
     * DyDomain can be added and removed.
     * @throws Exception If some problem inside
     */
    @Test
    public void addsAndRemoveDomains() throws Exception {
        final Base base = new DyBase(new Dynamo());
        final String john = "john";
        final User user = base.user(john);
        final String name = "google.com";
        user.add(name);
        final Domain domain = base.domain(name).iterator().next();
        MatcherAssert.assertThat(domain.name(), Matchers.equalTo(name));
        MatcherAssert.assertThat(domain.owner(), Matchers.equalTo(john));
        domain.delete();
        MatcherAssert.assertThat(
            base.domain(name).iterator().hasNext(),
            Matchers.equalTo(false)
        );
    }

}
