<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    <xsl:strip-space elements="*"/>
    <xsl:key name="arc" match="dependency[@type='resolved']/container[@type='package']"
        use="concat(../../@value,':',@value)"/>
    <xsl:template match="report">
        <xsl:text disable-output-escaping="yes">digraph G {&#10;</xsl:text>
        <xsl:for-each
            select="//container[generate-id()=generate-id(key('arc',concat(../../@value,':',@value)))]">

            <xsl:variable name="source">
                <xsl:choose>
                    <xsl:when test="not(../../@value)">&lt;default&gt;</xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="../../@value"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>

            <xsl:variable name="target">
                <xsl:choose>
                    <xsl:when test="not(@value) or string-length(@value) = 0">&lt;default&gt;</xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="@value"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            
            <xsl:if test="$source != $target">
                <xsl:text disable-output-escaping="yes">    "</xsl:text>
                <xsl:value-of select="$source"/>
                <xsl:text disable-output-escaping="yes">" -&gt; "</xsl:text>
                <xsl:value-of select="$target"/>
                <xsl:text disable-output-escaping="yes">";&#10;</xsl:text>
            </xsl:if>
            
        </xsl:for-each>
        
        <xsl:text disable-output-escaping="yes">}</xsl:text>

    </xsl:template>

</xsl:stylesheet>
