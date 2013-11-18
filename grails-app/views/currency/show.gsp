
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName" value="${message(code: 'currency.label', default: 'Currency')}" />
<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>
<body>
    <div class="page-header">
        <h1 class="inline">
            <g:message code="default.edit.label" args="[entityName]" />
        </h1>
        <div class="btn-group pull-right">
            <div class="input-append">
                <g:link action="list" class="btn btn-default">
                    <i class="icon-th-list"></i>
                </g:link>
                <button class="btn btn-default dropdown-toggle" data-toggle="dropdown">
                    <span class="caret"></span>
                </button>
                <ul class="dropdown-menu">
                    <li><g:link action="create">
                            <i class="icon-plus"></i> Create</g:link></li>
                </ul>
            </div>
        </div>
    </div>
    <dl class="dl-horizontal">

        <g:if test="${currencyInstance?.name}">
            <dt>
                <g:message code="currency.name.label" default="Name" />
            </dt>
            <dd>

                <g:fieldValue bean="${currencyInstance}" field="name" />

            </dd>
        </g:if>

        <g:if test="${currencyInstance?.code}">
            <dt>
                <g:message code="currency.code.label" default="Code" />
            </dt>
            <dd>

                <g:fieldValue bean="${currencyInstance}" field="code" />

            </dd>
        </g:if>

        <g:if test="${currencyInstance?.dateCreated}">
            <dt>
                <g:message code="currency.dateCreated.label" default="Date Created" />
            </dt>
            <dd>

                <g:formatDate date="${currencyInstance?.dateCreated}" />

            </dd>
        </g:if>

        <g:if test="${currencyInstance?.lastUpdated}">
            <dt>
                <g:message code="currency.lastUpdated.label" default="Last Updated" />
            </dt>
            <dd>

                <g:formatDate date="${currencyInstance?.lastUpdated}" />

            </dd>
        </g:if>

    </dl>
    <g:form>
        <fieldset class="form-actions">
            <g:hiddenField name="id" value="${currencyInstance?.id}" />
            <g:link class="btn btn-primary" action="edit" id="${currencyInstance?.id}">
                <g:message code="default.button.edit.label" default="Edit" />
            </g:link>
            <g:actionSubmit class="btn btn-danger" action="delete"
                value="${message(code: 'default.button.delete.label')}"
                onclick="return confirm('${message(code: 'default.button.delete.confirm.message')}');" />
        </fieldset>
    </g:form>
</body>
</html>