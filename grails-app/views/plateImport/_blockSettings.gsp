<li class="fieldcontain">
<span class="property-label">

<g:if test="${rowWise}">
    Number of layout columns per block
    <g:set var="variableName" value="columnsPerBlock"/>
</g:if>
<g:else>
    Number of layout rows per block:
    <g:set var="variableName" value="rowsPerBlock"/>
</g:else>
</span>
<span class="property-value">
    This is not the number of actually spotted rows or columns, but rather this number divided by the number of depositions, e.g.
    12 columns with deposition pattern [4,4,2,2,1,1] result in 2 layout columns.<br/><br/>
    <g:field type="number" name="${variableName}" min="1" max="48" value="2"/>
</span>
</li>


