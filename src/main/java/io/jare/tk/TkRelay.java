/**
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
import io.jare.model.Domain;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Pattern;
import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Skipped;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqHref;
import org.takes.rs.RsWithHeaders;
import org.takes.rs.RsWithoutHeader;
import org.takes.tk.TkProxy;

/**
 * Relay.
 *
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class TkRelay implements Take {

    /**
     * Validation pattern for destination URLs.
     * @link https://tools.ietf.org/html/rfc3986
     */
    private static final Pattern PTN = Pattern.compile(
        "[A-Za-z0-9-%._~:/\\?#@!\\$&'\\(\\)\\*\\+,;=`\\[\\]]+"
    );

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
    public Response act(final Request req) throws Exception {
        final Iterator<String> param = new RqHref.Base(req).href()
            .param("u").iterator();
        if (!param.hasNext()) {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                String.format(
                    "Parameter \"u\" is mandatory in %s",
                    new RqHref.Base(req).href()
                )
            );
        }
        final String target = param.next().trim();
        if (!TkRelay.PTN.matcher(target).matches()) {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                String.format(
                    "Target URL \"%s\" is not compliant with RFC3986",
                    target
                )
            );
        }
        final URI uri = URI.create(target);
        final String host = uri.getHost().toLowerCase(Locale.ENGLISH);
        final Iterator<Domain> domains = this.base.domain(host).iterator();
        if (!domains.hasNext()) {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                String.format(
                    // @checkstyle LineLength (1 line)
                    "Domain \"%s\" is not registered, check your account at www.jare.io",
                    host
                )
            );
        }
        final Domain domain = domains.next();
        return TkRelay.cached(
            new RsWithHeaders(
                new TkProxy(uri.toString()).act(
                    TkRelay.request(
                        req,
                        new Destination(uri).path()
                    )
                ),
                String.format("X-Jare-Target: %s", uri),
                String.format("X-Jare-Usage: %d", domain.usage().total())
            )
        );
    }

    /**
     * Response that is cached forever.
     * @param rsp Response
     * @return New response
     */
    private static Response cached(final Response rsp) {
        return new RsWithHeaders(
            new RsWithoutHeader(
                new RsWithoutHeader(
                    new RsWithoutHeader(rsp, "Age"),
                    "Expires"
                ),
                "Cache-Control"
            ),
            "Age: 31536000",
            "Cache-Control: max-age=31536000",
            "Expires: Sun, 19 Jul 2020 18:06:32 GMT"
        );
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
                return new Joined<String>(
                    Collections.singleton(
                        String.format(
                            "GET %s HTTP/1.1",
                            path
                        )
                    ),
                    new Skipped<>(1, req.head())
                );
            }
            @Override
            public InputStream body() throws IOException {
                return req.body();
            }
        };
    }
}
