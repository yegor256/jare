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

import com.jcabi.aspects.Tv;
import java.net.HttpURLConnection;
import java.net.URI;
import org.takes.HttpException;

/**
 * Destination for relay.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.4
 */
final class Destination {

    /**
     * Destination URI.
     */
    private final transient URI uri;

    /**
     * Ctor.
     * @param dst Destination URI (full)
     */
    Destination(final URI dst) {
        this.uri = dst;
    }

    /**
     * Build destination path.
     * @return Destination path
     * @throws HttpException If fails
     */
    public String path() throws HttpException {
        if (!this.uri.isAbsolute()) {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                String.format("URI \"%s\" is not absolute", this.uri)
            );
        }
        final String protocol = this.uri.getScheme();
        if (!"https".equals(protocol) && !"http".equals(protocol)) {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                String.format(
                    "Protocol must be either HTTP or HTTPS at \"%s\"",
                    this.uri
                )
            );
        }
        if (this.uri.getHost() == null) {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                String.format("URI \"%s\" doesn't have a host", this.uri)
            );
        }
        final StringBuilder path = new StringBuilder(Tv.HUNDRED);
        if (this.uri.getRawPath().isEmpty()) {
            path.append('/');
        } else {
            path.append(this.uri.getRawPath());
        }
        if (this.uri.getRawQuery() != null) {
            path.append('?').append(this.uri.getRawQuery());
        }
        if (this.uri.getRawFragment() != null) {
            path.append('#').append(this.uri.getRawFragment());
        }
        return path.toString();
    }

}
