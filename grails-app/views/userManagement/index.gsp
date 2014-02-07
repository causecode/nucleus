<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'user.label')}" />
    <title><g:message code="default.list.label" args="[entityName]" /> Management</title>
    <r:require modules="angular, userManagementAJ" />
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
    <div ng-app="nucleus" ng-controller="UserManagementCtrl">
        <div class="row">
            <span class="col-sm-3">
                <span id="clear-selection" ng-show="selectedUser">
                    <small>
                        <a href="" ng-click="clearSelectedUsers()">Clear selected users</a>
                        <span title="Total selected user">
                            <abbr title="Selected user id's:">
                                <span ng-repeat="user in selectedUser" > {{user.id}}</span>
                                </abbr>
                        </span>
                    </small>
                </span>
            </span>
            <span class="col-sm-3" ng-show="letter">
                <strong><small>Searched by letter:</small></strong><small>{{letter }}</small><br>
                <a href="" ng-click="clearSelectedletter()"><small>Clear letter search</small></a>
            </span>
            <span class="col-sm-3" ng-show="query">
                <a href="" ng-click="clearSelectedAll()"><small>Reset search criteria</small></a>
            </span>
        </div>
        <div class="row" style="margin-bottom:10px;">
            <span class="col-sm-3">
                <g:select name="userAction" noSelection="${['null':'- User Action -']}" class="form-control"
                    ng-model="action" ng-change="userAction(action)"
                    from="${['Make user in-active', 'Make user active', 'Send bulk message', 'Export email list'] }" />
            </span>
            <div class="col-sm-6">
                <div style="padding: 5px 0px">
                    <g:each var="alphabet" in="${65..90}">
                        <small><a class="letter-sort" ng-click="searchLetter('${(char)alphabet}')" href="">
                            ${Character.toString((char)alphabet)}
                        </a></small>
                    </g:each>
                </div>
            </div>
            <g:form name="user-search" action="list" class="form form-inline input-append">
                <div class="pull-right col-sm-3">
                    <div class="input-group">
                        <g:textField name="query" ng-model="query" value="${params.query}" autofocus="autofocus" class="form-control"/>
                        <span class="input-group-btn" ng-click="searchQuery(query)">
                            <a class="btn btn-default" ><i class="fa fa-search"></i></a>
                        </span>
                    </div>
                </div>
            </g:form>
        </div>
        <div class="row">
            <div class="col-sm-3">
                <div class="btn-group btn-group-justified" data-toggle="buttons" style="margin-bottom: 10px;">
                    <label class="btn btn-default btn-sm active" ng-click="setRoleType('Any Granted')">
                        <g:radio name="roleType" ng-model="roleType" value="Any Granted" /><small>Any</small>
                    </label>
                    <label class="btn btn-default btn-sm" ng-click="setRoleType('All')">
                        <g:radio name="roleType" value="All"/><small>All</small>
                    </label>
                    <label class="btn btn-default btn-sm" ng-click="setRoleType('Only Granted')">
                        <g:radio name="roleType" ng-model="roleType" value="Only Granted"/><small>Only</small>
                    </label>
                    <label class="btn btn-default btn-sm disabled" ng-click="setRoleType('dummy')">
                        <g:radio name="dummy" ng-model="roleType" value="" disabled="" /><small>Granted</small>
                    </label>
                </div>
                <div class="list-group" id="role-filter">
                    <a href="#" class="list-group-item" ng-model="role.selected" value="{{role.id}}" 
                        ng-repeat="role in roleFilterList" ng-click="addOrRemoveFromRoleFilter(role.id)"
                        ng-class="{active: role.selected}">
                        <small>{{role.authority.substring(5).replace('_', ' ') }}</small>
                    </a>
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
                </ul>
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <ul class="list-inline" style="margin-bottom: 0">
                            <li>
                                <g:checkBox name="dummy"  ng-change="selectAllUser()" ng-model="userSelected"
                                    data-checkbox-name="userId" style="vertical-align: text-bottom;" />
                            </li>
                            <li>
                                <a href="#" class="btn btn-xs btn-default " id="modify-role"
                                    data-toggle="modal" data-target="#modify-role-overlay" >Modify Role</a>
                            </li>
                        </ul>
                    </div>
                    <div class="list-group" id="user-list-container">
                        <g:render template="/userManagement/templates/userListingAJ" />
                    </div>
                </div>
                
            </div>
        </div>
        <g:render template="/userManagement/templates/sendBulkMailOverlayAJ"></g:render>
        <g:render template="/userManagement/templates/modifyRoleOverlayAJ" />
        <i class="fa fa-spinner fa-spin" id="main-spinner" style="position: fixed;top: 14px;z-index: 10000;font-size: 22px;left: 50%;display: none;"></i>
        <div class="col-sm-offset-3">
             <pagination total-items="userInstanceTotal" page="currentPage" on-select-page="changePage(page)"
                    items-per-page="itemsPerPage" max-size="10"></pagination>
        </div>
    </div>
</body>
</html>