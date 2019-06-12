/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2019 Yegor Bugayenko
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

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.takes.Response;
import org.takes.rq.RqFake;

/**
 * Integration case for {@link TkInvalidate}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class TkInvalidateITCase {

    /**
     * TkInvalidate can invalidate URL.
     * @throws Exception If some problem inside
     */
    @Test
    @Ignore
    public void invalidatesUrl() throws Exception {
        final String url =
            "http://www.yegor256.com/images/yegor-bugayenko-192x192.png";
        final Response rsp = new TkInvalidate(
            "-key-", "-secret-"
        ).act(new RqFake("GET", String.format("/invalidate?url=%s", url)));
        MatcherAssert.assertThat(
            rsp.toString(),
            Matchers.containsString("InProgress")
        );
    }

}
