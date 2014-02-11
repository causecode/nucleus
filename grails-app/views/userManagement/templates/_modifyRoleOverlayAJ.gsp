<div class="modal fade" id="modify-role-overlay">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Modify Role</h4>
            </div>
                <div class="modal-body">
                    <div class="row">
                        <div class="col-sm-6 role-error-container">
                            <strong>Roles</strong>
                            <div class="checkbox" ng-repeat="role in roleList">
                                <label>
                                    <g:checkBox name="roleIds" value="{{role.id }}" checked="false" 
                                        ng-model="role.selected" />
                                    {{role.authority.substring(5).replace('_', ' ') }}
                                </label>
                            </div>
                            <g:hiddenField name="userIds"/>
                        </div>
                        <div class="col-sm-6 action-error-container">
                            <strong>Action Type</strong>
                            <div class="radio">
                                <label>
                                    <g:radio name="roleActionType" ng-model="roleActionType" value="append" required="" 
                                        data-error-placement=".action-error-container" title="Please select any of the two" /> Grant these also
                                        <i class="fa fa-question-circle" rel="tooltip" title="Will add selected roles keeping existing roles" ></i>
                                </label>
                            </div>
                            <div class="radio">
                                <label>
                                    <g:radio name="roleActionType" ng-model="roleActionType" value="refresh" required="" /> Grant only these
                                    <i class="fa fa-question-circle" rel="tooltip" title="Will remove all roles & add selected roles" ></i>
                                </label>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" ng-click="modifyRole()">Modify</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
        </div>
    </div>
</div>