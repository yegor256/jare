/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.tk;

import io.jare.model.Base;
import io.jare.model.Domain;
import io.jare.model.Usage;
import java.io.IOException;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.misc.Href;
import org.takes.rs.xe.XeAppend;
import org.takes.rs.xe.XeDirectives;
import org.takes.rs.xe.XeLink;
import org.takes.rs.xe.XeSource;
import org.takes.rs.xe.XeTransform;
import org.xembly.Directives;

/**
 * Index page, for authenticated user.
 *
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class TkDomains implements Take {

    /**
     * Base.
     */
    private final transient Base base;

    /**
     * Ctor.
     * @param bse Base
     */
    TkDomains(final Base bse) {
        this.base = bse;
    }

    @Override
    public Response act(final Request req) throws IOException {
        return new RsPage(
            "/xsl/domains.xsl",
            req,
            new XeAppend(
                "domains",
                new XeTransform<>(
                    this.base.user(new RqUser(req).name()).mine(),
                    TkDomains::source
                )
            ),
            new XeLink("add", "/add"),
            new XeLink("invalidate", "/invalidate")
        );
    }

    /**
     * Convert event to Xembly.
     * @param domain The event
     * @return Xembly
     * @throws IOException If fails
     */
    private static XeSource source(final Domain domain) throws IOException {
        final String name = domain.name();
        final Usage usage = domain.usage();
        return new XeDirectives(
            new Directives()
                .add("domain")
                .add("name").set(name).up()
                .add("usage").set(usage.total()).up()
                .append(
                    new XeLink(
                        "delete",
                        new Href("/delete").with("name", name)
                    ).toXembly()
                )
                .up()
        );
    }

}
