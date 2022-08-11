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
package io.jare.dynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.jcabi.dynamo.Credentials;
import com.jcabi.dynamo.Region;
import com.jcabi.dynamo.Table;
import com.jcabi.dynamo.retry.ReRegion;
import com.jcabi.log.Logger;
import com.jcabi.manifests.Manifests;

/**
 * Dynamo.
 *
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class Dynamo implements Region {

    /**
     * Region.
     */
    private final transient Region region = Dynamo.connect();

    @Override
    public AmazonDynamoDB aws() {
        return this.region.aws();
    }

    @Override
    public Table table(final String name) {
        return this.region.table(name);
    }

    /**
     * Connect.
     * @return Region
     */
    private static Region connect() {
        final String key = Manifests.read("Jare-DynamoKey");
        final Credentials.Simple creds = new Credentials.Simple(
            key, Manifests.read("Jare-DynamoSecret")
        );
        final Region region;
        if (key.startsWith("AAAAA")) {
            final int port = Integer.parseInt(
                System.getProperty("dynamo.port")
            );
            region = new Region.Simple(new Credentials.Direct(creds, port));
            Logger.warn(Dynamo.class, "test DynamoDB at port #%d", port);
        } else {
            region = new Region.Prefixed(
                new ReRegion(new Region.Simple(creds)),
                "jare-"
            );
        }
        Logger.info(Dynamo.class, "DynamoDB connected as %s", key);
        return region;
    }

}
