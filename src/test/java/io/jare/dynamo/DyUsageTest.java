/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
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
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link DyUsage}.
 * @since 0.7
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class DyUsageTest {

    /**
     * DyUsage can be record usage.
     * @throws Exception If some problem inside
     */
    @Test
    public void recordsUsage() throws Exception {
        final Usage usage = new DyUsage(DyUsageTest.item());
        usage.add(new Date(), 1L);
        usage.add(new Date(), 1L);
        MatcherAssert.assertThat(usage.total(), Matchers.equalTo(2L));
    }

    /**
     * DyUsage can be ignore old data.
     * @throws Exception If some problem inside
     */
    @Test
    public void ignoresOldData() throws Exception {
        final Usage usage = new DyUsage(DyUsageTest.item());
        usage.add(new Date(), 1L);
        usage.add(DateUtils.addDays(new Date(), -50), 1L);
        MatcherAssert.assertThat(usage.total(), Matchers.equalTo(1L));
    }

    /**
     * DyUsage can be print history.
     * @throws Exception If some problem inside
     */
    @Test
    public void printsHistory() throws Exception {
        final Usage usage = new DyUsage(DyUsageTest.item());
        usage.add(new Date(), 1L);
        usage.add(new Date(), 1L);
        MatcherAssert.assertThat(
            usage.history(),
            Matchers.hasEntry(
                Matchers.any(Date.class),
                Matchers.equalTo(2L)
            )
        );
    }

    /**
     * The item to work with.
     * @return Item to work with
     * @throws Exception If some problem inside
     */
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
