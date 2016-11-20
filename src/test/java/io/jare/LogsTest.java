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
package io.jare;

import com.jcabi.s3.Bucket;
import com.jcabi.s3.Ocket;
import io.jare.fake.FkBase;
import java.io.OutputStream;
import java.util.Collections;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link Logs}.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class LogsTest {

    /**
     * Logs can unzip and log.
     * @throws Exception If some problem inside
     */
    @Test
    public void unzipsAndLogs() throws Exception {
        final Ocket ocket = Mockito.mock(Ocket.class);
        Mockito.doAnswer(
            inv -> {
                final GZIPOutputStream gzip = new GZIPOutputStream(
                    OutputStream.class.cast(inv.getArgument(0))
                );
                IOUtils.copyLarge(
                    Logs.class.getResourceAsStream("test"),
                    gzip
                );
                gzip.close();
                return null;
            }
        ).when(ocket).read(Mockito.any(OutputStream.class));
        final Bucket bucket = Mockito.mock(Bucket.class);
        Mockito.doReturn(ocket).when(bucket).ocket(Mockito.anyString());
        Mockito.doReturn(Collections.singleton("x")).when(bucket).list("");
        new Logs(new FkBase(), bucket).run();
    }

}
