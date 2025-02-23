/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.tk;

import com.jcabi.manifests.Manifests;
import io.jare.model.Base;
import java.io.IOException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.takes.facets.auth.TkSecure;
import org.takes.facets.fallback.TkFallback;
import org.takes.facets.flash.TkFlash;
import org.takes.facets.fork.FkAuthenticated;
import org.takes.facets.fork.FkFixed;
import org.takes.facets.fork.FkHost;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.facets.fork.TkMethods;
import org.takes.facets.forward.TkForward;
import org.takes.misc.Opt;
import org.takes.rs.RsWithBody;
import org.takes.rs.RsWithStatus;
import org.takes.rs.RsWithType;
import org.takes.tk.TkGzip;
import org.takes.tk.TkMeasured;
import org.takes.tk.TkVersioned;
import org.takes.tk.TkWithHeaders;
import org.takes.tk.TkWithType;
import org.takes.tk.TkWrap;

/**
 * App.
 *
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 * @checkstyle ClassFanOutComplexityCheck (500 lines)
 * @checkstyle LineLength (500 lines)
 */
@SuppressWarnings(
    {
        "PMD.ExcessiveImports", "PMD.ExcessiveMethodLength",
        "PMD.AvoidDuplicateLiterals"
    }
)
public final class TkApp extends TkWrap {

    /**
     * Ctor.
     * @param base Base
     * @throws IOException If fails
     */
    public TkApp(final Base base) throws IOException {
        super(
            new TkWithHeaders(
                new TkVersioned(
                    new TkMeasured(
                        new TkFlash(
                            new TkAppFallback(
                                new TkAppAuth(
                                    new TkForward(
                                        new TkFork(
                                            new FkHost(
                                                "relay.jare.io",
                                                new TkFallback(
                                                    new TkRelay(base),
                                                    req -> new Opt.Single<>(
                                                        new RsWithType(
                                                            new RsWithBody(
                                                                new RsWithStatus(req.code()),
                                                                String.format(
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
                                            new FkFixed(
                                                new TkGzip(
                                                    new TkFork(
                                                        new FkRegex("/robots.txt", ""),
                                                        new FkRegex(
                                                            "/xsl/[a-z\\-]+\\.xsl",
                                                            new TkWithType(
                                                                new TkRefresh("./src/main/xsl"),
                                                                "text/xsl"
                                                            )
                                                        ),
                                                        new FkRegex(
                                                            "/css/[a-z]+\\.css",
                                                            new TkWithType(
                                                                new TkRefresh("./src/main/scss"),
                                                                "text/css"
                                                            )
                                                        ),
                                                        new FkRegex(
                                                            "/images/[a-z]+\\.svg",
                                                            new TkWithType(
                                                                new TkRefresh("./src/main/resources"),
                                                                "image/svg+xml"
                                                            )
                                                        ),
                                                        new FkRegex(
                                                            "/images/[a-z]+\\.png",
                                                            new TkWithType(
                                                                new TkRefresh("./src/main/resources"),
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
                                                                    new FkRegex(
                                                                        "/add",
                                                                        new TkMethods(new TkAdd(base), "POST")
                                                                    ),
                                                                    new FkRegex("/delete", new TkDelete(base))
                                                                )
                                                            )
                                                        )
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                ),
                String.format(
                    "X-Jare-Revision: %s",
                    Manifests.read("Jare-Revision")
                ),
                "Vary: Cookie"
            )
        );
    }

}
