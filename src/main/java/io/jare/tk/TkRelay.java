/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 jare.io
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

import com.google.common.collect.Iterables;
import io.jare.model.Base;
import io.jare.model.Domain;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqHref;
import org.takes.rs.RsWithHeader;
import org.takes.tk.TkProxy;

/**
 * Relay.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 1.0
 */
final class TkRelay implements Take {

    /**
     * Base.
     */
    private final transient Base base;

    /**
     * Ctor.
     * @param bse Base
     */
    TkRelay(final Base bse) {
        this.base = bse;
    }

    @Override
    public Response act(final Request req) throws IOException {
        final Iterator<String> param = new RqHref.Base(req).href()
            .param("u").iterator();
        if (!param.hasNext()) {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                "parameter \"u\" is mandatory"
            );
        }
        final URI uri = URI.create(param.next().trim());
        return new RsWithHeader(
            new TkProxy(uri.toString()).act(
                TkRelay.request(req, this.path(uri))
            ),
            String.format("X-Jare-Target: %s", uri)
        );
    }

    /**
     * Build destination path.
     * @param uri URI of destination
     * @return Destination path
     * @throws HttpException If fails
     */
    private String path(final URI uri) throws HttpException {
        if (!uri.isAbsolute()) {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                String.format("URI \"%s\" is not absolute", uri)
            );
        }
        final String protocol = uri.getScheme();
        if (!"https".equals(protocol) && !"http".equals(protocol)) {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                String.format(
                    "protocol must be either HTTP or HTTPS at \"%s\"",
                    uri
                )
            );
        }
        if (uri.getHost() == null) {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                String.format("URI \"%s\" doesn't have a host", uri)
            );
        }
        final String host = uri.getHost().toLowerCase(Locale.ENGLISH);
        final Iterator<Domain> domains = this.base.domain(host);
        if (!domains.hasNext()) {
            throw new HttpException(
                HttpURLConnection.HTTP_NOT_FOUND,
                String.format("domain \"%s\" is not registered", host)
            );
        }
        final String path;
        if (uri.getPath().isEmpty()) {
            path = "/";
        } else {
            path = uri.getPath();
        }
        return path;
    }

    /**
     * The request to send.
     * @param req Original request
     * @param path Destination path
     * @return Request
     */
    private static Request request(final Request req, final String path) {
        return new Request() {
            @Override
            public Iterable<String> head() throws IOException {
                return Iterables.concat(
                    Collections.singleton(
                        String.format(
                            "GET %s HTTP/1.1",
                            path
                        )
                    ),
                    Iterables.skip(req.head(), 1)
                );
            }
            @Override
            public InputStream body() throws IOException {
                return req.body();
            }
        };
    }
}
