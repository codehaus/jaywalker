<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                              xmlns:func="http://exslt.org/functions"
                              xmlns:jw="http://jaywalker.codehaus.org"
                              extension-element-prefixes="func">
    <xsl:import href="jaywalker-common.xslt"/>
    <xsl:output method="html"/>
    <xsl:strip-space elements="*"/>
    <xsl:key name="distinct-package" match="container[dependency[@type='resolved']/container[@type='package']]" use="@value"/>

    <xsl:template match="report">
        <table id="package-dependencies-metrics-table" class="sort-table">
        	<thead>
            <tr>
            <td>Package</td>
            <td><xsl:value-of disable-output-escaping="yes" select="jw:tooltip('Total Classes','Total Classes&lt;p&gt;The total number of concrete and abstract classes&lt;br/&gt;and interfaces in a package.&lt;/p&gt;&lt;p&gt;This number represents the extensibility of the&lt;br/&gt;package.&lt;/p&gt;')"/></td>
            <td><xsl:value-of disable-output-escaping="yes" select="jw:tooltip('Abstract Classes', 'Abstract Classes&lt;p&gt;The total number of abstract classes and interfaces&lt;br/&gt;in a package.&lt;/p&gt;&lt;p&gt;This number also represents the extensibility of&lt;br/&gt;the package.&lt;/p&gt;')"/></td>
            <td><xsl:value-of disable-output-escaping="yes" select="jw:tooltip('Abstractness', 'Abstractness (A)&lt;p&gt;The ratio of the number of abstract classes and&lt;br/&gt;interfaces to the total number of classes.&lt;/p&gt;')"/></td>
            <td><xsl:value-of disable-output-escaping="yes" select="jw:tooltip('Afferent', 'Afferent Couplings (Ca)&lt;p&gt;The number of other packages that depend upon&lt;br/&gt;classes within the package.&lt;/p&gt;&lt;p&gt;This metric is an indicator of the packages&lt;br/&gt;responsibility.&lt;/p&gt;')"/></td> 
            <td><xsl:value-of disable-output-escaping="yes" select="jw:tooltip('Efferent', 'Efferent Couplings (Ce)&lt;p&gt;The number of other packages that the classes&lt;br/&gt;within the package depend upon.&lt;/p&gt;&lt;p&gt;This metric is an indicator of the packages&lt;br/&gt;independence.&lt;/p&gt;')"/></td>           
            <td><xsl:value-of disable-output-escaping="yes" select="jw:tooltip('Instability', 'Instability (I)&lt;p&gt;The ratio of efferent coupling (Ce) to total coupling&lt;br/&gt;(Ce + Ca) such that I = Ce / (Ce + Ca).&lt;/p&gt;&lt;p&gt;This ratio is an indicator of the packages resilience&lt;br/&gt;to change.&lt;/p&gt;&lt;p&gt;The range for this metric is 0 to 1, with I = 0&lt;br/&gt;indicating a completely stable package and I = 1&lt;br/&gt;indicating a completely instable package&lt;/p&gt;')"/></td>
            <td><xsl:value-of disable-output-escaping="yes" select="jw:tooltip('Distance', 'Distance from the Main Sequence (D)&lt;p&gt;The perpendicular distance of a package from the&lt;br/&gt;idealized line A + I = 1.&lt;/p&gt;&lt;p&gt;This metric is an indicator of the packages balance&lt;br/&gt;between abstractness and stability.&lt;/p&gt;&lt;p&gt;Ideal packages are either completely abstract and&lt;br/&gt;stable (x=0, y=1) or completely concrete and instable&lt;br/&gt;(x=1, y=0).&lt;/p&gt;&lt;p&gt;The range for this metric  is 0 to 1, with D=0&lt;br/&gt;indicating a package that is coincident with the&lt;br/&gt;main sequence and D=1 indicating a package that is&lt;br/&gt;as far from the main sequence as possible.&lt;/p&gt;')"/></td>
            </tr>
            </thead>
            <tbody>
            <xsl:choose>
                <xsl:when test="count(//container/element[@type='class' or @type='interface' or @type='abstract']) = 0">
				    <xsl:text disable-output-escaping="yes">&lt;tr&gt;</xsl:text>
				    <td colspan="8"><i>
				    <xsl:text>No Packages Found</xsl:text>
				    </i></td>
				    <xsl:text disable-output-escaping="yes">&lt;/tr&gt;</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="//container[generate-id()=generate-id(key('distinct-package',@value))][count(element[@type='class' or @type='interface' or @type='abstract']) > 0]">
                        <xsl:sort select="@url"/>
                    </xsl:apply-templates>
                </xsl:otherwise>
            </xsl:choose>
            </tbody>
        </table>
    </xsl:template>
    
    <xsl:template match="container">
    
        <xsl:variable name="package-name" select="@value"/>

        <xsl:variable name="abstract-class-count"
                select="count(//container[@value=$package-name]/element[@type='interface' or @type='abstract'])"/>

        <xsl:variable name="total-class-count"
                select="$abstract-class-count + count(//container[@value=$package-name]/element[@type='class'])"/>

        <xsl:variable name="abstractness"
                select="round(100 * ($abstract-class-count div $total-class-count)) div 100"/>

        <xsl:variable name="afferent"
                select="count(//container/dependency[@type='resolved']/container[@type='package'][@value=$package-name])"/>

        <xsl:variable name="efferent"
                select="count(//container[@value=$package-name]/dependency[@type='resolved']/container[@type='package'])"/>

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
        <xsl:value-of select="$package-name"/>
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
       
        <xsl:text disable-output-escaping="yes">&lt;/tr&gt;&#10;</xsl:text>
       
    </xsl:template>
    
</xsl:stylesheet>
