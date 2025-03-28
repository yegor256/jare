/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.tk;

import com.jcabi.manifests.Manifests;
import java.io.IOException;
import java.util.Arrays;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.RqAuth;
import org.takes.facets.auth.XeIdentity;
import org.takes.facets.auth.XeLogoutLink;
import org.takes.facets.auth.social.XeGithubLink;
import org.takes.facets.flash.XeFlash;
import org.takes.facets.fork.FkTypes;
import org.takes.facets.fork.RsFork;
import org.takes.rs.RsPrettyXml;
import org.takes.rs.RsWithType;
import org.takes.rs.RsWrap;
import org.takes.rs.RsXslt;
import org.takes.rs.xe.RsXembly;
import org.takes.rs.xe.XeAppend;
import org.takes.rs.xe.XeChain;
import org.takes.rs.xe.XeDate;
import org.takes.rs.xe.XeLink;
import org.takes.rs.xe.XeLinkHome;
import org.takes.rs.xe.XeLinkSelf;
import org.takes.rs.xe.XeLocalhost;
import org.takes.rs.xe.XeMillis;
import org.takes.rs.xe.XeSla;
import org.takes.rs.xe.XeSource;
import org.takes.rs.xe.XeStylesheet;
import org.takes.rs.xe.XeWhen;

/**
 * Index resource, front page of the website.
 *
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("PMD.ExcessiveImports")
public final class RsPage extends RsWrap {

    /**
     * Ctor.
     * @param xsl XSL
     * @param req Request
     * @param src Source
     * @throws IOException If fails
     */
    public RsPage(final String xsl, final Request req, final XeSource... src)
        throws IOException {
        this(xsl, req, Arrays.asList(src));
    }

    /**
     * Ctor.
     * @param xsl XSL
     * @param req Request
     * @param src Source
     * @throws IOException If fails
     */
    public RsPage(final String xsl, final Request req,
        final Iterable<XeSource> src)
        throws IOException {
        super(RsPage.make(xsl, req, src));
    }

    /**
     * Make it.
     * @param xsl XSL
     * @param req Request
     * @param src Source
     * @return Response
     * @throws IOException If fails
     */
    private static Response make(final String xsl, final Request req,
        final Iterable<XeSource> src) throws IOException {
        final Response raw = new RsXembly(
            new XeStylesheet(xsl),
            new XeAppend(
                "page",
                new XeMillis(false),
                new XeChain(src),
                new XeLinkHome(req),
                new XeLinkSelf(req),
                new XeMillis(true),
                new XeDate(),
                new XeSla(),
                new XeLocalhost(),
                new XeFlash(req),
                new XeWhen(
                    new RqAuth(req).identity().equals(Identity.ANONYMOUS),
                    new XeChain(
                        new XeGithubLink(req, Manifests.read("Jare-GithubId"))
                    )
                ),
                new XeWhen(
                    !new RqAuth(req).identity().equals(Identity.ANONYMOUS),
                    new XeChain(
                        new XeIdentity(req),
                        new XeLogoutLink(req),
                        new XeLink("domains", "/domains")
                    )
                ),
                new XeAppend(
                    "version",
                    new XeAppend("name", Manifests.read("Jare-Version")),
                    new XeAppend("revision", Manifests.read("Jare-Revision")),
                    new XeAppend("date", Manifests.read("Jare-Date")),
                    new XeAppend("heroku", RsPage.heroku())
                )
            )
        );
        return new RsFork(
            req,
            new FkTypes(
                "application/xml,text/xml",
                new RsPrettyXml(new RsWithType(raw, "text/xml"))
            ),
            new FkTypes(
                "*/*",
                new RsXslt(new RsWithType(raw, "text/html"))
            )
        );
    }

    /**
     * Current Heroku release version.
     * @return The version
     */
    private static String heroku() {
        String ver = System.getenv("HEROKU_RELEASE_VERSION");
        if (ver == null) {
            ver = "?";
        }
        return ver;
    }

}
