<#-- Microsoft AutoRoute -->
<#-- Codecs: ASCII, UTF8 -->
<tmpl_par name="charset" value="ASCII">
<#-- somme chars should not appear in the cachename -->
<tmpl_par name="badchars" value=";"">
<#-- newline: CR, LF, CRLF -->
<tmpl_par name="newline" value="CRLF">
<#-- decimal seperator: . or , -->
<tmpl_par name="decsep" value=",">
Name;Breitengrad;Lšngengrad;Typ1;Typ2;Waypoint;Datum;Hyperlink<br />
<tmpl_loop cache_index>
"<tmpl_var name=SHORTTYPE>-<tmpl_var name=SHORTSIZE>-<tmpl_var name=DIFFICULTY>-<tmpl_var name=TERRAIN><tmpl_var name=NAME>,<tmpl_var name=LON>,<tmpl_var name=LAT>,,,,<br />
</tmpl_loop>
