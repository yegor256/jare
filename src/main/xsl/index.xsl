<?xml version="1.0"?>
<!--
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 jare.io
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
 -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml" version="1.0">
    <xsl:output method="html" doctype-system="about:legacy-compat"
        encoding="UTF-8" indent="yes" />
    <xsl:include href="/xsl/layout.xsl"/>
    <xsl:template match="page" mode="head">
        <title>
            <xsl:text>jare</xsl:text>
        </title>
    </xsl:template>
    <xsl:template match="page" mode="body">
        <p>
            <xsl:text>If you know what </xsl:text>
            <strong><xsl:text>CDN</xsl:text></strong>
            <xsl:text> is,</xsl:text>
            <xsl:text> but don't want to spend time and money</xsl:text>
            <xsl:text> to employ a full-scale solution like </xsl:text>
            <xsl:text> Akamai or CloudFront,</xsl:text>
            <xsl:text> jare.io is right for you.</xsl:text>
        </p>
        <p>
            <xsl:text>Say, you have something like this in your HTML:</xsl:text>
        </p>
        <pre>
            <xsl:text>&lt;img src="http://google.com/logo.gif"/&gt;</xsl:text>
        </pre>
        <p>
            <xsl:text>Just change the URL and this </xsl:text>
            <code><xsl:text>logo.gif</xsl:text></code>
            <xsl:text> will be delivered through AWS CloudFront delivery servers,</xsl:text>
            <xsl:text> for free:</xsl:text>
        </p>
        <pre>
            <xsl:text>&lt;img src="http://cf.jare.io/?u=http://google.com/logo.gif"/&gt;</xsl:text>
        </pre>
        <p>
            <xsl:text>Well, there is one more thing you should do.</xsl:text>
            <xsl:text> You should login using your GitHub account and</xsl:text>
            <xsl:text> register your domain with us (</xsl:text>
            <code><xsl:text>google.com</xsl:text></code>
            <xsl:text> in this example).</xsl:text>
            <xsl:text> We want to know who you are, mostly in order to track usage.</xsl:text>
            <xsl:text> By the way, if you want to know how it all works, read this blog post: </xsl:text>
            <a href="http://www.yegor256.com/2016/03/30/jare-instant-free-cdn.html">
                <xsl:text>Jare.io, an Instant and Free CDN</xsl:text>
            </a>
        </p>
        <p>
            <xsl:text>In the nearest future we will charge you, if the traffic is too high.</xsl:text>
            <xsl:text> In the mean time, please, don't abuse the system, keep your traffic to a reasonable limit.</xsl:text>
        </p>
        <xsl:if test="domains/domain">
            <p>
                <xsl:text>There are </xsl:text>
                <strong><xsl:value-of select="count(domains/domain)"/></strong>
                <xsl:text> domains registered now.</xsl:text>
                <xsl:text> This is the list of 50 most active of them.</xsl:text>
                <xsl:text> The amount of Mb is calculated over the last ten days.</xsl:text>
                <xsl:text> If you see yourself on top of the list, you most probably have to upgrade your account to premium.</xsl:text>
                <xsl:text> Please, </xsl:text>
                <a href="mailto:premium@jare.io"><xsl:text>email us</xsl:text></a>
                <xsl:text> to get an upgrade before we ban you.</xsl:text>
            </p>
            <xsl:apply-templates select="domains"/>
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
                <xsl:sort select="./usage" data-type="number" case-order="upper-first" />
                <xsl:if test="position() &lt; 50">
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
            <xsl:value-of select="format-number(usage div 1048576, '###,###,###')"/>
            <xsl:text>Mb</xsl:text>
        </li>
    </xsl:template>
</xsl:stylesheet>
