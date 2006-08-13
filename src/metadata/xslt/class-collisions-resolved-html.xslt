<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    <xsl:strip-space elements="*"/>
    <xsl:key name="distinct-classname" match="element" use="@value"/>
    
    <xsl:template match="report">
        <table id="table-3" class="sort-table">
        	<thead>
            <td>Class Name</td>
            <td>Collision</td>
            </thead>
            <tbody>
            <xsl:choose>
                <xsl:when test="count(//collision) = 0">
				    <tr><td colspan="2"><i>
				    <xsl:text>No Collisions Found</xsl:text>
				    </i></td></tr>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates/>
                </xsl:otherwise>
            </xsl:choose>
            </tbody>
        </table>
    </xsl:template>
    
    <xsl:template match="//element[generate-id()=generate-id(key('distinct-classname',@value))]">
        <xsl:if test="count(child::collision) > 0">
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
        </xsl:if>
    </xsl:template>
    
</xsl:stylesheet>
