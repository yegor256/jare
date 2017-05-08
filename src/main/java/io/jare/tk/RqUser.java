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

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.takes.Request;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.RqAuth;
import org.takes.rq.RqWrap;

/**
 * User in request.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class RqUser extends RqWrap {

    /**
     * Pattern to fetch name.
     */
    private static final Pattern PTN = Pattern.compile(
        "urn:(?:github|test):(.*)"
    );

    /**
     * Ctor.
     * @param req Request
     */
    public RqUser(final Request req) {
        super(req);
    }

    /**
     * Get user name (GitHub handle).
     * @return Name
     * @throws IOException If fails
     */
    public String name() throws IOException {
        final Identity identity = new RqAuth(this).identity();
        final String urn = identity.urn();
        final Matcher mtr = RqUser.PTN.matcher(urn);
        if (!mtr.matches()) {
            throw new IllegalArgumentException(
                String.format("URN \"%s\" is not from GitHub", urn)
            );
        }
        return identity.properties().get("login");
    }

}
