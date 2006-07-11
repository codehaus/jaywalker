<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    <xsl:strip-space elements="*"/>
    <xsl:key name="arc" match="dependency[@type='resolved']/container[@type='package']"
        use="concat(../../@value,':',@value)"/>

    <xsl:template match="report">
        <h3>Package Resolved Dependencies</h3>
        <table width="95%" cellspacing="2" cellpadding="5" border="0" class="details">
            <tr>
            <th>Package</th>
            <th>Dependency</th>
            </tr>
            <xsl:choose>
                <xsl:when test="count(//container[generate-id()=generate-id(key('arc',concat(../../@value,':',@value)))]) = 0">
				    <tr><td colspan="2"><i>
				    <xsl:text>No Packages Found</xsl:text>
				    </i></td></tr>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates>
                        <xsl:sort select="../../@value"/>
                    </xsl:apply-templates>
                </xsl:otherwise>
            </xsl:choose>
        </table>
    </xsl:template>
    
    <xsl:template match="//container[generate-id()=generate-id(key('arc',concat(../../@value,':',@value)))]">

        <xsl:variable name="source">
            <xsl:choose>
                <xsl:when test="not(../../@value)">&lt;default&gt;</xsl:when>
                <xsl:otherwise>
                     <xsl:value-of select="../../@value"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:choose>
            <xsl:when test="not(preceding-sibling::node())">
                <xsl:text disable-output-escaping="yes">&#10;&lt;tr&gt;</xsl:text>
                <xsl:text disable-output-escaping="yes">&lt;td rowspan="</xsl:text>
                <xsl:value-of select="../@value"/>
                <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
                <xsl:value-of select="$source"/>
                <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text disable-output-escaping="yes">&#10;&lt;tr&gt;</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
   
        <xsl:variable name="target">
            <xsl:choose>
                <xsl:when test="not(@value) or string-length(@value) = 0">&lt;default&gt;</xsl:when>
                <xsl:otherwise>
                     <xsl:value-of select="@value"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
            <td>
                <xsl:value-of select="$target"/>
            </td>
   
        <xsl:text disable-output-escaping="yes">&lt;/tr&gt;&#10;</xsl:text>
   
    </xsl:template>

</xsl:stylesheet>
