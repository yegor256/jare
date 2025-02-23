/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.dynamo;

import com.amazonaws.services.dynamodbv2.model.Select;
import com.jcabi.dynamo.Attributes;
import com.jcabi.dynamo.Conditions;
import com.jcabi.dynamo.QueryValve;
import com.jcabi.dynamo.Region;
import com.jcabi.dynamo.Table;
import io.jare.model.Domain;
import io.jare.model.User;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Dynamo user.
 *
 * @since 1.0
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@ToString
@EqualsAndHashCode(of = { "region", "handle" })
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class DyUser implements User {

    /**
     * The region to work with.
     */
    private final transient Region region;

    /**
     * The name of him.
     */
    private final transient String handle;

    /**
     * Ctor.
     * @param reg Region
     * @param name Name of him
     */
    public DyUser(final Region reg, final String name) {
        this.region = reg;
        this.handle = name.toLowerCase(Locale.ENGLISH);
    }

    @Override
    public Iterable<Domain> mine() {
        return this.table()
            .frame()
            .through(
                new QueryValve()
                    .withSelect(Select.ALL_ATTRIBUTES)
                    .withLimit(100)
                    .withConsistentRead(false)
                    .withIndexName("mine")
            )
            .where("user", Conditions.equalTo(this.handle))
            .stream()
            .map(DyDomain::new)
            .map(Domain.class::cast)
            .collect(Collectors.toList());
    }

    @Override
    public void add(final String name) throws IOException {
        synchronized (this.region) {
            final Iterator<Domain> before = new DyBase(this.region)
                .domain(name).iterator();
            if (before.hasNext()) {
                final Domain domain = before.next();
                if (!domain.owner().equals(this.handle)) {
                    throw new IOException(
                        String.format(
                            "Domain \"%s\" is occupied by @%s",
                            domain.name(), domain.owner()
                        )
                    );
                }
            }
            this.table().put(
                new Attributes()
                    .with("user", this.handle)
                    .with("domain", name.toLowerCase(Locale.ENGLISH))
            );
        }
    }

    /**
     * Table to work with.
     * @return Table
     */
    private Table table() {
        return this.region.table("domains");
    }

}
