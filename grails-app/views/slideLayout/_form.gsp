<%@ page import="org.nanocan.layout.SlideLayout" %>

<div class="fieldcontain ${hasErrors(bean: slideLayoutInstance, field: 'title', 'error')} ">
	<label for="title">
		<g:message code="slideLayout.title.label" default="Title" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="title" minSize="1" required="" value="${slideLayoutInstance?.title}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: slideLayoutInstance, field: 'columnsPerBlock', 'error')} required">
	<label for="columnsPerBlock">
		<g:message code="slideLayout.columnsPerBlock.label" default="Columns Per Block" />
		<span class="required-indicator">*</span>
	</label>
	<g:field type="number" name="columnsPerBlock" min="1" required="" value="${fieldValue(bean: slideLayoutInstance, field: 'columnsPerBlock')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: slideLayoutInstance, field: 'rowsPerBlock', 'error')} required">
	<label for="rowsPerBlock">
		<g:message code="slideLayout.rowsPerBlock.label" default="Rows Per Block" />
		<span class="required-indicator">*</span>
	</label>
	<g:field type="number" name="rowsPerBlock" min="1" required="" value="${fieldValue(bean: slideLayoutInstance, field: 'rowsPerBlock')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: slideLayoutInstance, field: 'numberOfBlocks', 'error')} required">
	<label for="numberOfBlocks">
		<g:message code="slideLayout.numberOfBlocks.label" default="Number Of Blocks" />
		<span class="required-indicator">*</span>
	</label>
	<g:field type="number" name="numberOfBlocks" min="1" required="" value="${fieldValue(bean: slideLayoutInstance, field: 'numberOfBlocks')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: slideLayoutInstance, field: 'blocksPerRow', 'error')} required">
    <label for="blocksPerRow">
        <g:message code="slideLayout.numberOfBlocks.label" default="Blocks Per Row" />
        <span class="required-indicator">*</span>
    </label>
    <g:field type="number" name="blocksPerRow" min="1" required="" value="${fieldValue(bean: slideLayoutInstance, field: 'blocksPerRow')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: slideLayoutInstance, field: 'depositionPattern', 'error')} ">
	<label for="depositionPattern">
		<g:message code="slideLayout.depositionPattern.label" default="Deposition Pattern" />
        <span class="required-indicator">*</span>
	</label>
	<g:textField name="depositionPattern" minSize="1" required="" value="${slideLayoutInstance?.depositionPattern?:'[4,4,2,2,1,1]'}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: slideLayoutInstance, field: 'depositionDirection', 'error')} ">
    <label for="depositionDirection">
        <g:message code="slideLayout.depositionPattern.label" default="depositionDirection" />
        <span class="required-indicator">*</span>
    </label>
    <g:select name="depositionDirection" required="" from="['row-wise', 'column-wise']" value="${slideLayoutInstance?.depositionDirection?:'row-wise'}"/>
</div>

<div class="fieldcontain">
    <label for="experiments">
        <g:message code="slideLayout.experiments.label" default="Experiments" />
    </label>
    <g:select name="experimentsSelected" multiple="${true}" size="10" from="${experiments}" optionKey="id"/>
</div>

