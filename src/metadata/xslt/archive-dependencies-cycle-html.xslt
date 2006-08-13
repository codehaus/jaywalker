<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="report">
        <table id="table-3" class="sort-table">
        	<thead>
            <tr>
            <td>Archive</td>
            <td>Cycle</td>
            </tr>
            </thead>
            <tbody>
            <xsl:choose>
                <xsl:when test="count(//dependency[@type='cycle']/container[@type='archive']) = 0">
				    <tr><td colspan="2"><i>
				    <xsl:text>No Cycles Found</xsl:text>
				    </i></td></tr>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates/>
                </xsl:otherwise>
            </xsl:choose>
            </tbody>
        </table>
    </xsl:template>
    <xsl:template match="container[@type='archive']">
        <xsl:if test="count(child::dependency)>0">
            <xsl:variable name="container-dependencies"
                select="child::dependency[@type='cycle']/container[@type='archive']"/>
            <xsl:if test="count($container-dependencies)>0">
                <xsl:text disable-output-escaping="yes">&#10;&lt;tr&gt;</xsl:text>
                <xsl:text disable-output-escaping="yes">&lt;td rowspan="</xsl:text>
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
        </xsl:if>
        <xsl:apply-templates/>
    </xsl:template>
</xsl:stylesheet>
