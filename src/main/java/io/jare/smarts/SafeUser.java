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
package io.jare.smarts;

import io.jare.model.Domain;
import io.jare.model.User;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Safe user.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class SafeUser implements User {

    /**
     * Pattern to match.
     */
    private static final Pattern PTN = Pattern.compile(
        "(?=^.{4,253}$)(^((?!-)[a-zA-Z0-9-]{1,63}(?<!-)\\.)+[a-zA-Z]{2,63}$)"
    );

    /**
     * Original one.
     */
    private final transient User origin;

    /**
     * Ctor.
     * @param user Original user
     */
    public SafeUser(final User user) {
        this.origin = user;
    }

    @Override
    public Iterable<Domain> mine() {
        return this.origin.mine();
    }

    @Override
    public void add(final String name) throws IOException {
        if (!SafeUser.PTN.matcher(name).matches()) {
            throw new SafeUser.InvalidNameException(name);
        }
        this.origin.add(name);
    }

    /**
     * When name is not valid.
     */
    public static final class InvalidNameException extends IOException {
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
}
