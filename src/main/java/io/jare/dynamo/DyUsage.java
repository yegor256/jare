/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package io.jare.dynamo;

import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.jcabi.dynamo.Item;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import io.jare.model.Usage;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.time.DateUtils;
import org.w3c.dom.Node;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Dynamo usage.
 *
 * @since 0.7
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@ToString
@EqualsAndHashCode(of = "item")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class DyUsage implements Usage {

    /**
     * The item.
     */
    private final transient Item item;

    /**
     * Ctor.
     * @param itm Item
     */
    public DyUsage(final Item itm) {
        this.item = itm;
    }

    @Override
    public void add(final Date date, final long bytes) throws IOException {
        final int day = DyUsage.asNumber(date);
        final XML xml = this.xml();
        final String xpath = String.format("/usage/day[@id='%d']/text()", day);
        final List<String> items = xml.xpath(xpath);
        final long before;
        if (items.isEmpty()) {
            before = 0L;
        } else {
            before = Long.parseLong(items.get(0));
        }
        final Node node = xml.node();
        new Xembler(
            new Directives()
                .xpath(xpath).up().remove()
                .xpath("/usage").add("day").attr("id", day).set(before + bytes)
        ).applyQuietly(node);
        new Xembler(
            new Directives().xpath(
                String.format(
                    "/usage/day[number(@id) < %d]",
                    DyUsage.asNumber(DateUtils.addDays(new Date(), -30))
                )
            ).remove()
        ).applyQuietly(node);
        final XML after = new XMLDocument(node);
        this.save(after.toString());
        this.save(Long.parseLong(after.xpath("sum(/usage/day)").get(0)));
    }

    @Override
    public long total() throws IOException {
        if (!this.item.has("total")) {
            this.save(0L);
        }
        return Long.parseLong(this.item.get("total").getN());
    }

    @Override
    public SortedMap<Date, Long> history() throws IOException {
        final SortedMap<Date, Long> map = new TreeMap<>();
        for (final XML day : this.xml().nodes("/usage/day")) {
            map.put(
                DyUsage.asDate(Integer.parseInt(day.xpath("@id").get(0))),
                Long.parseLong(day.xpath("text()").get(0))
            );
        }
        return map;
    }

    /**
     * Load XML.
     * @return The XML with usage
     * @throws IOException If fails
     */
    private XML xml() throws IOException {
        if (!this.item.has("usage")) {
            this.save("<usage/>");
        }
        return new XMLDocument(this.item.get("usage").getS());
    }

    /**
     * Save XML.
     * @param xml The XML to save
     * @throws IOException If fails
     */
    private void save(final String xml) throws IOException {
        this.item.put(
            "usage",
            new AttributeValueUpdate()
                .withValue(new AttributeValue().withS(xml))
                .withAction(AttributeAction.PUT)
        );
    }

    /**
     * Save total.
     * @param total Total usage
     * @throws IOException If fails
     */
    private void save(final long total) throws IOException {
        this.item.put(
            "total",
            new AttributeValueUpdate()
                .withValue(new AttributeValue().withN(Long.toString(total)))
                .withAction(AttributeAction.PUT)
        );
    }

    /**
     * Convert date to number.
     * @param date The date
     * @return A number
     */
    private static int asNumber(final Date date) {
        final TimeZone zone = TimeZone.getTimeZone("UTC");
        final DateFormat fmt = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
        fmt.setTimeZone(zone);
        return Integer.parseInt(fmt.format(date));
    }

    /**
     * Convert number to date.
     * @param num The number
     * @return A date
     */
    private static Date asDate(final int num) {
        final TimeZone zone = TimeZone.getTimeZone("UTC");
        final DateFormat fmt = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
        fmt.setTimeZone(zone);
        try {
            return fmt.parse(Integer.toString(num));
        } catch (final ParseException ex) {
            throw new IllegalStateException(ex);
        }
    }

}
