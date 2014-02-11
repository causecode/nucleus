var nucleusApp = angular.module('nucleus', ['ngCookies', 'ngSanitize', 'ngResource', 'ngRoute', 'ui.bootstrap'], 
        function($routeProvider, $locationProvider, $httpProvider) {});

nucleusApp.controller('UserManagementCtrl',['$scope', '$rootScope', '$resource', 'roleService',
    function ($scope, $rootScope, $resource, roleService) {
    var User = $resource('/userManagement/list?dbType=Mongo');

    $scope.ajaxLoading = false;
    $scope.selectedUser = [];
    $scope.selectedRole = [];
    $scope.selectedRoleFilter = [];
    $scope.roleType= 'Any Granted';
    $scope.roleActionType = 'refresh';
    $scope.letter = '';
    $scope.query = '';
    $scope.sort = 'id';
    $scope.order = 'asc';

    $scope.currentPage = 1;
    $scope.ajaxLoading = true;
    $scope.itemsPerPage = 10;
    $scope.pagedUserList = [];
    $scope.max = 10;

    $scope.fetchAndDisplayUserList = function(forPage) {
        var offset = forPage ? (forPage - 1) * $scope.max : 0;

        var stateObj = {sort: $scope.sort, order: $scope.order, max: $scope.max, offset: offset, 
                roleFilter: $scope.selectedRoleFilter, roleType: $scope.roleType,
                letter: $scope.letter, query: $scope.query};

        if(!forPage) {
            forPage = $scope.currentPage;
        }
        $scope.ajaxLoading = true;

        User.get(stateObj, function(data) {
            console.log(data.userInstanceList)
            $scope.userInstanceList = data.userInstanceList;
            $scope.userInstanceTotal = data.userInstanceTotal;
            $scope.currentUserInstance = data.currentUserInstance;

            $scope.pagedUserList[forPage] = data.userInstanceList;
            $scope.currentPage = forPage;
            $scope.ajaxLoading = false;
        })
    }

    $scope.changePage = function(toPage) {
        if(!$scope.pagedUserList[toPage]) {
            $scope.ajaxLoading = true;
            $scope.fetchAndDisplayUserList(toPage);
        } else {
            $scope.currentPage = toPage;
        }
    };

    $scope.modifyRole = function(data) {
        var selectedUserIdList = $scope.getSelectedUserList();
        var selectedRoleIdList = $scope.getSelectedRoleList();
        var modifyRoles = $resource('/userManagement/modifyRoles');
        modifyRoles.get({userIds:selectedUserIdList, roles: selectedRoleIdList, roleActionType: $scope.roleActionType}, function(data) {
            $('div#modify-role-overlay').modal('hide');
        })
    };

    $scope.addOrRemoveFromRoleFilter = function(roleId) {
        var index = $scope.selectedRoleFilter.indexOf(roleId);
        console.log(index, roleId)
        if(index > -1) {
            this.role.selected = false;
            $scope.selectedRoleFilter.splice(index, 1);
            console.log('dd')
        } else {
            this.role.selected = true;
            $scope.selectedRoleFilter.push(roleId);
        }
        console.log('dd js',$scope.selectedRoleFilter)
        $scope.fetchAndDisplayUserList();
    }

    $scope.addOrRemoveSelectedUser = function() {
        if(this.userInstance.selected) {
            var index = $scope.selectedUser.indexOf(this.userInstance.id);
            this.userInstance.selected = false;
            $scope.selectedUser.splice(index, 1);
        } else {
            this.userInstance.selected = true;
            $scope.selectedUser.push(this.userInstance.id);
        }
    }

    $scope.sortList = function(data) {
        $scope.fetchAndDisplayUserList($scope.currentPage);
    }
    $scope.orderList = function() {
        $scope.fetchAndDisplayUserList($scope.currentPage);
    }

    $scope.setRoleType = function(roleType) {
        $scope.roleType = roleType;
        $scope.fetchAndDisplayUserList();
    }

    $scope.selectAllUser = function() {
        angular.forEach($scope.userInstanceList, function(user) {
            if(user.selected) {
                user.selected = false;
            } else {
                user.selected = true;
            }
        });
    };

    $scope.clearSelectedUsers = function() {
        angular.forEach($scope.userInstanceList, function(user) {
            user.selected = false;
        });
        $scope.selectedUser = [];
    };

    $scope.clearSelectedletter = function() {
        $scope.letter = '';
        $scope.fetchAndDisplayUserList();
    };

    $scope.clearSelectedAll = function() {
        $scope.query = '';
        $scope.letter = '';
        $scope.clearSelectedUsers();
        $scope.fetchAndDisplayUserList();
    };

    $scope.getSelectedRoleList = function() {
        $scope.selectedRole = [];
        angular.forEach($scope.roleFilterList, function(role) {
            if(role.selected) {
                $scope.selectedRole.push(role.id);
            }
        });
        return $scope.selectedRole
    };

    $scope.getSelectedUserList = function() {
        var selectedUserId = [];
        angular.forEach($scope.selectedUser, function(user) {
            selectedUserId.push(user.id);
        });
        console.log(selectedUserId)
        return selectedUserId;
    };

    $scope.filterByLetter = function() {
        $scope.letter = this.char;
        $scope.fetchAndDisplayUserList();
    };

    $scope.searchQuery = function() {
        $scope.fetchAndDisplayUserList();
    };

    $scope.userAction = function(action) {
        if (action.indexOf('null') == 0) {
            return false;
        }
        var selectedUserIdList = $scope.selectedUser;
        if (selectedUserIdList.length == 0 ) {
            showAlertMessage('Please select at least one user at current page.');
            return false;
        }
        var confirmAction = confirm('Are you sure want to perform this action- ' + action);
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
    };

    $scope.makeUserActiveInactive = function(type, selectedUserIdList) {
        showAlertMessage('Please wait ..', 'warning');
        var makeUserActiveInactive = $resource('/userManagement/makeUserActiveInactive');
        makeUserActiveInactive.get({type: type, selectedUser: selectedUserIdList}, function(data) {
            showAlertMessage(data.message, 'success');
        });
    };

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
    };

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
    };

    $scope.downloadEmails = function(selectedUserIdList) {
        window.location.href = '/userManagement/downloadEmails?selectedUser='+selectedUserIdList.toString();
    };

    $scope.fetchAndDisplayUserList($scope.currentPage);

    $scope.letterArray = [];
    for(var i = 0; i < 26; i++) {
        $scope.letterArray.push(String.fromCharCode(65 + i));
    }

    roleService.getPromise().then(function(resp) {
        $scope.roleList = resp.data;
        $scope.roleFilterList = angular.copy(resp.data);
    });
}])
.filter("roleName", function(roleService) {
    return function(roleId) {
        return roleService.getSimplifiedName(roleId);
    }
})
.factory('roleService', function($http) {
    var roleList = [];
    var promise = $http.get('/userManagement/roleList')
    promise.then(function(response) {
        roleList = response.data;
    });

    function get(roleId) {
        var roleInstance;
        angular.forEach(roleList, function(role) {
            if(role.id === roleId) {
                roleInstance = role;
            }
        });
        return roleInstance;
    }

    return {
        get: function(roleId) {
            return get(roleId)
        },
        getPromise: function() {
            return promise;
        },
        getRoleList: function() {
            return roleList
        },
        getSimplifiedName: function(roleId) {
            return get(roleId).authority.substring(5).replace('_', ' ');
        }
    }
})
.filter('highlight', function () {
    return function(text, search, caseSensitive) {
        if(search || angular.isNumber(search)) {
            text = text.toString();
            search = search.toString();
            if (caseSensitive) {
                return text.split(search).join('<strong class="ui-match">' + search + '</strong>');
            } else {
                return text.replace(new RegExp(search, 'gi'), '<strong class="ui-match">$&</strong>');
            }
        } else {
            return text;
        }
    };
});