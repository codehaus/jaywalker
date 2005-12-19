<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="report">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:key name="distinct-classname" match="dependency[@type='unresolved']" use="@value"/>
    <xsl:template match="/">
        <html>
            <body>
                <table>
                    <th>Class Name</th>
                    <th>URL</th>
                    <xsl:for-each
                        select="//dependency[@type='unresolved'][generate-id()=generate-id(key('distinct-classname',@value))]">
                        <xsl:variable name="classname" select="@value"/>
                        <xsl:variable name="dependency-nodes" select="//element/dependency[@value=$classname]"/>
                        <xsl:text disable-output-escaping="yes">&#10;&lt;tr&gt;</xsl:text>
                        <xsl:text disable-output-escaping="yes">&lt;td rowspan="</xsl:text>
                        <xsl:for-each select="/">
                            <xsl:value-of select="count($dependency-nodes)"/>
                        </xsl:for-each>
                        <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
                        <xsl:value-of select="$classname"/>
                        <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>
                        <xsl:for-each select="/">
                            <xsl:for-each select="$dependency-nodes">
                                <xsl:if test="position() > 1">
                                    <xsl:text disable-output-escaping="yes">&#10;&lt;tr&gt;</xsl:text>
                                </xsl:if>
                                <td>
                                    <xsl:value-of select="../@url"/>
                                </td>
                                <xsl:text disable-output-escaping="yes">&lt;/tr&gt;&#10;</xsl:text>
                            </xsl:for-each>
                        </xsl:for-each>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
