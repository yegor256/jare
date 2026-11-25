/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package io.jare;

import com.jcabi.s3.Bucket;
import com.jcabi.s3.Ocket;
import io.jare.fake.FkBase;
import io.jare.model.Base;
import io.jare.model.Domain;
import io.jare.model.Usage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

/**
 * Test case for {@link Logs}.
 *
 * @since 1.0
 */
final class LogsTest {

    /**
     * Test that log files can be unzipped and processed.
     *
     * @throws Exception If some problem inside
     */
    @Test
    void unzipsAndLogs() throws Exception {
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
        Mockito.verify(bucket).remove("x");
    }

    /**
     * IPv4 and IPv6 records are credited before the log is removed.
     *
     * @throws Exception If some problem inside
     */
    @Test
    void countsBothIpVersionsBeforeRemovingOcket() throws Exception {
        final Ocket ocket = Mockito.mock(Ocket.class);
        Mockito.doAnswer(
            inv -> {
                LogsTest.writeLogs(
                    OutputStream.class.cast(inv.getArgument(0))
                );
                return null;
            }
        ).when(ocket).read(Mockito.any(OutputStream.class));
        final Bucket bucket = Mockito.mock(Bucket.class);
        Mockito.doReturn(ocket).when(bucket).ocket(Mockito.anyString());
        Mockito.doReturn(Collections.singleton("x")).when(bucket).list("");
        final Base base = Mockito.mock(Base.class);
        final Domain domain = Mockito.mock(Domain.class);
        final Usage usage = Mockito.mock(Usage.class);
        Mockito.doReturn(Collections.singleton(domain))
            .when(base).domain("example.com");
        Mockito.doReturn(usage).when(domain).usage();
        new Logs(base, bucket).run();
        final InOrder order = Mockito.inOrder(usage, bucket);
        order.verify(usage).add(LocalDate.parse("2026-11-25"), 2000L);
        order.verify(bucket).remove("x");
    }

    private static void writeLogs(final OutputStream output) throws IOException {
        try (
            GZIPOutputStream gzip = new GZIPOutputStream(output)
        ) {
            gzip.write(
                String.join(
                    System.lineSeparator(),
                    String.join(
                        "\t",
                        "2026-11-25",
                        "10:38:41",
                        "LHR50",
                        "1000",
                        "192.0.2.1",
                        "GET",
                        "abc.cloudfront.net",
                        "/",
                        "200",
                        "https://example.com/path\t"
                    ),
                    String.join(
                        "\t",
                        "2026-11-25",
                        "10:38:41",
                        "LHR50",
                        "1000",
                        "2001:db8::1",
                        "GET",
                        "abc.cloudfront.net",
                        "/",
                        "200",
                        "https://example.com/path\t"
                    )
                ).getBytes(StandardCharsets.UTF_8)
            );
        }
    }
}
