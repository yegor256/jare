/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
