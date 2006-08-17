<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    <xsl:strip-space elements="*"/>
    
    <xsl:template match="report">
        <table id="table-3" class="sort-table">
            <thead>
            <tr>
            <td>Archive</td>
            <td>Dependency</td>
            </tr>
            </thead>
            <tbody>
            <xsl:choose>
                <xsl:when test="count(//container[@type='archive']/dependency[@type='resolved']/container[@type='archive']) = 0">
		            <xsl:text disable-output-escaping="yes">&lt;tr&gt;</xsl:text>
				    <td colspan="2"><i>
				    <xsl:text>No Archives Found</xsl:text>
				    </i></td>
				    <xsl:text disable-output-escaping="yes">&lt;/tr&gt;</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="//container[@type='archive'][count(dependency[@type='resolved']/container[@type='archive'])>0]">
                        <xsl:sort select="@url"/>
                    </xsl:apply-templates>
                </xsl:otherwise>
            </xsl:choose>
            </tbody>
        </table>
    </xsl:template>
    
    <xsl:template match="container">

        <xsl:variable name="row-class">
            <xsl:choose>
                <xsl:when test="position() mod 2 = 0">even</xsl:when>
                <xsl:otherwise>odd</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:text disable-output-escaping="yes">&#10;&lt;tr class="</xsl:text>
        <xsl:value-of select="$row-class"/>
        <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
                
        <xsl:text disable-output-escaping="yes">&lt;td rowspan="</xsl:text>
        <xsl:variable name="container-dependencies"
            select="child::dependency[@type='resolved']/container[@type='archive']"/>
        <xsl:value-of select="count($container-dependencies)+1"/>
        <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
        <xsl:value-of select="@url"/>
        <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>
        
        <xsl:text disable-output-escaping="yes">&lt;/tr&gt;&#10;</xsl:text>

        <xsl:for-each select="$container-dependencies">
            <xsl:sort select="@url"/>
            <xsl:text disable-output-escaping="yes">&#10;&lt;tr class="</xsl:text>
            <xsl:value-of select="$row-class"/>
           	<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
            <td>
                <xsl:value-of select="@url"/>
            </td>
            <xsl:text disable-output-escaping="yes">&lt;/tr&gt;&#10;</xsl:text>
        </xsl:for-each>

    </xsl:template>
</xsl:stylesheet>
