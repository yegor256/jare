/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare;

import com.jcabi.s3.Bucket;
import com.jcabi.s3.Ocket;
import io.jare.fake.FkBase;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link Logs}.
 *
 * @since 1.0
 */
final class LogsTest {

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
     * Logs can leave no downloaded temp file behind.
     *
     * @throws Exception If some problem inside
     */
    @Test
    void removesTempFileAfterProcessing() throws Exception {
        final Path tmp = Paths.get(System.getProperty("java.io.tmpdir"));
        final long before;
        try (Stream<Path> files = Files.list(tmp)) {
            before = files.filter(LogsTest::isTempFile).count();
        }
        final Ocket ocket = Mockito.mock(Ocket.class);
        Mockito.doAnswer(
            inv -> {
                new GZIPOutputStream(
                    OutputStream.class.cast(inv.getArgument(0))
                ).close();
                return null;
            }
        ).when(ocket).read(Mockito.any(OutputStream.class));
        final Bucket bucket = Mockito.mock(Bucket.class);
        Mockito.doReturn(ocket).when(bucket).ocket(Mockito.anyString());
        Mockito.doReturn(Collections.singleton("x")).when(bucket).list("");
        new Logs(new FkBase(), bucket).run();
        Mockito.verify(bucket).remove("x");
        final long after;
        try (Stream<Path> files = Files.list(tmp)) {
            after = files.filter(LogsTest::isTempFile).count();
        }
        MatcherAssert.assertThat(
            "no jare*.gz file must be left in the temp directory",
            after,
            Matchers.equalTo(before)
        );
    }

    private static boolean isTempFile(final Path file) {
        return file.getFileName().toString().matches("jare.*\\.gz");
    }
}
