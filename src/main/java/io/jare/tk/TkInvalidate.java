/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.tk;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudfront.AmazonCloudFront;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClientBuilder;
import com.amazonaws.services.cloudfront.model.CreateInvalidationRequest;
import com.amazonaws.services.cloudfront.model.CreateInvalidationResult;
import com.amazonaws.services.cloudfront.model.InvalidationBatch;
import com.amazonaws.services.cloudfront.model.Paths;
import io.jare.model.Base;
import io.jare.model.Domain;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.UUID;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.flash.RsFlash;
import org.takes.facets.forward.RsForward;
import org.takes.rq.RqHref;

/**
 * Invalidate an URL.
 *
 * @since 1.0
 */
final class TkInvalidate implements Take {

    /**
     * Base.
     */
    private final transient Base base;

    /**
     * AWS key.
     */
    private final transient String key;

    /**
     * AWS secret.
     */
    private final transient String secret;

    /**
     * Ctor.
     *
     * @param bse Base
     * @param akey AWS key
     * @param scrt AWS secret
     */
    TkInvalidate(final Base bse, final String akey, final String scrt) {
        this.base = bse;
        this.key = akey;
        this.secret = scrt;
    }

    @Override
    public Response act(final Request req) throws IOException {
        final Iterator<String> params = new RqHref.Base(req).href()
            .param("url").iterator();
        if (!params.hasNext()) {
            throw new RsForward(
                new RsFlash("url parameter is required")
            );
        }
        final String url = params.next();
        final URI uri;
        try {
            uri = new URI(url);
        } catch (final URISyntaxException ex) {
            final RsForward fwd = new RsForward(
                new RsFlash(
                    String.format("URL \"%s\" is not valid", url)
                )
            );
            fwd.initCause(ex);
            throw fwd;
        }
        final String host = uri.getHost();
        if (host == null || host.isEmpty()) {
            throw new RsForward(
                new RsFlash(
                    String.format("URL \"%s\" is not valid", url)
                )
            );
        }
        final Iterator<Domain> domains = this.base.domain(host).iterator();
        if (!domains.hasNext()) {
            throw new RsForward(
                new RsFlash(
                    String.format("domain \"%s\" doesn't exist", host)
                )
            );
        }
        if (!domains.next().owner().equals(new RqUser(req).name())) {
            throw new RsForward(
                new RsFlash(
                    String.format("domain \"%s\" is not yours", host)
                )
            );
        }
        final String path = String.format(
            "/?u=%s",
            URLEncoder.encode(
                url,
                StandardCharsets.UTF_8
            )
        );
        final AmazonCloudFront aws = AmazonCloudFrontClientBuilder
            .standard().withCredentials(
                new AWSStaticCredentialsProvider(
                    new BasicAWSCredentials(this.key, this.secret)
                )
            )
            .build();
        final CreateInvalidationResult result = aws.createInvalidation(
            new CreateInvalidationRequest(
                "E2QC66VZY6F0QA",
                new InvalidationBatch(
                    new Paths().withItems(path).withQuantity(1),
                    UUID.randomUUID().toString()
                )
            )
        );
        return new RsForward(
            new RsFlash(
                String.format(
                    "URL \"%s\" was invalidated (ID=\"%s\", Status=\"%s\")",
                    url,
                    result.getInvalidation().getId(),
                    result.getInvalidation().getStatus()
                )
            ),
            "/domains"
        );
    }
}
