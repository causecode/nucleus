<html>
<head>
    <meta name="layout" content="main">
    <title>User Management</title>
    <r:require modules="angular, userManagementAJ" />
</head>
<body class="wide-screen user-list">
    <div ng-app="nucleus" ng-controller="UserManagementCtrl">
        <div class="custom-header well">
            <h1>
                User Management
                &nbsp;<i class="fa fa-spinner fa-spin" ng-show="ajaxLoading"></i>
            </h1>
        </div>
        <ul class="list-inline">
            <li ng-if="selectedUser.length !== 0">
                <a class="btn btn-warning btn-xs" ng-click="clearSelectedUsers()">
                    <strong>Total <abbr title="{{getSelectedUserIdList().join(', ')}}">{{selectedUser.length}}</abbr> selected
                        <ng-pluralize count="selectedUser.length" when="{'1': 'user', 'other': 'users'}"></ng-pluralize>
                        <i class="fa fa-fw fa-times-circle"></i></strong>
                </a>
            </li>
            <li ng-if="letter">
                <a class="btn btn-warning btn-xs" ng-click="clearSelectedletter()">
                    <strong>Letter filter by {{letter}} <i class="fa fa-fw fa-times-circle"></i></strong>
                </a>
            </li>
            <li ng-if="query">
                <a class="btn btn-warning btn-xs" ng-click="clearQueryFilter()">
                    <strong>Reset query filter <i class="fa fa-fw fa-times-circle"></i></strong>
                </a>
            </li>
        </ul>
        <div class="row">
            <div class="col-sm-3">
                <div class="page-header" style="margin-top: 0">
                    <h2 style="margin: 0">Filters</h2>
                </div>
                <section id="role-filters">
                    <h3>Role</h3>
                    <div class="btn-group btn-group-justified" data-toggle="buttons" style="margin-bottom: 10px;">
                        <label class="btn btn-default btn-sm active" ng-click="setRoleType('Any Granted')">
                            <g:radio name="roleType" ng-model="roleType" value="Any Granted" /><small>Any</small>
                        </label>
                        <label class="btn btn-default btn-sm" ng-click="setRoleType('All Granted')">
                            <g:radio name="roleType" value="All Granted"/><small>All</small>
                        </label>
                        <label class="btn btn-default btn-sm" ng-click="setRoleType('Only Granted')">
                            <g:radio name="roleType" ng-model="roleType" value="Only Granted"/><small>Only</small>
                        </label>
                        <label class="btn btn-default btn-sm disabled">
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
                </section>

                <section id="query-filters">
                    <h3>Query</h3>
                    <form name="user-search" class="form-inline input-append" ng-submit="searchQuery()">
                        <div class="input-group">
                            <g:textField name="query" ng-model="query" class="form-control input-sm"/>
                            <span class="input-group-btn">
                                <button type="submit" class="btn btn-default btn-sm" ><i class="fa fa-search"></i></button>
                            </span>
                        </div>
                    </form>
                </section>

                <section id="sort-filters">
                    <h3>Sort</h3>
                    <select name="sortAction" class="form-control input-sm" ng-model="sort" ng-change="sortList()">
                        <option value="id">ID</option>
                        <option value="email">Email</option>
                        <option value="firstName">First Name</option>
                        <option value="lastName">Last Name</option>
                        <option value="accountLocked">Locked</option>
                        <option value="username">Username</option>
                    </select>
                </section>

                <section id="order-filters">
                    <h3>Order</h3>
                    <select name="orderAction" class="form-control input-sm" ng-model="order" ng-change="orderList()">
                        <option value="asc">Ascending</option>
                        <option value="desc">Descending</option>
                    </select>
                </section>
            </div>
            <div class="col-sm-9">
                <div class="btn-toolbar" role="toolbar" style="margin-bottom: 10px;">
                    <div class="btn-group">
                        <a class="btn btn-default btn-sm disabled" href="" disabled>
                            Letter Filter
                        </a>
                        <a class="btn btn-default btn-sm" ng-repeat="char in letterArray" 
                            ng-click="filterByLetter()" href="" ng-class="{active: char == letter}">
                            {{char}}
                        </a>
                    </div>
                </div>
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <ul class="list-inline" style="margin-bottom: 0">
                            <li>
                                <g:checkBox name="dummy"  ng-change="selectAllUser()" ng-model="userSelected"
                                    data-checkbox-name="userId" style="vertical-align: text-bottom;" />
                            </li>
                            <li>
                                <form name="userAction" class="form-inline" ng-submit="performUserAction()">
                                    <g:select name="userAction" noSelection="${['':'- User Action -']}" ng-model="action"
                                        class="form-control input-sm" ng-disabled="selectedUser.length == 0"
                                        from="${['Export User Report', 'Make user in-active', 'Make user active', 'Modify Role'] }" />
                                    <button type="submit" class="btn btn-default btn-sm"
                                        ng-disabled="selectedUser.length == 0 || action == ''">
                                        Go</button>
                                </form>
                            </li>
                        </ul>
                    </div>
                    <div class="list-group" id="user-list-container">
                        <g:render template="/userManagement/templates/userListingAJ" />
                    </div>
                </div>
                <pagination total-items="userInstanceTotal" page="currentPage" on-select-page="changePage(page)"
                    items-per-page="itemsPerPage" max-size="10"></pagination>
            </div>
        </div>
        <g:render template="/userManagement/templates/sendBulkMailOverlayAJ"></g:render>
        <g:render template="/userManagement/templates/modifyRoleOverlayAJ" />
    </div>
</body>
</html>