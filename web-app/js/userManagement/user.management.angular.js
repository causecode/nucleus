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
    $scope.action = '';
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
            var selectedUserIdList = $scope.getSelectedUserIdList();
            angular.forEach(data.userInstanceList, function(userInstance) {
                if(selectedUserIdList.indexOf(userInstance.id) > -1) {
                    userInstance.selected = true;
                }
            });
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
        var selectedUserIdList = $scope.getSelectedUserIdList();
        var selectedRoleIdList = $scope.getSelectedRoleList();
        var modifyRoles = $resource('/userManagement/modifyRoles');

        $('div#modify-role-overlay').modal('hide');
        modifyRoles.get({userIds:selectedUserIdList, roleIds: selectedRoleIdList, roleActionType: $scope.roleActionType}, function(data) {
            showAlertMessage("Role modified successfully.", 'success');
            $scope.fetchAndDisplayUserList($scope.currentPage);
        })
    };

    $scope.addOrRemoveFromRoleFilter = function(roleId) {
        var index = $scope.selectedRoleFilter.indexOf(roleId);
        if(index > -1) {
            this.role.selected = false;
            $scope.selectedRoleFilter.splice(index, 1);
        } else {
            this.role.selected = true;
            $scope.selectedRoleFilter.push(roleId);
        }
        $scope.fetchAndDisplayUserList();
    }

    $scope.addOrRemoveSelectedUser = function() {
        var currentUser = this.userInstance;
        if(currentUser.selected) { // Reverse selection value. Means unselecting.
            var index = -1;
            angular.forEach($scope.selectedUser, function(selectedUser, i) {
                if(selectedUser.id === currentUser.id) {
                    index = i;
                }
            });
            $scope.selectedUser.splice(index, 1);
        } else {
            $scope.selectedUser.push(currentUser);
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
        var selectAll = this.selectUnselectAll;
        console.log(selectAll)
        angular.forEach($scope.userInstanceList, function(user) {
            if(selectAll) {
                user.selected = true;
            } else {
                user.selected = false;
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

    $scope.clearQueryFilter = function() {
        $scope.query = '';
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
        angular.forEach($scope.roleList, function(role) {
            if(role.selected) {
                $scope.selectedRole.push(role.id);
            }
        });
        return $scope.selectedRole
    };

    $scope.getSelectedUserIdList = function() {
        var selectedUserId = [];
        angular.forEach($scope.selectedUser, function(user) {
            selectedUserId.push(user.id);
        });
        return selectedUserId;
    };

    $scope.filterByLetter = function() {
        $scope.letter = this.char;
        $scope.fetchAndDisplayUserList();
    };

    $scope.searchQuery = function() {
        $scope.fetchAndDisplayUserList();
    };

    $scope.performUserAction = function() {
        var action = $scope.action;
        if(action === '') {
            return false;
        }
        $scope.action = '';
        var confirmAction = confirm('Are you sure want to perform this action- ' + action);
        if(!confirmAction)  return false;
        switch (action) {
            case 'Export User Report':
                $scope.exportUserReport();
                break;
            case 'Make user in-active':
                $scope.makeUserActiveInactive(false);
                break;
            case 'Make user active':
                $scope.makeUserActiveInactive(true);
                break;
            case 'Modify Role':
                $("#modify-role-overlay").modal("show");
                break;
        }
    };

    $scope.makeUserActiveInactive = function(activate) {
        showAlertMessage('Please wait ..', 'warn');
        var selectedUserIdList = $scope.getSelectedUserIdList();
        var makeUserActiveInactive = $resource('/userManagement/makeUserActiveInactive');

        makeUserActiveInactive.get({type: activate, selectedUser: selectedUserIdList}, function(data) {
            showAlertMessage(data.message, 'success');
            $scope.fetchAndDisplayUserList($scope.currentPage);
        });
    };

    $scope.exportUserReport = function() {
        var selectedUserIdList = $scope.getSelectedUserIdList();
        window.location.href = '/userManagement/exportUserReport?selectedUser=' + selectedUserIdList.toString();
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