/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.dynamo;

import com.jcabi.dynamo.Attributes;
import com.jcabi.dynamo.Item;
import io.jare.model.Domain;
import io.jare.model.Usage;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Dynamo domain.
 *
 * @since 1.0
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@ToString
@EqualsAndHashCode(of = "item")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class DyDomain implements Domain {

    /**
     * The item.
     */
    private final transient Item item;

    /**
     * Ctor.
     * @param itm Item
     */
    public DyDomain(final Item itm) {
        this.item = itm;
    }

    @Override
    public String owner() throws IOException {
        return this.item.get("user").getS();
    }

    @Override
    public String name() throws IOException {
        return this.item.get("domain").getS();
    }

    @Override
    public void delete() throws IOException {
        this.item.frame().table().delete(
            new Attributes().with("domain", this.name())
        );
    }

    @Override
    public Usage usage() {
        return new DyUsage(this.item);
    }

}
