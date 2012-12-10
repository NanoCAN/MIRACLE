<%@ page import="org.nanocan.rppa.layout.PlateLayout; org.nanocan.rppa.layout.NumberOfCellsSeeded; org.nanocan.rppa.rnai.Sample; org.nanocan.rppa.layout.Treatment; org.nanocan.rppa.layout.Inducer; org.nanocan.rppa.layout.CellLine" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <title><g:message code="match.savanah.properties" /></title>

    <r:script>$(function() {
        $("#accordion").accordion({
            collapsible:true,
            autoHeight: false
        });
    });
    </r:script>
</head>
<body>
    <a href="#show-plateImport" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
    <div class="navbar">
        <div class="navbar-inner">
            <div class="container">
                <ul class="nav">
                    <g:render template="/templates/navmenu"></g:render>
                </ul>
            </div>
        </div>
    </div>
<div id="show-plateImport" class="content scaffold-show" role="main">
    <h1><g:message code="match.savanah.properties" /></h1>
    <div class="message" role="status">${message?:"Assign SAVANAH properties to MIRACLE properties or create new ones."}</div>
    <g:set var="backParams" value="${params.toQueryString()}"/>
    <g:set var="backController" value="spotting"/>
    <g:set var="backAction" value = "plateLayoutSpotting"/>

    <g:set var="importAction" value="spotterSettings"/>

    <g:form>

    <div id="accordion" style="margin: 25px; width: 90%;">
        <g:if test="${cellLineList.size() > 0}">
        <h3><a href="#">CellLine</a></h3>
        <div>
            <ol class="property-list">
                <g:each in="${cellLineList}" var="cellLine">
                    <li class="fieldcontain">
                    <span class="property-label">${cellLine}:</span>
                    <span class="property-value"><g:select from="${CellLine.list()}" value="${params.get("cellline_"+cellLine.name)?CellLine.findByName(params.get("cellline_"+cellLine.name)):CellLine.findByNameLike(cellLine.name)}" name="cellline_${cellLine}"/>
                        or <g:link controller="cellLine" action="create"
                                   params="${[name: cellLine.name, color: cellLine.color, backParams: backParams, backController: backController, backAction: backAction]}">
                    create</g:link></span>
                    </li>
                </g:each>
            </ol>
        </div>
        </g:if>
        <g:if test="${inducerList.size() > 0}">
        <h3><a href="#">Inducer</a></h3>
        <div>
            <ol class="property-list">
                <g:each in="${inducerList}" var="inducer">
                    <li class="fieldcontain">
                        <span class="property-label">${inducer}:</span>
                        <span class="property-value"><g:select from="${Inducer.list()}" value="${params.get("inducer_"+inducer.name)?Inducer.findByName(params.get("inducer_"+inducer.name)):Inducer.findByNameLike(inducer.name)}" name="inducer_${inducer}"/>
                            or <g:link controller="inducer" action="create"
                                       params="${[name: inducer.name, color: inducer.color, backParams: backParams, backController: backController, backAction: backAction]}">
                            create</g:link></span>
                    </li>
                </g:each>
            </ol>
        </div>
        </g:if>
        <g:if test="${numberOfCellsSeededList.size() > 0}">
        <h3><a href="#">NumberOfCellsSeeded</a></h3>
        <div>
            <ol class="property-list">
                <g:each in="${numberOfCellsSeededList}" var="numberOfCellsSeeded">
                    <li class="fieldcontain">
                        <span class="property-label">${numberOfCellsSeeded}:</span>
                        <span class="property-value"><g:select from="${NumberOfCellsSeeded.list()}" value="${params.get("numberOfCellsSeeded_"+numberOfCellsSeeded)?NumberOfCellsSeeded.findByName(params.get("numberOfCellsSeeded_"+numberOfCellsSeeded.name)):NumberOfCellsSeeded.findByNameLike(numberOfCellsSeeded.name)}" name="numberOfCellsSeeded_${numberOfCellsSeeded}"/>
                            or <g:link controller="numberOfCellsSeeded" action="create"
                                       params="${[name: numberOfCellsSeeded.name, color: numberOfCellsSeeded.color, backParams: backParams, backController: backController, backAction: backAction]}">
                            create</g:link></span>
                    </li>
                </g:each>
            </ol>
        </div>
        </g:if>
        <g:if test="${sampleList.size() > 0}">
        <h3><a href="#">Sample</a></h3>
        <div>
            <ol class="property-list">
                <g:each in="${sampleList}" var="sample">
                    <li class="fieldcontain">
                        <span class="property-label">${sample}:</span>
                        <span class="property-value"><g:select from="${Sample.list()}" value="${params.get("sample_"+sample.name)?Sample.findByName(params.get("sample_"+sample.name)):Sample.findByNameLike(sample.name)}" name="sample_${sample}"/>
                            or <g:link controller="sample" action="create"
                                       params="${[name: sample.name, color: sample.color, backParams: backParams, backController: backController, backAction: backAction]}">
                            create</g:link></span>
                    </li>
                </g:each>
            </ol>
        </div>
        </g:if>
        <g:if test="${treatmentList.size() > 0}">
        <h3><a href="#">Treatment</a></h3>
        <div>
            <ol class="property-list">
                <g:each in="${treatmentList}" var="treatment">
                    <li class="fieldcontain">
                        <span class="property-label">${treatment}:</span>
                        <span class="property-value"><g:select from="${Treatment.list()}" value="${params.get("treatment_"+treatment.name)?Treatment.findByName(params.get("treatment_"+treatment.name)):Treatment.findByNameLike(treatment.name)}" name="treatment_${treatment}"/>
                            or <g:link controller="treatment" action="create"
                                       params="${[name: treatment.name, color: treatment.color, backParams: backParams, backController: backController, backAction: backAction]}">
                            create</g:link></span>
                    </li>
                </g:each>
            </ol>
        </div>
        </g:if>
    </div>
    <div class="buttons">
        <g:submitButton name="continue" value="Continue"/>
    </div>
    </g:form>
</div>
</body>
</html>