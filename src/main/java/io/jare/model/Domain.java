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
package io.jare.model;

import java.io.IOException;

/**
 * Domain.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public interface Domain {

    /**
     * Owner of it.
     * @return The owner's GitHub handle
     * @throws IOException If fails
     */
    String owner() throws IOException;

    /**
     * Name.
     * @return The name
     * @throws IOException If fails
     */
    String name() throws IOException;

    /**
     * Delete it.
     * @throws IOException If fails
     */
    void delete() throws IOException;

    /**
     * Usage.
     * @return Usage
     * @throws IOException If fails
     */
    Usage usage() throws IOException;

}
