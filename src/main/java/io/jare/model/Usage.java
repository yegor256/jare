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
package io.jare.model;

import java.io.IOException;
import java.util.Date;
import java.util.SortedMap;

/**
 * Usage.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.7
 */
public interface Usage {

    /**
     * Add more usage in bytes.
     * @param date When did it happen
     * @param bytes How many bytes
     * @throws IOException If fails
     */
    void add(Date date, long bytes) throws IOException;

    /**
     * Total, over the last ten days.
     * @return The total in bytes
     * @throws IOException If fails
     */
    long total() throws IOException;

    /**
     * History.
     * @return Full usage history
     * @throws IOException If fails
     */
    SortedMap<Date, Long> history() throws IOException;

}
