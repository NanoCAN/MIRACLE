<%@ page import="org.nanocan.rppa.layout.CellLine"%>
<a href="#list-cellLine" class="skip" tabindex="-1"><g:message
		code="default.link.skip.label" default="Skip to content&hellip;" /></a>
<div id="list-cellLine" class="content scaffold-list" role="main">
	<h1>
		List of CellLine's
	</h1>
	<g:if test="${flash.message}">
		<div class="message" role="status">
			${flash.message}
		</div>
	</g:if>
	<table>
		<thead>
			<tr>

				<g:sortableColumn property="name"
					title="${message(code: 'cellLine.name.label', default: 'Name')}" />

				<g:sortableColumn property="color"
					title="${message(code: 'cellLine.color.label', default: 'Color')}" />

			</tr>
		</thead>
		<tbody>
			<g:each in="${cellLineInstanceList}" status="i"
				var="cellLineInstance">
				<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

					<td><g:link action="show" id="${cellLineInstance.id}">
							${fieldValue(bean: cellLineInstance, field: "name")}
						</g:link></td>

					<td><div id="colorPickDiv"
							style="float:left; background-color: ${cellLineInstance?.color}; border: 1px solid; width:25px; height:25px;" /></td>

				</tr>
			</g:each>
		</tbody>
	</table>

	<div class="pagination">
		<g:paginate total="${cellLineInstanceTotal}" />
	</div>
</div>