<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="report">
        <table id="table-3" class="sort-table">
            <thead>
            <tr>
            <td>Archive</td>
            <td>Total Classes</td>
            <td>Abstract Classes</td>
            <td>Abstractness</td>
            <td>Afferent</td> 
            <td>Efferent</td>           
            <td>Instability</td>
            <td>Distance</td>
            </tr>
            </thead>
            <tbody>
            <xsl:choose>
                <xsl:when test="count(//container[@type='archive'][name(..)='container' or name(..)='report']) = 0">
    	        	<xsl:text disable-output-escaping="yes">&lt;tr&gt;</xsl:text>
				    <td colspan="8"><i>
				    <xsl:text>No Archives Found</xsl:text>
				    </i></td>
		        	<xsl:text disable-output-escaping="yes">&lt;/tr&gt;</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="//container[@type='archive'][name(..)='container' or name(..)='report']">
                        <xsl:sort select="@url"/>
                    </xsl:apply-templates>
                </xsl:otherwise>
            </xsl:choose>
            </tbody>
        </table>
    </xsl:template>

    <xsl:template match="container">

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

        <xsl:variable name="row-class">
            <xsl:choose>
                <xsl:when test="position() mod 2 = 0">even</xsl:when>
                <xsl:otherwise>odd</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:text disable-output-escaping="yes">&#10;&lt;tr class="</xsl:text>
        <xsl:value-of select="$row-class"/>
       	<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
        
        <td>
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
        </td>
        
    	<xsl:text disable-output-escaping="yes">&lt;/tr&gt;</xsl:text>

    </xsl:template>
    
</xsl:stylesheet>
