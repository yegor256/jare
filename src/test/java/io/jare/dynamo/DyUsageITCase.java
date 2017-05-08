/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017 Yegor Bugayenko
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
import io.jare.model.Usage;
import io.jare.model.User;
import java.util.Date;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Integration case for {@link DyUsage}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.7
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class DyUsageITCase {

    /**
     * DyUsage can be record usage.
     * @throws Exception If some problem inside
     */
    @Test
    public void recordsUsage() throws Exception {
        final Base base = new DyBase(new Dynamo());
        final User user = base.user("Erik");
        final String name = "yegor256.com";
        user.add(name);
        final Domain domain = base.domain(name).iterator().next();
        final Usage usage = domain.usage();
        usage.add(new Date(), 1L);
        usage.add(new Date(), 1L);
        MatcherAssert.assertThat(usage.total(), Matchers.equalTo(2L));
        domain.delete();
    }

}
