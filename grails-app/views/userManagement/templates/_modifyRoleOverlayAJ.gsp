<div class="modal fade" id="modify-role-overlay">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Modify Role</h4>
            </div>
            <g:form class="form-horizontal jquery-form has-error">
                <div class="modal-body">
                    <div class="row">
                        <div class="col-sm-6 role-error-container">
                            <strong>Role</strong>
                            <g:each in="${com.cc.user.Role.list([sort: 'authority']) }">
                                <div class="checkbox">
                                    <label>
                                        <g:checkBox name="roleIds" value="${it.id }" checked="false" 
                                            data-error-placement="" title="Please select atleast one role" />
                                        ${it.authority.substring(5).replace('_', ' ') }
                                    </label>
                                </div>
                            </g:each>
                            
                            <g:hiddenField name="userIds"/>
                        </div>
                        <div class="col-sm-6 action-error-container">
                            <strong>Action Type</strong>
                            <div class="radio">
                                <label>
                                    <g:radio name="roleActionType" value="append" required="" 
                                        data-error-placement=".action-error-container" title="Please select any of the two" /> Grant these also
                                        <i class="fa fa-question-circle" rel="tooltip" title="Will add selected roles keeping existing roles" ></i>
                                </label>
                            </div>
                            <div class="radio">
                                <label>
                                    <g:radio name="roleActionType" value="refresh" required="" /> Grant only these
                                    <i class="fa fa-question-circle" rel="tooltip" title="Will remove all roles & add selected roles" ></i>
                                </label>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary">Modify</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </g:form>
        </div>
    </div>
</div>