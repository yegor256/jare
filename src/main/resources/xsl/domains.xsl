<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="1.0">
  <xsl:output method="xsl" doctype-system="about:legacy-compat" encoding="UTF-8" indent="yes"/>
  <xsl:strip-space elements="*"/>
  <xsl:include href="/xsl/layout.xsl"/>
  <xsl:template match="page" mode="head">
    <title>
      <xsl:text>Domains</xsl:text>
    </title>
  </xsl:template>
  <xsl:template match="page" mode="body">
    <form action="{links/link[@rel='add']/@href}" method="post">
      <label for="name">Register a new domain:</label>
      <input type="text" id="name" name="name" size="45" maxlength="150" required="required"/>
      <button type="submit">Add</button>
    </form>
    <xsl:if test="domains/domain">
      <p>
        <xsl:if test="count(domains/domain) = 1">
          <xsl:text>There is just one domain:</xsl:text>
        </xsl:if>
        <xsl:if test="count(domains/domain) &gt; 1">
          <xsl:text>There are </xsl:text>
          <xsl:value-of select="count(domains/domain)"/>
          <xsl:text> domains registered:</xsl:text>
        </xsl:if>
      </p>
      <xsl:apply-templates select="domains"/>
      <form action="{links/link[@rel='invalidate']/@href}" method="get">
        <label for="name">You can invalidate any URL at your domain:</label>
        <input type="text" id="url" name="url" size="60" required="required"/>
        <button type="submit">Invalidate</button>
      </form>
    </xsl:if>
    <xsl:if test="not(domains/domain)">
      <p>
        <xsl:text>There are no registered domains yet.</xsl:text>
      </p>
    </xsl:if>
  </xsl:template>
  <xsl:template match="domains">
    <ul>
      <xsl:apply-templates select="domain"/>
    </ul>
  </xsl:template>
  <xsl:template match="domain">
    <li>
      <xsl:value-of select="name"/>
      <xsl:text>: </xsl:text>
      <xsl:value-of select="format-number(usage div 1024, '###,###,###')"/>
      <xsl:text>Kb </xsl:text>
      <a href="{links/link[@rel='delete']/@href}">
        <xsl:text>delete</xsl:text>
      </a>
    </li>
  </xsl:template>
</xsl:stylesheet>
