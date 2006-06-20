<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="report">
        <xsl:text disable-output-escaping="yes">digraph G {&#10;</xsl:text>
        <xsl:apply-templates/>
        <xsl:text disable-output-escaping="yes">}</xsl:text>
    </xsl:template>
    <xsl:template match="container[@type='archive']">
        <xsl:variable name="url" select="@url"/>
        
        <!-- Add edge to represent dependency -->
        <xsl:if test="count(child::dependency)>0">
            <xsl:for-each select="child::dependency[@type='resolved']/container[@type='archive']">
                <xsl:text disable-output-escaping="yes">    "</xsl:text>
                <xsl:value-of select="$url"/>
                <xsl:text disable-output-escaping="yes">" -&gt; "</xsl:text>
                <xsl:value-of select="@url"/>
                <xsl:text disable-output-escaping="yes">";&#10;</xsl:text>
            </xsl:for-each>
        </xsl:if>

        <!-- Add edge to represent composition -->
        <xsl:if test="name(parent::node()) = 'container'">
            <xsl:for-each select="ancestor::container[@type='archive'][1]">
                <xsl:text disable-output-escaping="yes">    "</xsl:text>
                <xsl:value-of select="@url"/>
                <xsl:text disable-output-escaping="yes">" -&gt; "</xsl:text>
                <xsl:value-of select="$url"/>
                <xsl:text disable-output-escaping="yes">" [style=dotted,arrowtail=diamond];&#10;</xsl:text>
            </xsl:for-each>
        </xsl:if>
        <xsl:apply-templates/>
    </xsl:template>
</xsl:stylesheet>
