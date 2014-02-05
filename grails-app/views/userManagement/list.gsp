<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'user.label')}" />
    <title><g:message code="default.list.label" args="[entityName]" /> Management</title>
    <r:require module="userManagement" />
</head>
<body class="wide-screen user-list">
    <content tag="breadcrumb">
        <cc:breadcrumb map="['/dashboard': 'Causecode Dashboard', 'active': 'User Management']" />
    </content>
    <div class="page-header custom">
        <h1>
            <g:message code="user.management.label" default="User Management" />
        </h1>
    </div>
    <div class="row">
        <span class="col-sm-3">
            <strong><small>Total Users:</small></strong><small>${userInstanceTotal }</small><br>
            <g:if test="${session.selectedUser }">
                <span id="clear-selection">
                    <small>
                        <g:remoteLink action="clearSelection" onSuccess="clearSelectedUsers()" update="[success: 'clear-selection']"> 
                            Clear selected users</g:remoteLink>
                        <span title="Total selected user">
                            <abbr title="Selected user id's:
                                ${session.selectedUser.size() == 1 ? session.seletedUser : session.selectedUser.sort{ a,b -> a.toLong() <=> b.toLong()} }">
                                ${session.selectedUser.size() }</abbr>
                        </span>
                    </small>
                </span>
            </g:if>
        </span>
        <g:if test="${params.letter }">
            <span class="col-sm-3">
                <strong><small>Searched by letter:</small></strong><small>${params.letter }</small><br>
                <a href="${createLink(action: 'list', params: params+[clear: 'letter']) }"><small>Clear letter search</small></a>
            </span>
        </g:if>
        <g:if test="${params.letter || params.query || params.selectedUser || params._selectedUser }">
            <span class="col-sm-3"><a href="${createLink(action: 'list') }"><small>Reset search criteria</small></a></span>
        </g:if>
    </div>
    <div class="row" style="margin-bottom:10px;">
        <span class="col-sm-3">
            <g:select name="userAction" noSelection="${['null':'- User Action -']}" class="form-control"
                from="${['Make user in-active', 'Make user active', 'Send bulk message', 'Export email list'] }" />
        </span>
        <div class="col-sm-6">
            <div style="padding: 5px 24px">
                <g:each var="alphabet" in="${65..90}">
                    <small><a class="letter-sort"
                        href="${createLink(action:'list', params: [sort: params.sort, max: params.max, 
                            offset: params.offset, order: params.order, letter: (char)alphabet])}">
                        ${Character.toString((char)alphabet)}
                    </a></small>
                </g:each>
            </div>
        </div>
        <g:form name="user-search" action="list" class="form form-inline input-append">
            <div class="pull-right col-sm-3">
                <div class="input-group">
                    <g:textField name="query" value="${params.query}" autofocus="autofocus" class="form-control"/>
                    <span class="input-group-btn">
                        <a class="btn btn-default btn-sm" onclick="$('#user-search').submit();">
                            <i class="icon-search"></i></a>
                    </span>
                </div>
            </div>
        </g:form>
    </div>
    <div class="row">
        <div class="col-sm-3">
            <div class="btn-group btn-group-justified" data-toggle="buttons" style="margin-bottom: 10px;">
                <label class="btn btn-default btn-sm active">
                    <g:radio name="roleType" value="Any Granted" /><small>Any</small>
                </label>
                <label class="btn btn-default btn-sm">
                    <g:radio name="roleType" value="All Granted"/><small>All</small>
                </label>
                <label class="btn btn-default btn-sm">
                    <g:radio name="roleType" value="Only Granted"/><small>Only</small>
                </label>
                <label class="btn btn-default btn-sm disabled">
                    <g:radio name="dummy" value="" disabled="" /><small>Granted</small>
                </label>
            </div>
            <div class="list-group" id="role-filter">
                <g:set var="modifiedRoleList" value="${roleList.clone() }" />
                <g:set var="dummy" value="${modifiedRoleList.add([id: 0, authority: 'ROLE_NO_ROLE']) }" />

                <g:each in="${roleList }" var="roleInstance">
                    <a href="#" class="list-group-item" data-value="${roleInstance.id}">
                        <small>${roleInstance.authority.substring(5).replace('_', ' ') }</small>
                    </a>
                </g:each>
            </div>
        </div>
        <div class="col-sm-9">
            <ul class="list-inline">
                <li>
                    <div class="btn-group" id="sort-list">
                        <button type="button" class="btn btn-default dropdown-toggle btn-sm" data-toggle="dropdown">
                            <span class="text-muted">Sort:</span> <span class="value">Id</span> <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu" role="menu">
                            <li><a href="#" data-value="id">Id</a></li>
                            <li><a href="#" data-value="email">Email</a></li>
                            <li><a href="#" data-value="firstName">First Name</a></li>
                            <li><a href="#" data-value="lastName">Last Name</a></li>
                            <li><a href="#" data-value="accountLocked">Locked</a></li>
                            <li><a href="#" data-value="username">Username</a></li>
                        </ul>
                    </div>
                </li>
                <li>
                    <div class="btn-group" id="order-list">
                        <button type="button" class="btn btn-default dropdown-toggle btn-sm" data-toggle="dropdown">
                            <span class="text-muted">Order:</span> <span class="value">Ascending</span> <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu" role="menu">
                            <li><a href="#" data-value="asc">Ascending</a></li>
                            <li><a href="#" data-value="desc">Descending</a></li>
                        </ul>
                    </div>
                </li>
                <li class="pull-right">
                    <ul class="pagination pagination-sm" style="margin: 0">
                        <g:paginate total="${userInstanceTotal}" />
                    </ul>
                </li>
            </ul>
            <div class="panel panel-default">
                <div class="panel-heading">
                    <ul class="list-inline" style="margin-bottom: 0">
                        <li>
                            <g:checkBox name="dummy" class="check-uncheck" data-checkbox-name="userId" style="vertical-align: text-bottom;" />
                        </li>
                        <li>
                            <a href="#" class="btn btn-xs btn-default " id="modify-role">Modify Role</a>
                        </li>
                    </ul>
                </div>
                <div class="list-group" id="user-list-container">
                    <g:render template="/user/templates/userListing" />
                </div>
            </div>
            
        </div>
    </div>
    <g:render template="/user/templates/modifyRoleOverlay" />
    <r:script>
        var max = ${params.max },
            sort = "${params.sort }",
            order = "${params.order }",
            offset = ${params.offset },
            filterRoleList = new Array(),
            roleType = "Any Granted";
    </r:script>
    <i class="fa fa-spinner fa-spin" id="main-spinner" style="position: fixed;top: 14px;z-index: 10000;font-size: 22px;left: 50%;display: none;"></i>
</body>
</html>