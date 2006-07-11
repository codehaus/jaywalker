<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="report">
        <h3>Archive Dependencies Metrics</h3>
        <table width="95%" cellspacing="2" cellpadding="5" border="0" class="details">
            <tr>
            <th>Archive</th>
            <th>Total Classes</th>
            <th>Abstract Classes</th>
            <th>Abstractness</th>
            <th>Afferent</th> 
            <th>Efferent</th>           
            <th>Instability</th>
            <th>Distance</th>
            </tr>
            <xsl:choose>
                <xsl:when test="count(//container[@type='archive'][name(..)='container' or name(..)='report']) = 0">
				    <tr><td colspan="8"><i>
				    <xsl:text>No Archives Found</xsl:text>
				    </i></td></tr>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates>
                        <xsl:sort select="@url"/>
                    </xsl:apply-templates>
                </xsl:otherwise>
            </xsl:choose>
        </table>
    </xsl:template>

    <xsl:template match="container[@type='archive'][name(..)='container' or name(..)='report']">

        <xsl:variable name="url"
                select="@url"/>

        <xsl:variable name="all-abstract-class-count"
                select="count(descendant::element[@type='interface' or @type='abstract'][name(..)='container'])"/>
        <xsl:variable name="nested-abstract-class-count"
                select="count(descendant::container[@type='archive']/element[@type='interface' or @type='abstract'][name(..)='container'])"/>

        <xsl:variable name="all-total-class-count"
                select="$all-abstract-class-count + count(descendant::element[@type='class'][name(..)='container'])"/>
        <xsl:variable name="nested-total-class-count"
                select="$nested-abstract-class-count + count(descendant::container[@type='archive']/element[@type='class'][name(..)='container'])"/>

        <xsl:variable name="abstract-class-count"
                select="$all-abstract-class-count - $nested-abstract-class-count"/>
        <xsl:variable name="total-class-count"
                select="$all-total-class-count - $nested-total-class-count"/>

        <xsl:variable name="abstractness"
                select="round(100 * ($abstract-class-count div $total-class-count)) div 100"/>

        <xsl:variable name="afferent"
                select="count(//container[@type='archive']/dependency[@type='resolved']/container[@url=$url])"/>

        <xsl:variable name="efferent"
                select="count(child::dependency[@type='resolved']/container[@type='archive'])"/>

        <xsl:variable name="instability"
                select="round(100 * ($efferent div ($efferent + $afferent))) div 100"/>
                
        <xsl:variable name="distance"
                select="round(100 * ($abstractness + $instability)) div 100"/>

        <tr><td>
        <xsl:value-of select="@url"/>
        </td><td>
        <xsl:value-of select="$total-class-count"/>        
        </td><td>
        <xsl:value-of select="$abstract-class-count"/>
        </td><td>
        <xsl:value-of select="$abstractness"/>        
        </td><td>
        <xsl:value-of select="$afferent"/>        
        </td><td>
        <xsl:value-of select="$efferent"/>        
        </td><td>
        <xsl:value-of select="$instability"/>        
        </td><td>
        <xsl:value-of select="$distance"/>
        </td></tr>
        <xsl:apply-templates/>
    </xsl:template>
    
</xsl:stylesheet>
