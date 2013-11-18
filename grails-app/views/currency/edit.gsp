<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName" value="${message(code: 'currency.label')}" />
<title><g:message code="default.edit.label" args="[entityName]" /></title>
</head>
<body>
    <div class="page-header">
        <h1>
            <g:message code="default.edit.label" args="[entityName]" />
        </h1>
    </div>
    <g:form class="form-horizontal">
        <g:hiddenField name="id" value="${currencyInstance?.id}" />
        <g:hiddenField name="version" value="${currencyInstance?.version}" />
        <g:render template="form" />
        <fieldset class="form-actions">
            <g:actionSubmit class="btn btn-primary" action="update"
                value="${message(code: 'default.button.update.label')}" />
            <g:actionSubmit class="btn btn-danger" action="delete"
                value="${message(code: 'default.button.delete.label')}" formnovalidate=""
                onclick="return confirm('${message(code: 'default.button.delete.confirm.message')}');" />
        </fieldset>
    </g:form>
</body>
</html>
