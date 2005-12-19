<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="report">
        <html>
            <body>
                <table>
                    <th>Container</th>
                    <th>Package Name</th>
                    <xsl:apply-templates/>
                </table>
            </body>
        </html>
    </xsl:template>
    <xsl:template match="container">
        <xsl:if test="count(child::dependency)>0">
            <xsl:text disable-output-escaping="yes">&#10;&lt;tr&gt;</xsl:text>
            <xsl:text disable-output-escaping="yes">&lt;td rowspan="</xsl:text>
            <xsl:variable name="package-dependencies" select="child::dependency[@type='resolved']/container[@type='package']"/>
            <xsl:value-of select="count($package-dependencies)"/>
            <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
            <xsl:value-of select="@url"/>
            <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>
            <xsl:for-each select="$package-dependencies">
                <td>
                    <xsl:value-of select="@value"/>
                </td>
                <xsl:text disable-output-escaping="yes">&lt;/tr&gt;&#10;</xsl:text>
            </xsl:for-each>
        </xsl:if>
        <xsl:apply-templates/>
    </xsl:template>
</xsl:stylesheet>
