<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="report">
        <xsl:text disable-output-escaping="yes">digraph G {&#10;</xsl:text>
        <xsl:apply-templates/>
        <xsl:text disable-output-escaping="yes">}</xsl:text>
    </xsl:template>
    <xsl:template match="container[@type='archive']/dependency[@type='cycle']">
        <xsl:variable name="first-dependency-url" select="container/@url"/>
        <xsl:for-each select="container[@type='archive']">
            <xsl:variable name="current-dependency-url" select="@url"/>
            <xsl:variable name="next-dependency-url">
                <xsl:choose>
                    <xsl:when test="position() = last()">
                        <xsl:value-of select="$first-dependency-url"/>				    
				    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="following::*/@url"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <xsl:text disable-output-escaping="yes">    "</xsl:text>
            <xsl:value-of select="$current-dependency-url"/>
            <xsl:text disable-output-escaping="yes">" -&gt; "</xsl:text>
            <xsl:value-of select="$next-dependency-url"/>
            <xsl:text disable-output-escaping="yes">";&#10;</xsl:text>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
