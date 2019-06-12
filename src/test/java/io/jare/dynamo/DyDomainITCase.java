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
package io.jare.dynamo;

import io.jare.model.Base;
import io.jare.model.Domain;
import io.jare.model.User;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Integration case for {@link DyDomain}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class DyDomainITCase {

    /**
     * DyDomain can be added and removed.
     * @throws Exception If some problem inside
     */
    @Test
    public void addsAndRemoveDomains() throws Exception {
        final Base base = new DyBase(new Dynamo());
        final String john = "john";
        final User user = base.user(john);
        final String name = "google.com";
        user.add(name);
        final Domain domain = base.domain(name).iterator().next();
        MatcherAssert.assertThat(domain.name(), Matchers.equalTo(name));
        MatcherAssert.assertThat(domain.owner(), Matchers.equalTo(john));
        domain.delete();
        MatcherAssert.assertThat(
            base.domain(name).iterator().hasNext(),
            Matchers.equalTo(false)
        );
    }

}
