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
    <xsl:template match="/page">
        <html lang="en">
            <head>
                <meta charset="UTF-8"/>
                <meta name="viewport" content="width=device-width,minimum-scale=1,initial-scale=1"/>
                <link rel="shortcut icon" href="/images/logo.png"/>
                <link rel="stylesheet" href="http://yegor256.github.io/tacit/tacit.min.css"/>
                <link rel="stylesheet" href="/css/main.css"/>
                <xsl:apply-templates select="." mode="head"/>
            </head>
            <body>
                <section>
                    <header>
                        <nav>
                            <ul>
                                <li>
                                    <a href="{links/link[@rel='home']/@href}">
                                        <img src="/images/logo.svg" class="logo"/>
                                    </a>
                                </li>
                            </ul>
                        </nav>
                        <nav>
                            <ul class="menu">
                                <li>
                                    <xsl:if test="identity">
                                        <xsl:text>@</xsl:text>
                                        <xsl:value-of select="identity/login"/>
                                    </xsl:if>
                                    <xsl:if test="not(identity)">
                                        <a href="{links/link[@rel='takes:github']/@href}">
                                            <xsl:text>login</xsl:text>
                                        </a>
                                    </xsl:if>
                                </li>
                                <xsl:if test="identity">
                                    <li>
                                        <a href="{links/link[@rel='domains']/@href}">
                                            <xsl:text>domains</xsl:text>
                                        </a>
                                    </li>
                                </xsl:if>
                                <xsl:if test="identity">
                                    <li>
                                        <a href="{links/link[@rel='takes:logout']/@href}">
                                            <xsl:text>exit</xsl:text>
                                        </a>
                                    </li>
                                </xsl:if>
                            </ul>
                        </nav>
                        <xsl:apply-templates select="flash"/>
                    </header>
                    <article>
                        <xsl:apply-templates select="." mode="body"/>
                    </article>
                    <footer>
                        <nav>
                            <ul style="color:gray;">
                                <li>
                                    <xsl:text>v</xsl:text>
                                    <xsl:value-of select="version/name"/>
                                </li>
                                <li>
                                    <xsl:call-template name="millis">
                                        <xsl:with-param name="millis" select="millis"/>
                                    </xsl:call-template>
                                </li>
                                <li>
                                    <xsl:value-of select="@sla"/>
                                </li>
                            </ul>
                        </nav>
                        <nav>
                            <ul>
                                <li>
                                    <a href="http://www.teamed.io">
                                        <img src="http://img.teamed.io/btn.svg"/>
                                    </a>
                                </li>
                            </ul>
                        </nav>
                        <nav>
                            <ul>
                                <li>
                                    <a href="https://github.com/yegor256/jare/stargazers">
                                        <img src="https://img.shields.io/github/stars/yegor256/jare.svg?style=flat-square" alt="github stars"/>
                                    </a>
                                </li>
                            </ul>
                        </nav>
                    </footer>
                </section>
                <script>
                    (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
                    (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
                    m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
                    })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
                    ga('create', 'UA-1963507-42', 'auto');
                    ga('send', 'pageview');
                </script>
            </body>
        </html>
    </xsl:template>
    <xsl:template match="flash">
        <p>
            <xsl:attribute name="style">
                <xsl:text>color:</xsl:text>
                <xsl:choose>
                    <xsl:when test="level = 'INFO'">
                        <xsl:text>#348C62</xsl:text>
                    </xsl:when>
                    <xsl:when test="level = 'WARNING'">
                        <xsl:text>orange</xsl:text>
                    </xsl:when>
                    <xsl:when test="level = 'SEVERE'">
                        <xsl:text>red</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>inherit</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:value-of select="message"/>
        </p>
    </xsl:template>
    <xsl:template name="millis">
        <xsl:param name="millis"/>
        <xsl:choose>
            <xsl:when test="not($millis)">
                <xsl:text>?</xsl:text>
            </xsl:when>
            <xsl:when test="$millis &gt; 60000">
                <xsl:value-of select="format-number($millis div 60000, '0')"/>
                <xsl:text>min</xsl:text>
            </xsl:when>
            <xsl:when test="$millis &gt; 1000">
                <xsl:value-of select="format-number($millis div 1000, '0.0')"/>
                <xsl:text>s</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="format-number($millis, '0')"/>
                <xsl:text>ms</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
