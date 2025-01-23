/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Yegor Bugayenko
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

import com.jcabi.aspects.ScheduleWithFixedDelay;
import com.jcabi.log.Logger;
import com.jcabi.s3.Bucket;
import com.jcabi.s3.Ocket;
import io.jare.model.Base;
import io.jare.model.Domain;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import org.apache.commons.lang3.StringUtils;

/**
 * Logs in S3.
 *
 * @since 0.7
 */
@ScheduleWithFixedDelay(delay = 1, unit = TimeUnit.MINUTES)
final class Logs implements Runnable {

    /**
     * Parser of one line.
     */
    private static final Pattern PTN = Pattern.compile(
        StringUtils.join(
            "(\\d{4}-\\d{2}-\\d{2})\t",
            "\\d{2}:\\d{2}:\\d{2}\t",
            "[A-Z0-9]+\t",
            "(\\d+)\t",
            "\\d+\\.\\d+\\.\\d+\\.\\d+\t",
            "GET\t",
            "[a-z0-9]+.cloudfront.net\t",
            "/\t",
            "\\d{3}\t",
            "(https?://[^\t]+)\t"
        )
    );

    /**
     * Base.
     */
    private final transient Base base;

    /**
     * Bucket.
     */
    private final transient Bucket bucket;

    /**
     * Ctor.
     * @param bse Base
     * @param bkt Bucket
     */
    Logs(final Base bse, final Bucket bkt) {
        this.base = bse;
        this.bucket = bkt;
    }

    @Override
    public void run() {
        try {
            final Iterator<String> ockets = this.bucket.list("").iterator();
            if (ockets.hasNext()) {
                final String name = ockets.next();
                final long bytes = this.process(name);
                this.bucket.remove(name);
                Logger.info(this, "ocket %s processed: %d bytes", name, bytes);
            }
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Process one ocket.
     * @param name The name of the ocket
     * @return Bytes in total
     * @throws IOException If fails
     */
    private long process(final String name) throws IOException {
        final Ocket ocket = this.bucket.ocket(name);
        final Path path = Files.createTempFile("jare", ".gz");
        ocket.read(Files.newOutputStream(path));
        final BufferedReader input = new BufferedReader(
            new InputStreamReader(
                new GZIPInputStream(
                    Files.newInputStream(path)
                )
            )
        );
        final Map<String, Map<Date, Long>> map = new HashMap<>(0);
        try {
            while (true) {
                final String line = input.readLine();
                if (line == null) {
                    break;
                }
                Logs.parse(map, line);
            }
        } finally {
            input.close();
        }
        long total = 0L;
        for (final Map.Entry<String, Map<Date, Long>> entry : map.entrySet()) {
            for (final Map.Entry<Date, Long> usg
                : entry.getValue().entrySet()) {
                final Iterator<Domain> domains =
                    this.base.domain(entry.getKey()).iterator();
                if (domains.hasNext()) {
                    domains.next().usage().add(
                        usg.getKey(),
                        usg.getValue()
                    );
                }
                total += usg.getValue();
            }
        }
        return total;
    }

    /**
     * Parse one line.
     * @param map Map to populate
     * @param line The line
     */
    private static void parse(final Map<String, Map<Date, Long>> map,
        final CharSequence line) {
        final Matcher matcher = Logs.PTN.matcher(line);
        if (matcher.find()) {
            final String domain = URI.create(matcher.group(3)).getHost();
            if (!map.containsKey(domain)) {
                map.put(domain, new HashMap<>(0));
            }
            final Map<Date, Long> target = map.get(domain);
            final Date date = Logs.asDate(matcher.group(1));
            if (!target.containsKey(date)) {
                target.put(date, 0L);
            }
            target.put(
                date,
                target.get(date) + Long.parseLong(matcher.group(2))
            );
        }
    }

    /**
     * Convert text to date.
     * @param txt Text
     * @return A date
     */
    private static Date asDate(final String txt) {
        final TimeZone zone = TimeZone.getTimeZone("UTC");
        final DateFormat fmt = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH
        );
        fmt.setTimeZone(zone);
        try {
            return fmt.parse(txt);
        } catch (final ParseException ex) {
            throw new IllegalStateException(ex);
        }
    }

}
