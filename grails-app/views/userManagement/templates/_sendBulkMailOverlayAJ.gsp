<div id="send-bulk-msg-overlay" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <a href="#" class="close" data-dismiss="modal">&times;</a>
                <h3>Send Bulk Message</h3>
            </div>
            <g:form name="send-bulk-msg-form" class="jquery-form form-horizontal">
                <div class="modal-body">
                    <div class="form-group">
                        <label class="control-label col-sm-2" for="subject">
                            Email
                        </label>
                        <div class="col-sm-10">
                            <g:textArea name="selectedEmail" ng-model="selectedEmail" class="required uniqueInList emailList form-control"></g:textArea>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="control-label col-sm-2" for="subject">Subject</label>
                        <div class="col-sm-10">
                            <g:textField required="required" class="required form-control" autofocus="autofocus"
                                title="Subject is a required field." ng-model="subject" name="subject" />
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="control-label col-sm-2" for="body">Body</label>
                        <div class="col-sm-10">
                            <g:textArea name="body" ng-model="body" class="required form-control" style="height:200px;"/>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <a class="btn btn-primary" href="javascript:void(0)" id="send-bulk-msg" ng-click="sendMail()">Send Message</a>
                    <a class="btn btn-default" href="#" data-dismiss="modal">Cancel</a>
                </div>
            </g:form>
        </div>
    </div>
</div>