<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="report">
        <h3>Class Cyclic Dependencies</h3>
        <table width="95%" cellspacing="2" cellpadding="5" border="0" class="details">
            <th>Archive</th>
            <th>Cycle</th>
            <xsl:apply-templates/>
        </table>
    </xsl:template>
    <xsl:template match="element[@type='class' or @type='interface' or @type='abstract']">
        <xsl:if test="count(child::dependency)>0">
            <xsl:variable name="element-dependencies"
                select="child::dependency[@type='cycle']/element[@type='class' or @type='interface' or @type='abstract']"/>
            <xsl:if test="count($element-dependencies)>0">
                <xsl:text disable-output-escaping="yes">&#10;&lt;tr&gt;</xsl:text>
                <xsl:text disable-output-escaping="yes">&lt;td rowspan="</xsl:text>
                <xsl:value-of select="count($element-dependencies)"/>
                <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
                <xsl:value-of select="@url"/>
                <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>
                <xsl:for-each select="$element-dependencies">
                    <td>
                        <xsl:value-of select="@url"/>
                    </td>
                    <xsl:text disable-output-escaping="yes">&lt;/tr&gt;&#10;</xsl:text>
                </xsl:for-each>
            </xsl:if>
        </xsl:if>
        <xsl:apply-templates/>
    </xsl:template>
</xsl:stylesheet>
