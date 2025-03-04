/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
