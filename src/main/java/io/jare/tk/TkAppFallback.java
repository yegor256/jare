/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017 Yegor Bugayenko
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

import com.jcabi.manifests.Manifests;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.fallback.FbChain;
import org.takes.facets.fallback.FbStatus;
import org.takes.facets.fallback.RqFallback;
import org.takes.facets.fallback.TkFallback;
import org.takes.misc.Opt;
import org.takes.rs.RsText;
import org.takes.rs.RsVelocity;
import org.takes.rs.RsWithStatus;
import org.takes.rs.RsWithType;
import org.takes.tk.TkWrap;

/**
 * App with fallback.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class TkAppFallback extends TkWrap {

    /**
     * Revision.
     */
    private static final String REV = Manifests.read("Jare-Revision");

    /**
     * Ctor.
     * @param take Take
     */
    TkAppFallback(final Take take) {
        super(TkAppFallback.make(take));
    }

    /**
     * Authenticated.
     * @param take Takes
     * @return Authenticated takes
     */
    private static Take make(final Take take) {
        return new TkFallback(
            take,
            new FbChain(
                new FbStatus(
                    HttpURLConnection.HTTP_NOT_FOUND,
                    new RsWithStatus(
                        new RsText("page not found"),
                        HttpURLConnection.HTTP_NOT_FOUND
                    )
                ),
                new FbStatus(
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    new RsWithStatus(
                        new RsText("bad request"),
                        HttpURLConnection.HTTP_BAD_REQUEST
                    )
                ),
                req -> new Opt.Single<>(TkAppFallback.fatal(req))
            )
        );
    }

    /**
     * Make fatal error page.
     * @param req Request
     * @return Response
     * @throws IOException If fails
     */
    private static Response fatal(final RqFallback req) throws IOException {
        return new RsWithStatus(
            new RsWithType(
                new RsVelocity(
                    TkAppFallback.class.getResource("error.html.vm"),
                    new RsVelocity.Pair(
                        "err",
                        ExceptionUtils.getStackTrace(req.throwable())
                    ),
                    new RsVelocity.Pair("rev", TkAppFallback.REV)
                ),
                "text/html"
            ),
            HttpURLConnection.HTTP_INTERNAL_ERROR
        );
    }

}
