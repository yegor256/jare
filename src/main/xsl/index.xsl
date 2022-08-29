<?xml version="1.0"?>
<!--
The MIT License (MIT)

Copyright (c) 2016-2022 Yegor Bugayenko

Permission is hereby granted, free of charge,  to any person obtaining
a copy  of  this  software  and  associated  documentation files  (the
"Software"),  to deal in the Software  without restriction,  including
without limitation the rights to use,  copy,  modify,  merge, publish,
distribute,  sublicense,  and/or sell  copies of the Software,  and to
permit persons to whom the Software is furnished to do so,  subject to
the  following  conditions:   the  above  copyright  notice  and  this
permission notice  shall  be  included  in  all copies or  substantial
portions of the Software.  The software is provided  "as is",  without
warranty of any kind, express or implied, including but not limited to
the warranties  of merchantability,  fitness for  a particular purpose
and non-infringement.  In  no  event shall  the  authors  or copyright
holders be liable for any claim,  damages or other liability,  whether
in an action of contract,  tort or otherwise,  arising from, out of or
in connection with the software or  the  use  or other dealings in the
software.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="1.0">
  <xsl:output method="xml" doctype-system="about:legacy-compat" encoding="UTF-8" indent="yes"/>
  <xsl:strip-space elements="*"/>
  <xsl:include href="/xsl/layout.xsl"/>
  <xsl:template match="page" mode="head">
    <title>
      <xsl:text>Jare</xsl:text>
    </title>
  </xsl:template>
  <xsl:variable name="max" select="25"/>
  <xsl:template match="page" mode="body">
    <p>
      <xsl:text>If you know what </xsl:text>
      <strong>
        <xsl:text>CDN</xsl:text>
      </strong>
      <xsl:text> (</xsl:text>
      <a href="https://en.wikipedia.org/wiki/Content_delivery_network">
        <xsl:text>Content Delivery Network</xsl:text>
      </a>
      <xsl:text>) is,</xsl:text>
      <xsl:text> but don't want to spend time and money</xsl:text>
      <xsl:text> to employ a full-scale solution like </xsl:text>
      <xsl:text> Akamai or CloudFront,</xsl:text>
      <xsl:text> this system is the right choice for you.</xsl:text>
    </p>
    <p>
      <xsl:text>Say, you have something like this in your HTML:</xsl:text>
    </p>
    <pre>
      <xsl:text>&lt;img src="http://google.com/logo.gif"/&gt;</xsl:text>
    </pre>
    <p>
      <xsl:text>Just change the URL and this </xsl:text>
      <code>
        <xsl:text>logo.gif</xsl:text>
      </code>
      <xsl:text> will be delivered through </xsl:text>
      <a href="https://aws.amazon.com/cloudfront/">
        <xsl:text>AWS CloudFront</xsl:text>
      </a>
      <xsl:text> delivery servers,</xsl:text>
      <xsl:text> for free:</xsl:text>
    </p>
    <pre>
      <xsl:text>&lt;img src="http://cf.jare.io/?u=http://google.com/logo.gif"/&gt;</xsl:text>
    </pre>
    <p>
      <xsl:text>SSL is supported, you can use either</xsl:text>
      <code>
        <xsl:text>http://cf.jare.io</xsl:text>
      </code>
      <xsl:text> or </xsl:text>
      <code>
        <xsl:text>https://cf.jare.io</xsl:text>
      </code>
      <xsl:text>.</xsl:text>
    </p>
    <p>
      <xsl:text>Well, there is one more thing you should do.</xsl:text>
      <xsl:text> You should login using your GitHub account and</xsl:text>
      <xsl:text> register your domain with us (</xsl:text>
      <code>
        <xsl:text>google.com</xsl:text>
      </code>
      <xsl:text> in this example).</xsl:text>
      <xsl:text> We want to know who you are, mostly in order to track usage.</xsl:text>
      <xsl:text> By the way, if you want to know how it all works, read this blog post: </xsl:text>
      <a href="http://www.yegor256.com/2016/03/30/jare-instant-free-cdn.html">
        <xsl:text>Jare.io, an Instant and Free CDN</xsl:text>
      </a>
      <xsl:text> by </xsl:text>
      <a href="https://github.com/yegor256">
        <xsl:text>@yegor256</xsl:text>
      </a>
      <xsl:text>.</xsl:text>
    </p>
    <p>
      <xsl:text>Keep in mind that we </xsl:text>
      <a href="https://github.com/yegor256/jare/issues/25">
        <xsl:text>explicitly set</xsl:text>
      </a>
      <xsl:text> all HTTP caching headers to "cache forever" values.</xsl:text>
      <xsl:text> Thus, the only way to modify resources is to change their URLs/names.</xsl:text>
    </p>
    <xsl:if test="domains/domain">
      <p>
        <xsl:text>There are </xsl:text>
        <strong>
          <xsl:value-of select="count(domains/domain)"/>
          <xsl:text> domains</xsl:text>
        </strong>
        <xsl:text> registered now.</xsl:text>
        <xsl:text> This is the list of </xsl:text>
        <xsl:value-of select="$max"/>
        <xsl:text> most active of them.</xsl:text>
        <xsl:text> The amount of Mb is calculated over the last month.</xsl:text>
        <xsl:text> If your traffic is bigger than 50Gb/mo, you are not welcome here:</xsl:text>
        <xsl:text> please, use some other CDN.</xsl:text>
      </p>
      <xsl:apply-templates select="domains"/>
      <p>
        <xsl:text>Total traffic over the last month: </xsl:text>
        <strong>
          <xsl:value-of select="format-number(sum(domains/domain/usage) div (1024 * 1024 * 1024), '###,###,###')"/>
          <xsl:text>Gb</xsl:text>
        </strong>
        <xsl:text>.</xsl:text>
        <xsl:text> AWS charges all of us together </xsl:text>
        <a href="https://aws.amazon.com/cloudfront/pricing/">
          <xsl:text>approximately</xsl:text>
        </a>
        <xsl:text> $</xsl:text>
        <xsl:value-of select="format-number(sum(domains/domain/usage) div (1024 * 1024 * 1024) * 0.150, '###,###.00')"/>
        <xsl:text> monthly.</xsl:text>
      </p>
      <p>
        <xsl:text>It's an open source system.</xsl:text>
        <xsl:text> If you want to contribute, </xsl:text>
        <a href="https://github.com/yegor256/jare">
          <xsl:text>please do</xsl:text>
        </a>
        <xsl:text>.</xsl:text>
      </p>
    </xsl:if>
    <xsl:if test="not(domains/domain)">
      <p>
        <xsl:text>There are no registered domains yet.</xsl:text>
      </p>
    </xsl:if>
  </xsl:template>
  <xsl:template match="domains">
    <ul>
      <xsl:for-each select="domain">
        <xsl:sort select="- usage" data-type="number"/>
        <xsl:if test="position() &lt; $max">
          <xsl:apply-templates select="."/>
        </xsl:if>
      </xsl:for-each>
    </ul>
  </xsl:template>
  <xsl:template match="domain">
    <li>
      <xsl:value-of select="name"/>
      <xsl:text> by </xsl:text>
      <a href="https://github.com/{owner}">
        <xsl:text>@</xsl:text>
        <xsl:value-of select="owner"/>
      </a>
      <xsl:text>: </xsl:text>
      <xsl:variable name="mb" select="usage div 1048576"/>
      <xsl:choose>
        <xsl:when test="$mb &lt; 1">
          <xsl:value-of select="format-number($mb, '###.##')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="format-number($mb, '###,###,###')"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:text>Mb</xsl:text>
    </li>
  </xsl:template>
</xsl:stylesheet>
