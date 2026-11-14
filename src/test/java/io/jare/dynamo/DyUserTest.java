/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.dynamo;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.jcabi.dynamo.Attributes;
import com.jcabi.dynamo.Region;
import com.jcabi.dynamo.mock.H2Data;
import com.jcabi.dynamo.mock.MkRegion;
import io.jare.model.Base;
import io.jare.model.User;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link DyUser}.
 *
 * @since 1.0
 */
final class DyUserTest {

    /**
     * DyUser can keep usage when the owner adds the same domain again.
     *
     * @throws Exception If some problem inside
     */
    @Test
    void keepsUsageWhenOwnerAddsDomainAgain() throws Exception {
        final Region region = new MkRegion(
            new H2Data().with(
                "domains",
                new String[] {"domain"},
                "user", "usage", "total"
            )
        );
        region.table("domains").put(
            new Attributes()
                .with("domain", "example.com")
                .with("user", "alice")
                .with("usage", "<usage/>")
                .with("total", new AttributeValue().withN("0"))
        );
        final Base base = new DyBase(region);
        base.domain("example.com").iterator().next().usage()
            .add(LocalDate.now(ZoneOffset.UTC), 123L);
        final User user = base.user("alice");
        user.add("example.com");
        MatcherAssert.assertThat(
            base.domain("example.com").iterator().next().usage().total(),
            Matchers.equalTo(123L)
        );
    }
}
