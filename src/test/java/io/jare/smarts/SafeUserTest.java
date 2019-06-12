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
package io.jare.smarts;

import io.jare.fake.FkUser;
import io.jare.model.User;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link User}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.5
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class SafeUserTest {

    /**
     * User.Safe can accept normal domain names.
     * @throws Exception If some problem inside
     */
    @Test
    public void acceptsValidDomains() throws Exception {
        final User user = new SafeUser(new FkUser());
        final String[] domains = {
            "google.com",
            "www.google.com",
            "www-1.google.com",
            "google.ua",
            "www-8-9.google.ua",
        };
        for (final String domain : domains) {
            user.add(domain);
        }
    }

    /**
     * User.Safe can reject invalid domain names.
     * @throws Exception If some problem inside
     */
    @Test
    @SuppressWarnings("PMD.AvoidUsingHardCodedIP")
    public void rejectsInvalidDomains() throws Exception {
        final User user = new SafeUser(new FkUser());
        final String[] domains = {
            "google-com",
            "google",
            "www-1 .google.com",
            "google.УА",
            "www-8=9.google.ua",
            "127.0.0.1",
        };
        for (final String domain : domains) {
            try {
                user.add(domain);
                Assert.fail(String.format("exception expected: %s", domain));
            } catch (final SafeUser.InvalidNameException ex) {
                MatcherAssert.assertThat(
                    ex.getLocalizedMessage(),
                    Matchers.containsString(domain)
                );
            }
        }
    }

}
