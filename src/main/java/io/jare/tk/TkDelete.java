/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Yegor Bugayenko
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
