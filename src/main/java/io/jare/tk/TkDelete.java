/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.tk;

import io.jare.model.Base;
import io.jare.model.Domain;
import java.io.IOException;
import java.util.Iterator;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.flash.RsFlash;
import org.takes.facets.forward.RsForward;
import org.takes.rq.RqHref;

/**
 * Delete domain.
 *
 * @since 1.0
 */
final class TkDelete implements Take {

    /**
     * Base.
     */
    private final transient Base base;

    /**
     * Ctor.
     * @param bse Base
     */
    TkDelete(final Base bse) {
        this.base = bse;
    }

    @Override
    public Response act(final Request req) throws IOException {
        final String name = new RqHref.Base(req).href()
            .param("name").iterator().next();
        final Iterator<Domain> domains = this.base.domain(name).iterator();
        if (!domains.hasNext()) {
            throw new RsForward(
                new RsFlash(
                    String.format("domain \"%s\" doesn't exist", name)
                )
            );
        }
        final Domain domain = domains.next();
        final String user = new RqUser(req).name();
        if (!domain.owner().equals(user)) {
            throw new RsForward(
                new RsFlash(
                    String.format("domain \"%s\" is not yours", name)
                )
            );
        }
        domain.delete();
        return new RsForward(
            new RsFlash(
                String.format(
                    "domain \"%s\" deleted", name
                )
            ),
            "/domains"
        );
    }

}
