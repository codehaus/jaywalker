<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                              xmlns:func="http://exslt.org/functions"
                              xmlns:jw="http://jaywalker.codehaus.org"
                              extension-element-prefixes="func">

    <func:function name="jw:tooltip">
        <xsl:param name="title"/>
        <xsl:param name="tip"/>
        <xsl:variable name="apos">'</xsl:variable>
        <xsl:variable name="tooltip-begin" select="concat('&lt;a href=&quot;#&quot; onmouseover=&quot;this.style.color = ', $apos, '#000000', $apos, '; domTT_activate(this, event, ', $apos, 'content', $apos, ', ', $apos)"/>
        <xsl:variable name="tooltip-delay" select="concat($apos, 'delay', $apos, ', 1000')" />
        <xsl:variable name="tooltip-trail" select="concat($apos, 'trail', $apos, ', true')" />
        <xsl:variable name="tooltip-fade" select="concat($apos, 'fade', $apos, ', ', $apos, 'both', $apos)" />
        <xsl:variable name="tooltip-fade-max" select="concat($apos, 'fadeMax', $apos, ', 87')" />
        <xsl:variable name="tooltip-style-class" select="concat($apos, 'styleClass', $apos, ', ', $apos, 'niceTitle', $apos)" />
        <xsl:variable name="tooltip-onmouseout" select="concat('onmouseout=&quot;this.style.color = ', $apos, $apos, '; domTT_mouseout(this, event);&quot;&gt;')" />
        <xsl:variable name="tooltip-mid" select="concat($apos, ', ', $tooltip-delay, ', ', $tooltip-trail, ', ', $tooltip-fade, ', ', $tooltip-fade-max, ', ', $tooltip-style-class, ');&quot; ', $tooltip-onmouseout )" />
        <xsl:variable name="tooltip-end" select="'&lt;/a&gt;'" />
        <func:result select="concat($tooltip-begin, $tip, $tooltip-mid, $title, $tooltip-end)"/>
    </func:function>
    
</xsl:stylesheet>