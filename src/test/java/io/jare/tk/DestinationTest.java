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

import java.net.URI;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link Destination}.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.4
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class DestinationTest {

    /**
     * Destination can build a path.
     * @throws Exception If some problem inside
     */
    @Test
    public void buildsPath() throws Exception {
        MatcherAssert.assertThat(
            new Destination(
                new URI(
                    "http://www.google.com/a/b/%D0%B4%D0%B0?z=%D0%B0#%D0%B0"
                )
            ).path(),
            Matchers.equalTo("/a/b/%D0%B4%D0%B0?z=%D0%B0#%D0%B0")
        );
    }

    /**
     * Destination can build an empty path.
     * @throws Exception If some problem inside
     */
    @Test
    public void buildsEmptyPath() throws Exception {
        MatcherAssert.assertThat(
            new Destination(new URI("http://www.google.com")).path(),
            Matchers.equalTo("/")
        );
    }

    /**
     * Destination can build path with params.
     * @throws Exception If some problem inside
     */
    @Test
    public void buildsPathWithParams() throws Exception {
        MatcherAssert.assertThat(
            new Destination(new URI("http://www.google.com?a=1")).path(),
            Matchers.equalTo("/?a=1")
        );
    }

    /**
     * Destination can build path with fragment.
     * @throws Exception If some problem inside
     */
    @Test
    public void buildsPathWithFragment() throws Exception {
        MatcherAssert.assertThat(
            new Destination(new URI("http://www.google.com/a?x=1#t")).path(),
            Matchers.equalTo("/a?x=1#t")
        );
    }

}
