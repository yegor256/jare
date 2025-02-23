/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.tk;

import io.jare.model.Base;
import io.jare.model.Domain;
import java.io.IOException;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.xe.XeAppend;
import org.takes.rs.xe.XeDirectives;
import org.takes.rs.xe.XeSource;
import org.takes.rs.xe.XeTransform;
import org.xembly.Directives;

/**
 * Index page, for anonymous users.
 *
 * @since 1.0
 */
final class TkIndex implements Take {

    /**
     * Base.
     */
    private final transient Base base;

    /**
     * Ctor.
     * @param bse Base
     */
    TkIndex(final Base bse) {
        this.base = bse;
    }

    @Override
    public Response act(final Request req) throws IOException {
        return new RsPage(
            "/xsl/index.xsl",
            req,
            new XeAppend(
                "domains",
                new XeTransform<>(
                    this.base.all(),
                    TkIndex::source
                )
            )
        );
    }

    /**
     * Convert event to Xembly.
     * @param domain The event
     * @return Xembly
     * @throws IOException If fails
     */
    private static XeSource source(final Domain domain) throws IOException {
        return new XeDirectives(
            new Directives()
                .add("domain")
                .add("name").set(domain.name()).up()
                .add("owner").set(domain.owner()).up()
                .add("usage").set(domain.usage().total()).up()
                .up()
        );
    }

}
