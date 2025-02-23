/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare;

import com.jcabi.manifests.Manifests;
import com.jcabi.s3.Region;
import io.jare.cached.CdBase;
import io.jare.dynamo.DyBase;
import io.jare.model.Base;
import io.jare.tk.TkApp;
import io.sentry.Sentry;
import java.io.IOException;
import org.takes.http.Exit;
import org.takes.http.FtCli;

/**
 * Command line entry.
 *
 * @since 1.0
 */
public final class Entrance {

    /**
     * Ctor.
     */
    private Entrance() {
        // utility class
    }

    /**
     * Main entry point.
     * @param args Arguments
     * @throws IOException If fails
     */
    public static void main(final String... args) throws IOException {
        Sentry.init(Manifests.read("Jare-SentryDsn"));
        final Base base = new CdBase(new DyBase());
        new Logs(
            base,
            new Region.Simple(
                Manifests.read("Jare-S3Key"),
                Manifests.read("Jare-S3Secret")
            ).bucket("logs.jare.io")
        );
        new FtCli(new TkApp(base), args).start(Exit.NEVER);
    }

}
