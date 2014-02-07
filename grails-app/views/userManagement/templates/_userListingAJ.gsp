<div class="list-group-item" ng-repeat="userInstance in userInstanceList">
    <div class="row">
        <div class="col-sm-11">
            <ul class="list-inline">
                <li>
                    <h4 class="list-group-item-heading inline">
                        <g:checkBox name="userId" checked="false" ng-model="userInstance.selected" ng-checked="userSelected" />
                        <a href="/userProfile/show/{{userInstance.id}}">{{userInstance.firstName }}&nbsp;{{userInstance.lastName }}</a>
                    </h4>
                </li>
                <g:if test="{{userInstance.accountLocked }}">
                    <li class="text-warning" rel="tooltip" title="Locked"><i class="fa fa-exclamation-circle"></i></li>
                </g:if>
                <g:if test="{{!userInstance.enabled }}">
                    <li class="text-danger" rel="tooltip" title="Disabled"><i class="fa fa-lock"></i></li>
                </g:if>
                <li>
                    <small>
                        <span class="label label-default assigned-role {{authority.replace('_', '-').toLowerCase() }}" 
                            ng-repeat="authority in userInstance.authorities">
                            {{authority.substring(5).replace('_', ' ') }}
                        </span>
                    </small>
                </li>
            </ul>
            <div class="list-group-item-text text-muted user-info">
                <ul class="list-inline">
                    <li><small><i class="fa fa-envelope"></i> &nbsp;{{userInstance.email }}</small></li>
                    <li><small><i class="fa fa-user"></i> &nbsp;{{userInstance.username }}</small></li>
                    <li><small><i class="fa fa-calendar"></i> &nbsp;{{userInstance.dateCreated | date:'MM/dd/yyyy'}}</small></li>
                </ul>
            </div>
        </div>
        <div class="col-sm-1">
            <g:if test="{{currentUserInstance.id}} != {{userInstance.id }}">
                <g:link uri="/j_spring_security_switch_user?j_username={{userInstance.username }}"
                    data-container="body" rel="tooltip" title="Use Causecode as {{userInstance.username }}" class="pull-right">
                    <small><i class="fa fa-exchange"></i></small>
                </g:link>
            </g:if>
        </div>
    </div>
</div>
<div ng-hide="{{!userInstanceList}}" class="list-group-item">
    <i class="fa fa-meh-o"></i> No matching records found.
</div>
