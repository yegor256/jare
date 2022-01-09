/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2022 Yegor Bugayenko
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
package io.jare.fake;

import com.jcabi.log.Logger;
import io.jare.model.Usage;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Fake usage.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.7
 */
public final class FkUsage implements Usage {

    @Override
    public void add(final Date date, final long bytes) {
        Logger.info(this, "usage, date=%s, bytes=%d", date, bytes);
    }

    @Override
    public long total() {
        return 1L;
    }

    @Override
    public SortedMap<Date, Long> history() {
        return new TreeMap<>();
    }
}
