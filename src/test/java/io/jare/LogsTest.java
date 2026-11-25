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
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link Logs}.
 *
 * @since 1.0
 */
final class LogsTest {

    /**
     * Record with a foreign referer and the target in cs-uri-query.
     */
    private static final String RECORD = String.join(
        "\t",
        "2026-11-24",
        "10:38:41",
        "ABC01",
        "1024",
        "192.0.2.1",
        "GET",
        "djk1be5eatcae.cloudfront.net",
        "/",
        "200",
        "https://example.com/page",
        "Mozilla/5.0",
        "u=http://images.example.net/image.png",
        "-",
        "Hit",
        "request-id",
        "cf.jare.io",
        "http",
        "100",
        "0.001",
        "-",
        "-",
        "-",
        "Hit",
        "HTTP/1.1"
    );

    /**
     * Date of {@link LogsTest#RECORD}.
     */
    private static final LocalDate DATE = LocalDate.of(2026, 11, 24);

    /**
     * Bytes in {@link LogsTest#RECORD}.
     */
    private static final long BYTES = 1024L;

    /**
     * Logs can unzip and log.
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
     * Logs can credit the target from the query parameter, not the referer.
     *
     * @throws Exception If some problem inside
     */
    @Test
    void creditsTargetFromQueryParamNotReferer() throws Exception {
        final Usage usage = Mockito.mock(Usage.class);
        final Domain domain = Mockito.mock(Domain.class);
        Mockito.doReturn(usage).when(domain).usage();
        final Base base = Mockito.mock(Base.class);
        Mockito.doReturn(Collections.singleton(domain))
            .when(base).domain(Mockito.anyString());
        LogsTest.runs(base, LogsTest.RECORD);
        Mockito.verify(base).domain("images.example.net");
        Mockito.verify(base, Mockito.never()).domain("example.com");
        Mockito.verify(usage).add(LogsTest.DATE, LogsTest.BYTES);
    }

    /**
     * Logs can credit the target even when the referer is missing.
     *
     * @throws Exception If some problem inside
     */
    @Test
    void toleratesMissingReferer() throws Exception {
        final Usage usage = Mockito.mock(Usage.class);
        final Domain domain = Mockito.mock(Domain.class);
        Mockito.doReturn(usage).when(domain).usage();
        final Base base = Mockito.mock(Base.class);
        Mockito.doReturn(Collections.singleton(domain))
            .when(base).domain(Mockito.anyString());
        LogsTest.runs(
            base,
            LogsTest.RECORD.replace("https://example.com/page", "-")
        );
        Mockito.verify(base).domain("images.example.net");
        Mockito.verify(usage).add(LogsTest.DATE, LogsTest.BYTES);
    }

    /**
     * Logs can skip a record without a target in the query parameter.
     *
     * @throws Exception If some problem inside
     */
    @Test
    void skipsRecordWithoutTargetParam() throws Exception {
        final Usage usage = Mockito.mock(Usage.class);
        final Domain domain = Mockito.mock(Domain.class);
        Mockito.doReturn(usage).when(domain).usage();
        final Base base = Mockito.mock(Base.class);
        Mockito.doReturn(Collections.singleton(domain))
            .when(base).domain(Mockito.anyString());
        LogsTest.runs(
            base,
            LogsTest.RECORD.replace(
                "u=http://images.example.net/image.png",
                "-"
            )
        );
        Mockito.verify(base, Mockito.never())
            .domain(Mockito.anyString());
        Mockito.verifyNoInteractions(usage);
    }

    /**
     * Logs can skip a record with an invalid target in the query parameter.
     *
     * @throws Exception If some problem inside
     */
    @Test
    void skipsRecordWithInvalidTarget() throws Exception {
        final Usage usage = Mockito.mock(Usage.class);
        final Domain domain = Mockito.mock(Domain.class);
        Mockito.doReturn(usage).when(domain).usage();
        final Base base = Mockito.mock(Base.class);
        Mockito.doReturn(Collections.singleton(domain))
            .when(base).domain(Mockito.anyString());
        LogsTest.runs(
            base,
            LogsTest.RECORD.replace(
                "u=http://images.example.net/image.png",
                "u=http%3A%2F%2Fimages.example.net%2Fx%20y.png"
            )
        );
        Mockito.verify(base, Mockito.never())
            .domain(Mockito.anyString());
        Mockito.verifyNoInteractions(usage);
    }

    private static void runs(final Base base, final String line)
        throws Exception {
        final Ocket ocket = Mockito.mock(Ocket.class);
        Mockito.doAnswer(
            inv -> {
                final GZIPOutputStream gzip = new GZIPOutputStream(
                    OutputStream.class.cast(inv.getArgument(0))
                );
                IOUtils.copyLarge(
                    new ByteArrayInputStream(
                        line.getBytes(StandardCharsets.UTF_8)
                    ),
                    gzip
                );
                gzip.close();
                return null;
            }
        ).when(ocket).read(Mockito.any(OutputStream.class));
        final Bucket bucket = Mockito.mock(Bucket.class);
        Mockito.doReturn(ocket).when(bucket).ocket(Mockito.anyString());
        Mockito.doReturn(Collections.singleton("x")).when(bucket).list("");
        new Logs(base, bucket).run();
    }
}
