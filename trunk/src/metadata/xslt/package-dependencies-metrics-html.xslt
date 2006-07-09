<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="report">
        <h3>Package Dependencies Metrics</h3>
        <table width="95%" cellspacing="2" cellpadding="5" border="0" class="details">
            <tr>
            <th>Package</th>
            <th>Total Classes</th>
            <th>Abstract Classes</th>
            <th>Abstractness</th>
            <th>Afferent</th> 
            <th>Efferent</th>           
            <th>Instability</th>
            <th>Distance</th>
            </tr>
            <xsl:apply-templates>
                <xsl:sort select="@value"/>
            </xsl:apply-templates>
        </table>
    </xsl:template>
    <xsl:template match="container">
		<xsl:if test="count(child::element[@type='class' or @type='interface' or @type='abstract']) > 0">

            <xsl:variable name="value"
                    select="@value"/>	

            <xsl:variable name="abstract-class-count"
                    select="count(child::element[@type='interface' or @type='abstract'])"/>

            <xsl:variable name="total-class-count"
                    select="$abstract-class-count + count(child::element[@type='class'])"/>

            <xsl:variable name="abstractness"
                    select="round(100 * ($abstract-class-count div $total-class-count)) div 100"/>

            <xsl:variable name="afferent"
                    select="count(//container/dependency[@type='resolved']/container[@type='package'][@value=$value])"/>

            <xsl:variable name="efferent"
                    select="count(child::dependency[@type='resolved']/container[@type='package'])"/>

            <xsl:variable name="instability"
                    select="round(100 * ($efferent div ($efferent + $afferent))) div 100"/>
                
            <xsl:variable name="distance"
                    select="round(100 * ($abstractness + $instability)) div 100"/>

            <tr><td>
            <xsl:value-of select="$value"/>
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
       
        </xsl:if>
        <xsl:apply-templates/>
    </xsl:template>
    
</xsl:stylesheet>
