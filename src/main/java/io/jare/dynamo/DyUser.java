/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2022 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge,  to any person obtaining
 * a copy  of  this  software  and  associated  documentation files  (the
 * "Software"),  to deal in the Software  without restriction,  including
 * without limitation the rights to use,  copy,  modify,  merge, publish,
 * distribute,  sublicense,  and/or sell  copies of the Software,  and to
 * permit persons to whom the Software is furnished to do so,  subject to
 * the  following  conditions:   the  above  copyright  notice  and  this
 * permission notice  shall  be  included  in  all copies or  substantial
 * portions of the Software.  The software is provided  "as is",  without
 * warranty of any kind, express or implied, including but not limited to
 * the warranties  of merchantability,  fitness for  a particular purpose
 * and non-infringement.  In  no  event shall  the  authors  or copyright
 * holders be liable for any claim,  damages or other liability,  whether
 * in an action of contract,  tort or otherwise,  arising from, out of or
 * in connection with the software or  the  use  or other dealings in the
 * software.
 */
package io.jare.dynamo;

import com.amazonaws.services.dynamodbv2.model.Select;
import com.jcabi.aspects.Tv;
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
                    .withLimit(Tv.HUNDRED)
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
