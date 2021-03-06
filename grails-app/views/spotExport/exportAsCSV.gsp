<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'sheet.label', default: 'Sheet')}" />
    <title><g:message code="default.edit.label" args="[entityName]" /></title>

    <r:script>$(function() {
        $("#accordion").accordion({
            collapsible:true,
            autoHeight: false
        });

    });</r:script>

</head>
<body>

<div class="navbar">
    <div class="navbar-inner">
        <div class="container">
            <ul class="nav">
                 <g:render template="/templates/navmenu"></g:render>
               <li><g:link class="list" controller="slide" action="show" id="${slideInstanceId}">Back to slide</g:link></li>
            </ul>
        </div>
    </div>
</div>

<div class="content">
    <h1>Export Spots to CSV</h1>

    <div id="formDiv">
        <g:form name="exportToCSVform" controller="spotExport">
            <g:hiddenField name="id" value="${slideInstanceId}"></g:hiddenField>
        <div id="accordion" style="margin: 25px; width: 90%;">
            <h3><a href="#">Export settings</a></h3>
            <div>
                <ol class="property-list">
                    <li class="fieldcontain">
                        <span class="property-label">Select a column separator: </span>
                        <span class="property-value"><g:select name="separator" optionKey="key" optionValue="value" value="tab" from="${separatorMap}"/>
                        </span>
                    </li>
                    <li class="fieldcontain">
                        <span class="property-label">Decimal separator</span>
                        <span class="property-value"><g:select name="decimalSeparator" value="." from="${[',', '.']}"/></span>
                    </li>
                    <li class="fieldcontain">
                        <span class="property-label">Decimal precision</span>
                        <span class="property-value"><g:select name="decimalPrecision" value="3" from="${1..10}"/></span>
                    </li>

                    <li class="fieldcontain">
                        <span class="property-label">Include block shifts (columns hshift and vshift)</span>
                        <span class="property-value"><g:checkBox name="includeBlockShifts" value="${true}"/></span>
                    </li>
                </ol>
            </div>
        </div>


            <fieldset class="buttons">
                <g:actionSubmit onclick="outsideFunction();" value="Export and download" action="processExport"/>
            </fieldset>
        </g:form>
    </div>
</div>
</body>
</html>