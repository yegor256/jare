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
