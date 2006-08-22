<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    <xsl:strip-space elements="*"/>
    <xsl:key name="distinct-classname" match="element" use="@value"/>

    <xsl:template match="report">
        <table id="class-conflict-resolved-table" class="sort-table">
        	<thead>
            <td>Class Name</td>
            <td>Conflict</td>
            <td>serialVersionUid</td>
            </thead>
            <tbody>
            <xsl:choose>
                <xsl:when test="count(//collision) = 0">
				    <tr><td colspan="3"><i>
				    <xsl:text>No Conflicts Found</xsl:text>
				    </i></td></tr>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="//element[generate-id()=generate-id(key('distinct-classname',@value))][count(collision/conflict)>0]"/>
                </xsl:otherwise>
            </xsl:choose>
            </tbody>
        </table>
    </xsl:template>
    
    <xsl:template match="element">

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
        <xsl:value-of select="count(child::collision/conflict)+1"/>
        <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
        <xsl:value-of select="@value"/>
        <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>
            <td>
                <xsl:value-of select="@url"/>
            </td>
            <td>
                <xsl:value-of select="conflict/@value"/>
            </td>
        <xsl:text disable-output-escaping="yes">&lt;/tr&gt;&#10;</xsl:text>

        <xsl:for-each select="collision">
            <xsl:text disable-output-escaping="yes">&#10;&lt;tr class="</xsl:text>
            <xsl:value-of select="$row-class"/>
           	<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
            <td>
                <xsl:value-of select="@url"/>
            </td>
            <td>
                <xsl:value-of select="conflict[@type='serialVersionUid']/@value"/>
            </td>
            <xsl:text disable-output-escaping="yes">&lt;/tr&gt;&#10;</xsl:text>
        </xsl:for-each>

    </xsl:template>
</xsl:stylesheet>
