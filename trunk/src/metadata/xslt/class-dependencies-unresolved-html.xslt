<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    <xsl:strip-space elements="*"/>
    <xsl:key name="distinct-classname" match="element[@type='class' or @type='interface' or @type='abstract']/dependency[@type='unresolved']" use="@value"/>
    <xsl:template match="report">
        <h3>Class Unresolved Dependencies</h3>
        <table width="95%" cellspacing="2" cellpadding="5" border="0" class="details">
            <th>Unresolved Class Name</th>
            <th>Dependent</th>
            <xsl:for-each
                select="//dependency[generate-id()=generate-id(key('distinct-classname',@value))]">
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
            </xsl:for-each>
        </table>
    </xsl:template>
</xsl:stylesheet>
