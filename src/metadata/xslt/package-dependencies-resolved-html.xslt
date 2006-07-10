<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    <xsl:strip-space elements="*"/>
    <xsl:key name="arc" match="dependency[@type='resolved']/container[@type='package']"
        use="concat(../../@value,':',@value)"/>
    <xsl:template match="report">
        <h3>Package Resolved Dependencies</h3>
        <table width="95%" cellspacing="2" cellpadding="5" border="0" class="details">
            <th>Package</th>
            <th>Dependency</th>
            <xsl:for-each
                select="//container[generate-id()=generate-id(key('arc',concat(../../@value,':',@value)))]">
                <xsl:sort select="../../@value"/>
                <xsl:variable name="source">
                    <xsl:choose>
                        <xsl:when test="not(../../@value)">&lt;default&gt;</xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="../../@value"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:choose>
                    <xsl:when test="not(preceding-sibling::node())">
                        <xsl:text disable-output-escaping="yes">&#10;&lt;tr&gt;</xsl:text>
                        <xsl:text disable-output-escaping="yes">&lt;td rowspan="</xsl:text>
                        <xsl:value-of select="../@value"/>
                        <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
                        <xsl:value-of select="$source"/>
                        <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text disable-output-escaping="yes">&#10;&lt;tr&gt;</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
                <td>
                    <xsl:value-of select="@value"/>
                </td>
                <xsl:text disable-output-escaping="yes">&lt;/tr&gt;&#10;</xsl:text>
            </xsl:for-each>
        </table>
    </xsl:template>
</xsl:stylesheet>
