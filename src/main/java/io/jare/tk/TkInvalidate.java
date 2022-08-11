/**
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
package io.jare.tk;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudfront.AmazonCloudFront;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClientBuilder;
import com.amazonaws.services.cloudfront.model.CreateInvalidationRequest;
import com.amazonaws.services.cloudfront.model.CreateInvalidationResult;
import com.amazonaws.services.cloudfront.model.InvalidationBatch;
import com.amazonaws.services.cloudfront.model.Paths;
import java.io.IOException;
import java.net.URLEncoder;
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
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class TkInvalidate implements Take {

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
     * @param akey AWS key
     * @param scrt AWS secret
     */
    TkInvalidate(final String akey, final String scrt) {
        this.key = akey;
        this.secret = scrt;
    }

    @Override
    public Response act(final Request req) throws IOException {
        final String url = new RqHref.Base(req).href()
            .param("url").iterator().next();
        final String path = String.format(
            "/?u=%s",
            URLEncoder.encode(
                url,
                "UTF-8"
            )
        );
        final AmazonCloudFront aws = AmazonCloudFrontClientBuilder.standard()
            .withCredentials(
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
