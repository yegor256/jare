/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.tk;

import io.jare.model.Base;
import io.jare.smarts.SafeUser;
import java.io.IOException;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.flash.RsFlash;
import org.takes.facets.forward.RsForward;
import org.takes.rq.form.RqFormBase;

/**
 * Add pipe.
 *
 * @since 1.0
 */
final class TkAdd implements Take {

    /**
     * Base.
     */
    private final transient Base base;

    /**
     * Ctor.
     * @param bse Base
     */
    TkAdd(final Base bse) {
        this.base = bse;
    }

    @Override
    public Response act(final Request req) throws IOException {
        final String name = new RqFormBase(req).param("name")
            .iterator().next().trim();
        try {
            new SafeUser(this.base.user(new RqUser(req).name())).add(name);
        } catch (final SafeUser.InvalidNameException ex) {
            throw TkAdd.forward(new RsFlash(ex));
        }
        return TkAdd.forward(
            new RsFlash(
                String.format(
                    "domain \"%s\" added", name
                )
            )
        );
    }

    /**
     * Make forward.
     * @param rsp Response
     * @return Forward
     */
    private static RsForward forward(final Response rsp) {
        return new RsForward(rsp, "/domains");
    }

}
