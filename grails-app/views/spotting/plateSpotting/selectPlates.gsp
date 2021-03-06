<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <title><g:message code="select.plate.layouts"/></title>

    <r:script>$(function() {
        $("#accordion").accordion({
            collapsible:true,
            autoHeight: false,
            active: 1
        });

    });</r:script>
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
    <h1><g:message code="select.plate.layouts" /></h1>
    <g:if test="${message}"><div class="message" role="status">${message}</div></g:if>

    <div id="accordion" style="margin: 25px; width: 90%;">
        <h3><a href="#">Hints</a></h3>
        <div>
            <div class="message" role="status">${message?:"Select plate layouts by dragging them to the middle list."}</div>
            <div class="message">Order list elements by drag and drop, then press continue</div>
            <div class="message">Double click on a list element in order to open a preview window of the layout.</div>
            <div class="message">To remove elements from the selection drag them to the trash bin.</div>
            <div class="message">You can spot a plate layout multiple times. Just drag it to the selection list several times.</div>

        </div>
        <h3><a href="#">Select plates for new slide layout</a></h3>
        <div>

            <g:form>
                <g:hiddenField id="plateOrder" name="plateOrder" value=""/>

                <div class="buttons">
                    <g:submitButton name="refresh" value="Refresh plates"/>
                    <g:submitButton
                            value="Continue"
                            name="platesOrdered"
                            onclick="
                    var plateOrder = jQuery('#selectedPlates').sortable('serialize');
                    jQuery('#plateOrder').attr('value', plateOrder);
                "
                    />
                </div>
            </g:form>
            <div id="plateSelect">
                <g:render template="plateSorting"/>
            </div>
        </div>
    </div>

</div>

<r:script>
    $(document).ready(function() {
        var stickyTop = $('.stickable').offset().top;

        var sticky = function(){
            var scrollTop = $(window).scrollTop();

            if (scrollTop > stickyTop) {
                $('.stickable').addClass('sticky');
            } else {
                $('.stickable').removeClass('sticky');
            }
        };

        sticky();

        $(window).scroll(function() {
            sticky();
        });
    });
</r:script>
</body>
</html>
