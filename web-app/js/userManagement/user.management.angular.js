var nucleusApp = angular.module("nucleus", ["ngCookies", "ngResource", "ngRoute"], 
  function($routeProvider, $locationProvider, $httpProvider) {});

nucleusApp.controller('UserManagementCtrl',['$scope', '$rootScope', '$resource', function ($scope, $rootScope, $resource) {
    var User = $resource("/userManagement/list?ajax=true");
    $scope.selectedUser = [];
    $scope.selectedRole = [];
    $scope.selectedRoleFilter = [];
    $scope.roleType= 'Any Granted';
    $scope.roleActionType = 'refresh';
    $scope.letter = '';
    $scope.query = '';

    User.get(null, function(data) {
        $scope.userInstanceList = data.userInstanceList;
        $scope.userInstanceTotal = data.userInstanceTotal;
        $scope.roleList = data.roleList;
        $scope.roleFilterList = data.roleList;
        $scope.currentUserInstance = data.currentUserInstance;
        $scope.max = data.params.max;
        $scope.sort = data.params.sort;
        $scope.order = data.params.order;
        $scope.offset = data.params.offset;
    })

    $scope.modifyRole = function(data) {
        var selectedUserIdList = $scope.getSelectedUserList();
        var selectedRoleIdList = $scope.getSelectedRoleList();
        var modifyRoles = $resource("/userManagement/modifyRoles?ajax=true");
        modifyRoles.get({userIds:selectedUserIdList, roles: selectedRoleIdList, roleActionType: $scope.roleActionType}, function(data){
            $("div#modify-role-overlay").modal("hide");
        })
    }

    $scope.addOrRemoveFromRoleFilter = function(roleId) {
        var index = $scope.selectedRoleFilter.indexOf(roleId);
        if(index > -1) {
            this.role.selected = false;
            $scope.selectedRoleFilter.splice(index, 1);
        } else {
            this.role.selected = true;
            $scope.selectedRoleFilter.push(roleId);
        }
        $scope.fetchAndDisplayList();
        return false;
    }

    $scope.setRoleType = function(roleType) {
        $scope.roleType = roleType;
        $scope.fetchAndDisplayList();
        return false;
    }

    $scope.selectAllUser = function() {
        angular.forEach($scope.userInstanceList, function(user) {
            if(user.selected) {
                user.selected = false;
            } else {
                user.selected = true;
            }
        });
        return false;
    }

    $scope.getSelectedUserList = function() {
        $scope.selectedUser = [];
        angular.forEach($scope.userInstanceList, function(user) {
            if(user.selected) {
                $scope.selectedUser.push(user.id);
            }
        });
        return $scope.selectedUser;
    }

    $scope.clearSelectedUsers = function() {
        angular.forEach($scope.userInstanceList, function(user) {
            user.selected = false;
        });
        $scope.selectedUser = [];
        return false;
    }
    
    $scope.clearSelectedletter = function() {
        $scope.letter = "";
        $scope.fetchAndDisplayList();
        return false;
    }
    
    $scope.clearSelectedAll = function() {
        $scope.query = "";
        $scope.letter = "";
        $scope.clearSelectedUsers();
        $scope.fetchAndDisplayList();
        return false;
    }

    $scope.getSelectedRoleList = function() {
        $scope.selectedRole = [];
        angular.forEach($scope.roleList, function(role) {
            if(role.selected) {
                $scope.selectedRole.push(role.id);
            }
        });
        return $scope.selectedRole
    }
    $scope.searchLetter = function(letter) {
        $scope.letter = letter;
        $scope.fetchAndDisplayList();
        return false
    }

    $scope.searchQuery = function(query) {
        $scope.query = query;
        $scope.fetchAndDisplayList();
        return false
    }

    $scope.fetchAndDisplayList = function() {
        var stateObj = {sort: $scope.sort, order: $scope.order, max: $scope.max, offset: $scope.offset, 
                roleFilter: $scope.selectedRoleFilter, roleType: $scope.roleType,
                letter: $scope.letter, query: $scope.query};

        User.get(stateObj, function(data) {
            $scope.userInstanceList = data.userInstanceList;
            $scope.userInstanceTotal = data.userInstanceTotal;
            $scope.roleList = data.roleList;
            $scope.currentUserInstance = data.currentUserInstance;
        })
    }

    $scope.userAction = function(action) {
        if (action.indexOf('null') == 0) {
            return false;
        }
        var selectedUserIdList = $scope.getSelectedUserList();
        if (selectedUserIdList.length == 0 ) {
            showAlertMessage('Please select at least one user at current page.');
            return false;
        }
        var confirmAction = confirm("Are you sure want to perform this action- " + action);
        if(!confirmAction)  return false;
        switch (action) {
        case 'Make user in-active':
            $scope.makeUserActiveInactive('false', selectedUserIdList);
            break;
        case 'Make user active':
            $scope.makeUserActiveInactive('true', selectedUserIdList);
            break;
        case 'Send bulk message':
            $scope.fetchEmails(selectedUserIdList);
            break;
        case 'Export email list':
            $scope.downloadEmails(selectedUserIdList);
            break;
        }
    }

    $scope.makeUserActiveInactive = function(type, selectedUserIdList) {
        showAlertMessage('Please wait ..', 'warning');
        var makeUserActiveInactive = $resource('/userManagement/makeUserActiveInactive');
        makeUserActiveInactive.get({type: type, selectedUser: selectedUserIdList}, function(data) {
            showAlertMessage(data.message, 'success');
        });
    }

    $scope.fetchEmails = function(selectedUserIdList) {
        var fetchEmails = $resource('/userManagement/fetchEmails?');
        fetchEmails.get({selectedUser: selectedUserIdList}, function(data) {
            if(data.emails) {
                $('textArea[name=selectedEmail]', '#send-bulk-msg-overlay').val(data.emails);
                $('#send-bulk-msg-overlay').modal('show');
            } else {
                showAlertMessage('Unable to fetch Message.', 'error');
            }
        })
    }

    $scope.sendMail = function() {
        $scope.selectedEmail = $('textArea[name=selectedEmail]', '#send-bulk-msg-overlay').val();
        $('#send-bulk-msg-overlay').modal('hide');
        showAlertMessage('Please wait, performing your request ..', 'warn', {timeout: 'clear'})
        var sendBulkEmail = $resource('/userManagement/sendBulkEmail');
        sendBulkEmail.get({selectedEmail: $scope.selectedEmail, body: $scope.body, subject: $scope.subject}, function(data) {
            if(data) {
                showAlertMessage(data.message, 'info');
            } else {
                showAlertMessage('Unable to Send Bulk Message.', 'error');
            }
        })
    }

    $scope.downloadEmails = function(selectedUserIdList) {
        var downloadEmails = $resource('/userManagement/downloadEmails?');
        downloadEmails.get({selectedUser: selectedUserIdList}, function() {
        })
    }
}]);