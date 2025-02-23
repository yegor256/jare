/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.tk;

import java.net.HttpURLConnection;
import java.net.URI;
import org.takes.HttpException;

/**
 * Destination for relay.
 *
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
        final StringBuilder path = new StringBuilder(100);
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
