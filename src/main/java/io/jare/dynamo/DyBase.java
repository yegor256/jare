/*
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
import com.jcabi.dynamo.Conditions;
import com.jcabi.dynamo.QueryValve;
import com.jcabi.dynamo.Region;
import com.jcabi.dynamo.ScanValve;
import com.jcabi.dynamo.Table;
import io.jare.model.Base;
import io.jare.model.Domain;
import io.jare.model.User;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Dynamo Base.
 *
 * @since 1.0
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@ToString
@EqualsAndHashCode(of = "region")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class DyBase implements Base {

    /**
     * The region to work with.
     */
    private final transient Region region;

    /**
     * Ctor.
     */
    public DyBase() {
        this(new Dynamo());
    }

    /**
     * Ctor.
     * @param reg Region
     */
    public DyBase(final Region reg) {
        this.region = reg;
    }

    @Override
    public User user(final String name) {
        return new DyUser(this.region, name);
    }

    @Override
    public Iterable<Domain> domain(final String name) {
        return this.table()
            .frame()
            .through(
                new QueryValve()
                    .withSelect(Select.ALL_ATTRIBUTES)
                    .withLimit(1)
                    .withConsistentRead(true)
            )
            .where(
                "domain",
                Conditions.equalTo(name.toLowerCase(Locale.ENGLISH))
            )
            .stream()
            .map(DyDomain::new)
            .map(Domain.class::cast)
            .collect(Collectors.toList());
    }

    @Override
    public Iterable<Domain> all() {
        return this.table()
            .frame()
            .through(
                new ScanValve()
                    .withAttributeToGet("user", "domain", "total", "usage")
                    .withLimit(1000)
            )
            .stream()
            .map(DyDomain::new)
            .map(Domain.class::cast)
            .collect(Collectors.toList());
    }

    /**
     * Table to work with.
     * @return Table
     */
    private Table table() {
        return this.region.table("domains");
    }

}
