<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'currency.label')}" />
    <title><g:message code="default.list.label" args="[entityName]" /></title>
</head>
<body>
    <div class="page-header">
        <h1 class="inline">
            <g:message code="default.list.label" args="[entityName]" />
        </h1>
        <div class="pull-right col-lg-4">
            <g:form action="list" name="search">
                <div class="input-group">
                    <g:textField name="query" value="${params.query}" autofocus="" class="form-control"
                        placeholder="Search" />
                    <div class="input-group-btn">
                        <button type="submit" class="btn btn-default">
                            <i class="icon-search"></i>
                        </button>
                        <g:link action="create" class="btn btn-default">
                            <i class="icon-plus"></i>
                        </g:link>
                    </div>
                </div>
            </g:form>
        </div>
    </div>
    <table class="table table-bordered table-hover table-striped">
        <thead>
            <tr>
                <g:sortableColumn property="dateCreated" title="${message(code: 'currency.dateCreated.label', default: 'Date Created')}" />
                <g:sortableColumn property="lastUpdated" title="${message(code: 'currency.lastUpdated.label', default: 'Last Updated')}" />
                <g:sortableColumn property="code" title="${message(code: 'currency.code.label', default: 'Code')}" />
                <g:sortableColumn property="name" title="${message(code: 'currency.name.label', default: 'Name')}" />
            </tr>
        </thead>
        <tbody>
            <g:each in="${currencyInstanceList}" var="currencyInstance">
                <tr>
                    <td><g:link action="show" params="${[currencyInstance: currencyInstance]}" id="${currencyInstance.id}">${fieldValue(bean: currencyInstance, field: "dateCreated")}</g:link></td>
                    <td><g:formatDate date="${currencyInstance.lastUpdated}" /></td>
                    <td>${fieldValue(bean: currencyInstance, field: "code")}</td>
                    <td>${fieldValue(bean: currencyInstance, field: "name")}</td>
                </tr>
            </g:each>
            <g:if test="${!currencyInstanceList }">
                <tr>
                    <td colspan="4">
                        No record found. <g:link action="create">Create new</g:link>.
                    </td>
                </tr>
            </g:if>
        </tbody>
    </table>
    <ul class="pagination">
        <g:paginate total="${currencyInstanceTotal}" />
    </ul>
</body>
</html>