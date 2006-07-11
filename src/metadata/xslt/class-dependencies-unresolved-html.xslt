<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    <xsl:strip-space elements="*"/>
    <xsl:key name="distinct-classname" match="element[@type='class' or @type='interface' or @type='abstract']/dependency[@type='unresolved']" use="@value"/>

    <xsl:template match="report">
        <h3>Class Unresolved Dependencies</h3>
        <table width="95%" cellspacing="2" cellpadding="5" border="0" class="details">
            <tr>
            <th>Unresolved Class Name</th>
            <th>Dependent</th>
            </tr>
            <xsl:choose>
                <xsl:when test="count(//dependency[generate-id()=generate-id(key('distinct-classname',@value))]) = 0">
				    <tr><td colspan="2"><i>
				    <xsl:text>No Missing Classes</xsl:text>
				    </i></td></tr>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates>
                        <xsl:sort select="@value"/>
                    </xsl:apply-templates>
                </xsl:otherwise>
            </xsl:choose>
        </table>
    </xsl:template>
    
    <xsl:template match="//dependency[generate-id()=generate-id(key('distinct-classname',@value))]">
        <xsl:variable name="classname" select="@value"/>
        <xsl:variable name="dependencies" select="//dependency[@type='unresolved'][@value=$classname]"/>
        <xsl:text disable-output-escaping="yes">&#10;&lt;tr&gt;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;td rowspan="</xsl:text>
        <xsl:value-of select="count($dependencies)"/>
        <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
        <xsl:value-of select="$classname"/>
        <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>
                
        <xsl:for-each select="$dependencies">
            <xsl:if test="position()>1">
                <xsl:text disable-output-escaping="yes">&#10;&lt;tr&gt;</xsl:text>
            </xsl:if>
                <td>
                    <xsl:value-of select="../@url"/>
                </td>
            <xsl:if test="position()!=last()">
                <xsl:text disable-output-escaping="yes">&lt;/tr&gt;&#10;</xsl:text>
            </xsl:if>
        </xsl:for-each>
                
        <xsl:text disable-output-escaping="yes">&lt;/tr&gt;&#10;</xsl:text>                    

    </xsl:template>
    
</xsl:stylesheet>
