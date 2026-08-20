/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.smarts;

import io.jare.fake.FkUser;
import io.jare.model.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link User}.
 * @since 0.5
 */
final class SafeUserTest {

    /**
     * User.Safe can accept normal domain names.
     */
    @Test
    void acceptsValidDomains() {
        final User user = new SafeUser(new FkUser());
        final String[] domains = {
            "google.com",
            "www.google.com",
            "www-1.google.com",
            "google.ua",
            "www-8-9.google.ua",
        };
        try {
            for (final String domain : domains) {
                user.add(domain);
            }
        } catch (final IOException ex) {
            Assertions.fail(ex);
        }
    }

    /**
     * User.Safe can reject invalid domain names.
     * @throws IOException If some domain triggers a real I/O failure
     */
    @Test
    @SuppressWarnings("PMD.AvoidUsingHardCodedIP")
    void rejectsInvalidDomains() throws IOException {
        final User user = new SafeUser(new FkUser());
        final String[] domains = {
            "google-com",
            "google",
            "www-1 .google.com",
            "google.УА",
            "www-8=9.google.ua",
            "127.0.0.1",
        };
        final Collection<Boolean> rejected = new ArrayList<>(domains.length);
        for (final String domain : domains) {
            try {
                user.add(domain);
                rejected.add(false);
            } catch (final InvalidNameException ex) {
                rejected.add(ex.getLocalizedMessage().contains(domain));
            }
        }
        MatcherAssert.assertThat(rejected, Matchers.everyItem(Matchers.is(true)));
    }
}
