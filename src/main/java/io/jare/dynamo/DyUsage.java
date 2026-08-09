/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Yegor Bugayenko
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
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.w3c.dom.Node;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Dynamo usage.
 * @since 0.7
 */
@ToString
@EqualsAndHashCode(of = "item")
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
    public void add(final LocalDate date, final long bytes) throws IOException {
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
        final Node node = xml.deepCopy();
        new Xembler(
            new Directives()
                .xpath(xpath).up().remove()
                .xpath("/usage").add("day").attr("id", day).set(before + bytes)
        ).applyQuietly(node);
        new Xembler(
            new Directives().xpath(
                String.format(
                    "/usage/day[number(@id) < %d]",
                    DyUsage.asNumber(LocalDate.now(ZoneOffset.UTC).minusDays(30))
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
    public SortedMap<LocalDate, Long> history() throws IOException {
        final SortedMap<LocalDate, Long> map = new TreeMap<>();
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
    private static int asNumber(final LocalDate date) {
        return Integer.parseInt(date.format(DateTimeFormatter.BASIC_ISO_DATE));
    }

    /**
     * Convert number to date.
     * @param num The number
     * @return A date
     */
    private static LocalDate asDate(final int num) {
        return LocalDate.parse(
            Integer.toString(num), DateTimeFormatter.BASIC_ISO_DATE
        );
    }
}
