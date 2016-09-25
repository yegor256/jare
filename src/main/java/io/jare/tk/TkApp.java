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
package io.jare.tk;

import com.jcabi.log.VerboseProcess;
import com.jcabi.manifests.Manifests;
import io.jare.model.Base;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.takes.Take;
import org.takes.facets.auth.TkSecure;
import org.takes.facets.fallback.TkFallback;
import org.takes.facets.flash.TkFlash;
import org.takes.facets.fork.FkAuthenticated;
import org.takes.facets.fork.FkFixed;
import org.takes.facets.fork.FkHitRefresh;
import org.takes.facets.fork.FkHost;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.facets.forward.TkForward;
import org.takes.misc.Opt;
import org.takes.rs.RsWithBody;
import org.takes.rs.RsWithStatus;
import org.takes.rs.RsWithType;
import org.takes.tk.TkClasspath;
import org.takes.tk.TkFiles;
import org.takes.tk.TkGzip;
import org.takes.tk.TkMeasured;
import org.takes.tk.TkVersioned;
import org.takes.tk.TkWithHeaders;
import org.takes.tk.TkWithType;
import org.takes.tk.TkWrap;

/**
 * App.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 * @checkstyle ClassFanOutComplexityCheck (500 lines)
 */
@SuppressWarnings("PMD.ExcessiveImports")
public final class TkApp extends TkWrap {

    /**
     * Revision of app.
     */
    private static final String REV = Manifests.read("Jare-Revision");

    /**
     * Ctor.
     * @param base Base
     * @throws IOException If fails
     */
    public TkApp(final Base base) throws IOException {
        super(TkApp.make(base));
    }

    /**
     * Ctor.
     * @param base Base
     * @return Takes
     * @throws IOException If fails
     */
    private static Take make(final Base base) throws IOException {
        if (!"UTF-8".equals(Charset.defaultCharset().name())) {
            throw new IllegalStateException(
                String.format(
                    "default encoding is %s", Charset.defaultCharset()
                )
            );
        }
        return new TkWithHeaders(
            new TkVersioned(
                new TkMeasured(
                    new TkGzip(
                        new TkFlash(
                            new TkAppFallback(
                                new TkAppAuth(
                                    new TkForward(
                                        TkApp.regex(base)
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            String.format("X-Jare-Revision: %s", TkApp.REV),
            "Vary: Cookie"
        );
    }

    /**
     * Regex takes.
     * @param base Base
     * @return Takes
     * @throws IOException If fails
     */
    private static Take regex(final Base base) throws IOException {
        return new TkFork(
            new FkHost(
                "relay.jare.io",
                new TkFallback(
                    new TkRelay(base),
                    req -> new Opt.Single<>(
                        new RsWithType(
                            new RsWithBody(
                                new RsWithStatus(req.code()),
                                String.format(
                                    // @checkstyle LineLength (1 line)
                                    "Please, submit this stacktrace to GitHub and we'll try to help: https://github.com/yegor256/jare/issues\n\n%s",
                                    ExceptionUtils.getStackTrace(
                                        req.throwable()
                                    )
                                )
                            ),
                            "text/plain"
                        )
                    )
                )
            ),
            new FkRegex("/robots.txt", ""),
            new FkRegex(
                "/xsl/[a-z\\-]+\\.xsl",
                new TkWithType(
                    TkApp.refresh("./src/main/xsl"),
                    "text/xsl"
                )
            ),
            new FkRegex(
                "/css/[a-z]+\\.css",
                new TkWithType(
                    TkApp.refresh("./src/main/scss"),
                    "text/css"
                )
            ),
            new FkRegex(
                "/images/[a-z]+\\.svg",
                new TkWithType(
                    TkApp.refresh("./src/main/resources"),
                    "image/svg+xml"
                )
            ),
            new FkRegex(
                "/images/[a-z]+\\.png",
                new TkWithType(
                    TkApp.refresh("./src/main/resources"),
                    "image/png"
                )
            ),
            new FkRegex("/", new TkIndex(base)),
            new FkRegex(
                "/invalidate",
                new TkInvalidate(
                    Manifests.read("Jare-CloudFrontKey"),
                    Manifests.read("Jare-CloudFrontSecret")
                )
            ),
            new FkAuthenticated(
                new TkSecure(
                    new TkFork(
                        new FkRegex("/domains", new TkDomains(base)),
                        new FkRegex("/add", new TkAdd(base)),
                        new FkRegex("/delete", new TkDelete(base))
                    )
                )
            )
        );
    }

    /**
     * Hit refresh fork.
     * @param path Path of files
     * @return Fork
     * @throws IOException If fails
     */
    private static Take refresh(final String path) throws IOException {
        return new TkFork(
            new FkHitRefresh(
                new File(path),
                () -> {
                    new VerboseProcess(
                        new ProcessBuilder(
                            "mvn",
                            "generate-resources"
                        )
                    ).stdout();
                },
                new TkFiles("./target/classes")
            ),
            new FkFixed(new TkClasspath())
        );
    }

}
