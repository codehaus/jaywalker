<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                              xmlns:func="http://exslt.org/functions"
                              xmlns:jw="http://jaywalker.codehaus.org"
                              extension-element-prefixes="func">
    <xsl:import href="jaywalker-common.xslt"/>
    <xsl:output method="html"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="report">
        <table id="archive-dependencies-metrics-table" class="sort-table">
            <thead>
            <tr>
            <td>Archive</td>
            <td><xsl:value-of disable-output-escaping="yes" select="jw:tooltip('Total Classes','Total Classes&lt;p&gt;The total number of concrete and abstract classes&lt;br/&gt;and interfaces in an archive.&lt;/p&gt;&lt;p&gt;This number represents the extensibility of the&lt;br/&gt;archive.&lt;/p&gt;')"/></td>
            <td><xsl:value-of disable-output-escaping="yes" select="jw:tooltip('Abstract Classes', 'Abstract Classes&lt;p&gt;The total number of abstract classes and interfaces&lt;br/&gt;in an archive.&lt;/p&gt;&lt;p&gt;This number also represents the extensibility of&lt;br/&gt;the archive.&lt;/p&gt;')"/></td>
            <td><xsl:value-of disable-output-escaping="yes" select="jw:tooltip('Abstractness', 'Abstractness (A)&lt;p&gt;The ratio of the number of abstract classes and&lt;br/&gt;interfaces to the total number of classes.&lt;/p&gt;')"/></td>
            <td><xsl:value-of disable-output-escaping="yes" select="jw:tooltip('Afferent', 'Afferent Couplings (Ca)&lt;p&gt;The number of other archives that depend upon&lt;br/&gt;classes within the archive.&lt;/p&gt;&lt;p&gt;This metric is an indicator of the archives&lt;br/&gt;responsibility.&lt;/p&gt;')"/></td> 
            <td><xsl:value-of disable-output-escaping="yes" select="jw:tooltip('Efferent', 'Efferent Couplings (Ce)&lt;p&gt;The number of other archives that the classes&lt;br/&gt;within the archive depend upon.&lt;/p&gt;&lt;p&gt;This metric is an indicator of the archives&lt;br/&gt;independence.&lt;/p&gt;')"/></td>           
            <td><xsl:value-of disable-output-escaping="yes" select="jw:tooltip('Instability', 'Instability (I)&lt;p&gt;The ratio of efferent coupling (Ce) to total coupling&lt;br/&gt;(Ce + Ca) such that I = Ce / (Ce + Ca).&lt;/p&gt;&lt;p&gt;This ratio is an indicator of the archives resilience&lt;br/&gt;to change.&lt;/p&gt;&lt;p&gt;The range for this metric is 0 to 1, with I = 0&lt;br/&gt;indicating a completely stable archive and I = 1&lt;br/&gt;indicating a completely instable archive&lt;/p&gt;')"/></td>
            <td><xsl:value-of disable-output-escaping="yes" select="jw:tooltip('Distance', 'Distance from the Main Sequence (D)&lt;p&gt;The perpendicular distance of an archive from the&lt;br/&gt;idealized line A + I = 1.&lt;/p&gt;&lt;p&gt;This metric is an indicator of the archives balance&lt;br/&gt;between abstractness and stability.&lt;/p&gt;&lt;p&gt;Ideal archives are either completely abstract and&lt;br/&gt;stable (x=0, y=1) or completely concrete and instable&lt;br/&gt;(x=1, y=0).&lt;/p&gt;&lt;p&gt;The range for this metric  is 0 to 1, with D=0&lt;br/&gt;indicating an archive that is coincident with the&lt;br/&gt;main sequence and D=1 indicating an archive that is&lt;br/&gt;as far from the main sequence as possible.&lt;/p&gt;')"/></td>
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
