/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.smarts;

import java.io.IOException;

/**
 * When name is not valid.
 * @since 0.1
 */
public final class InvalidNameException extends IOException {

    /**
     * Serialization marker.
     */
    private static final long serialVersionUID = -869776873934626730L;

    /**
     * Ctor.
     * @param name Domain name
     */
    public InvalidNameException(final String name) {
        super(
            String.format(
                "domain name \"%s\" is not valid",
                name
            )
        );
    }
}
