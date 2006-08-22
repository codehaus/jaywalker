<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    <xsl:strip-space elements="*"/>
    <xsl:key name="distinct-package" match="container[dependency[@type='resolved']/container[@type='package']]" use="@value"/>
    <xsl:key name="distinct-package-dependencies" match="container/dependency[@type='resolved']/container[@type='package']" use="@value"/>
        
    <xsl:template match="report">
        <table id="package-dependencies-resolved-table" class="sort-table">
        	<thead>
            <tr>
            <td>Package</td>
            <td>Dependency</td>
            </tr>
            </thead>
            <tbody>
            <xsl:choose>
                <xsl:when test="count(//container[dependency[@type='resolved']/container[@type='package']]) = 0">
				    <xsl:text disable-output-escaping="yes">&lt;tr&gt;</xsl:text>
				    <td colspan="2"><i>
				    <xsl:text>No Packages Found</xsl:text>
				    </i></td>
				    <xsl:text disable-output-escaping="yes">&lt;/tr&gt;</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="//container[generate-id()=generate-id(key('distinct-package',@value))][dependency[@type='resolved']/container[@type='package'][@value!=../../@value]]">
                        <xsl:sort select="@value"/>
                    </xsl:apply-templates>
                </xsl:otherwise>
            </xsl:choose>
            </tbody>
        </table>
    </xsl:template>
    
    <xsl:template match="container">
    
        <xsl:variable name="package-name" select="@value"/>

        <xsl:variable name="package-dependencies" 
                select="//container[@value=$package-name]
                         /dependency[@type='resolved']
                         /container[@type='package']
                          [@value!=$package-name]
                          [generate-id()=generate-id(key('distinct-package-dependencies',@value))]" />
                          
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
        <xsl:value-of select="count($package-dependencies)+1"/>
        <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
        <xsl:value-of select="$package-name"/>
        <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>
                
        <xsl:text disable-output-escaping="yes">&lt;/tr&gt;&#10;</xsl:text>
        
        <xsl:for-each select="$package-dependencies">
            <xsl:sort select="@value"/>

            <xsl:text disable-output-escaping="yes">&#10;&lt;tr class="</xsl:text>
            <xsl:value-of select="$row-class"/>
           	<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>

            <td>
                <xsl:value-of select="@value"/>
            </td>

            <xsl:text disable-output-escaping="yes">&lt;/tr&gt;&#10;</xsl:text>
        </xsl:for-each>
        
    </xsl:template>

</xsl:stylesheet>
