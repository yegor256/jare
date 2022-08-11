/*
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
