<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="report">
        <table>
            <th>Archive</th>
            <th>URL</th>
            <xsl:apply-templates/>
        </table>
    </xsl:template>
    <xsl:template match="container[@type='archive']">
        <xsl:if test="count(child::dependency)>0">
            <xsl:text disable-output-escaping="yes">&#10;&lt;tr&gt;</xsl:text>
            <xsl:text disable-output-escaping="yes">&lt;td rowspan="</xsl:text>
            <xsl:variable name="container-dependencies"
                select="child::dependency[@type='cycle']/container[@type='archive']"/>
            <xsl:value-of select="count($container-dependencies)"/>
            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
            <xsl:value-of select="@url"/>
            <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>
            <xsl:for-each select="$container-dependencies">
                <td>
                    <xsl:value-of select="@url"/>
                </td>
                <xsl:text disable-output-escaping="yes">&lt;/tr&gt;&#10;</xsl:text>
            </xsl:for-each>
        </xsl:if>
        <xsl:apply-templates/>
    </xsl:template>
</xsl:stylesheet>
