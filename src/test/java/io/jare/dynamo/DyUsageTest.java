/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.dynamo;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.jcabi.dynamo.Attributes;
import com.jcabi.dynamo.Item;
import com.jcabi.dynamo.Region;
import com.jcabi.dynamo.Table;
import com.jcabi.dynamo.mock.H2Data;
import com.jcabi.dynamo.mock.MkRegion;
import io.jare.model.Usage;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link DyUsage}.
 * @since 0.7
 */
final class DyUsageTest {

    /**
     * DyUsage can be record usage.
     * @throws Exception If some problem inside
     */
    @Test
    void recordsUsage() throws Exception {
        final Usage usage = new DyUsage(DyUsageTest.item());
        usage.add(LocalDate.now(ZoneOffset.UTC), 1L);
        usage.add(LocalDate.now(ZoneOffset.UTC), 1L);
        MatcherAssert.assertThat(usage.total(), Matchers.equalTo(2L));
    }

    /**
     * DyUsage can be ignore old data.
     * @throws Exception If some problem inside
     */
    @Test
    void ignoresOldData() throws Exception {
        final Usage usage = new DyUsage(DyUsageTest.item());
        usage.add(LocalDate.now(ZoneOffset.UTC), 1L);
        usage.add(LocalDate.now(ZoneOffset.UTC).minusDays(50), 1L);
        MatcherAssert.assertThat(usage.total(), Matchers.equalTo(1L));
    }

    /**
     * DyUsage can be print history.
     * @throws Exception If some problem inside
     */
    @Test
    void printsHistory() throws Exception {
        final Usage usage = new DyUsage(DyUsageTest.item());
        usage.add(LocalDate.now(ZoneOffset.UTC), 1L);
        usage.add(LocalDate.now(ZoneOffset.UTC), 1L);
        MatcherAssert.assertThat(
            usage.history(),
            Matchers.hasEntry(
                Matchers.any(LocalDate.class),
                Matchers.equalTo(2L)
            )
        );
    }

    private static Item item() throws Exception {
        final Region region = new MkRegion(
            new H2Data().with(
                "domains",
                new String[] {"domain"},
                "owner", "usage", "total"
            )
        );
        final Table table = region.table("domains");
        table.put(
            new Attributes()
                .with("domain", "yegor256.com")
                .with("owner", new AttributeValue("yegor256"))
                .with("usage", new AttributeValue("<usage/>"))
                .with("total", new AttributeValue().withN("0"))
        );
        return table.frame()
            .where("domain", "yegor256.com")
            .iterator().next();
    }
}
