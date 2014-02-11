<div class="list-group-item" ng-repeat="userInstance in pagedUserList[currentPage]">
    <div class="row">
        <div class="col-sm-11">
            <ul class="list-inline">
                <li>
                    <h4 class="list-group-item-heading inline">
                        <g:checkBox name="userId" checked="false" 
                            ng-click="addOrRemoveSelectedUser()" ng-model="userInstance.selected"/>
                        <a href="/userProfile/show/{{userInstance.id}}">
                            <span ng-bind-html="userInstance.firstName | highlight:query"></span>
                            <span ng-bind-html="userInstance.lastName | highlight:query"></span>
                        </a>
                    </h4>
                </li>
                <li class="text-warning" rel="tooltip" title="Locked" ng-show="{{userInstance.accountLocked }}">
                    <i class="fa fa-exclamation-circle"></i>
                </li>
                <li class="text-danger" rel="tooltip" title="Disabled" ng-show="{{!userInstance.enabled }}">
                    <i class="fa fa-lock"></i>
                </li>
                <li>
                    <small>
                        <span class="label label-default assigned-role {{authority.replace('_', '-').toLowerCase() }}" 
                            ng-repeat="roleId in userInstance.roleIds">
                            {{roleId | roleName}}
                        </span>
                    </small>
                </li>
            </ul>
            <div class="list-group-item-text text-muted user-info">
                <ul class="list-inline">
                    <li><small><i class="fa fa-envelope"></i> &nbsp;<span ng-bind-html="userInstance.email | highlight:query"></span></small></li>
                    <li><small><i class="fa fa-user"></i> &nbsp;<span ng-bind-html="userInstance.username | highlight:query"></span></small></li>
                    <li><small><i class="fa fa-calendar"></i> &nbsp;{{userInstance.dateCreated | date:'MM/dd/yyyy'}}</small></li>
                </ul>
            </div>
        </div>
        <div class="col-sm-1">
            <g:link uri="/j_spring_security_switch_user?j_username={{userInstance.username }}"
                ng-hide="{{currentUserInstance.id  === userInstance.id }}"
                data-container="body" rel="tooltip" title="Use Causecode as {{userInstance.username }}" class="pull-right">
                <small><i class="fa fa-exchange"></i></small>
            </g:link>
        </div>
    </div>
</div>
<div ng-show="!pagedUserList[currentPage] || pagedUserList[currentPage].length == 0" class="list-group-item">
    <i class="fa fa-meh-o"></i> No matching records found.
</div>