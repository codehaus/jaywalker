<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="report">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:key name="distinct-classname" match="element" use="@value"/>
    <xsl:template match="/">
        <table>
            <th>Class Name</th>
            <th>URL</th>
            <xsl:for-each
                select="//element[generate-id()=generate-id(key('distinct-classname',@value))]">
                <xsl:text disable-output-escaping="yes">&#10;&lt;tr&gt;</xsl:text>
                <xsl:text disable-output-escaping="yes">&lt;td rowspan="</xsl:text>
                <xsl:value-of select="count(child::collision)+1"/>
                <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
                <xsl:value-of select="@value"/>
                <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>
                <td>
                    <xsl:value-of select="@url"/>
                </td>
                <xsl:for-each select="collision">
                    <xsl:if test="position() > 0">
                        <xsl:text disable-output-escaping="yes">&#10;&lt;tr&gt;</xsl:text>
                    </xsl:if>
                    <td>
                        <xsl:value-of select="@url"/>
                    </td>
                    <xsl:text disable-output-escaping="yes">&lt;/tr&gt;&#10;</xsl:text>
                </xsl:for-each>
            </xsl:for-each>
        </table>
    </xsl:template>
</xsl:stylesheet>
