var nucleusApp = angular.module("nucleus", ["ngCookies", "ngResource", "ngRoute"], 
  function($routeProvider, $locationProvider, $httpProvider) {});

nucleusApp.controller('UserManagementCtrl',['$scope', '$rootScope', '$resource', function ($scope, $rootScope, $resource) {
    var User = $resource("/userManagement/list?ajax=true");
    $scope.selectedUser = [];
    $scope.selectedRole = [];
    $scope.selectedRoleFilter = [];
    $scope.roleType= 'Any Granted';
    $scope.roleActionType = 'refresh'
    $scope.letter = '';
    $scope.query = '';

    User.get(null, function(data) {
        console.log(data,'data');
        $scope.userInstanceList = data.userInstanceList;
        $scope.userInstanceTotal = data.userInstanceTotal;
        $scope.roleList = data.roleList;
        $scope.roleFilterList = data.roleList;
        $scope.currentUserInstance = data.currentUserInstance;
    })

    $scope.modifyRole = function(data) {
        var selectedUserIdList = $scope.getSelectedUserList();
        var selectedRoleIdList = $scope.getSelectedRoleList();
        var modifyRoles = $resource("/userManagement/modifyRoles?ajax=true");
        modifyRoles.get({userIds:selectedUserIdList, roles: selectedRoleIdList, roleActionType: $scope.roleActionType}, function(data){
            console.log('roles Modified successfully');
            $("div#modify-role-overlay").modal("hide");
        })
    }

    $scope.addOrRemoveFromRoleFilter = function(roleId) {
        var index = $scope.selectedRoleFilter.indexOf(roleId);
        if(index > -1) {
            //this.removeClass('active');
            $scope.selectedRoleFilter.splice(index, 1);
        } else {
            //this.addClass('active');
            $scope.selectedRoleFilter.push(roleId);
        }
        console.log(this)
        $scope.fetchAndDisplayList()
        return false;
    }

    $scope.setRoleType = function(roleType) {
        $scope.roleType = roleType
        $scope.fetchAndDisplayList();
        return false;
    }

    $scope.selectAllUser = function() {
        angular.forEach($scope.userInstanceList, function(user) {
            if(user.selected) {
                user.selected = false
            } else {
                user.selected = true
            }
        });
        console.log($scope.userInstanceList,'$scope.userInstanceList')
        return false;
    }

    $scope.getSelectedUserList = function() {
        $scope.selectedUser = [];
        angular.forEach($scope.userInstanceList, function(user) {
            if(user.selected) {
                $scope.selectedUser.push(user.id);
            }
        });
        return $scope.selectedUser
    }

    $scope.getSelectedRoleList = function() {
        console.log('role select');
        $scope.selectedRole = [];
        angular.forEach($scope.roleList, function(role) {
            if(role.selected) {
                console.log('role select',role);
                $scope.selectedRole.push(role.id);
            }
        });
        return $scope.selectedRole
    }
    $scope.searchLetter = function(letter) {
        console.log('search', letter);
        $scope.letter = letter;
        $scope.fetchAndDisplayList();
        return false
    }

    $scope.searchQuery = function(query) {
        console.log('search', query);
        $scope.query = query;
        $scope.fetchAndDisplayList();
        return false
    }

    $scope.fetchAndDisplayList = function() {
        console.log('fetchAndDisplayList');
        var stateObj = {sort: sort, order: order, max: max, offset: offset, roleFilter: $scope.selectedRoleFilter, roleType: $scope.roleType,
                letter: $scope.letter, query: $scope.query};

        User.get(stateObj, function(data) {
            console.log(data,'data');
            $scope.userInstanceList = data.userInstanceList;
            $scope.userInstanceTotal = data.userInstanceTotal;
            $scope.roleList = data.roleList;
            $scope.currentUserInstance = data.currentUserInstance;
        })
    }

    $scope.userAction = function(action) {
        console.log('user-action',action)
        if (action.indexOf('null') == 0)
            return false;
        if ($scope.selectedUser) {
            showAlertMessage('Please select at least one user at current page.');
            return false
        }
        var confirmAction = confirm("Are you sure want to perform this action- " + action);
        if(!confirmAction)  return false;
        switch (action) {
        case 'Make user in-active':
            $scope.makeUserActiveInactive('false');
            break;
        case 'Make user active':
            $scope.makeUserActiveInactive('true');
            break;
        case 'Send bulk message':
            $scope.fetchEmails();
            break;
        case 'Export email list':
            $scope.downloadEmails();
            break;
    }
    }

    $scope.makeUserActiveInactive = function(type) {
        showAlertMessage('Please wait ..', 'warning')
        var makeUserActiveInactive = $resource('/userManagement/makeUserActiveInactive?type='+type)
        makeUserActiveInactive.get(null, function(data, textStatus) {
            showAlertMessage(data, 'success')
        })
    }

    $scope.fetchEmails = function() {
        var fetchEmails = $resource('/userManagement/fetchEmails?')
        fetchEmails.get(null, function() {
            
        })
    }

    $scope.downloadEmails = function() {
        var downloadEmails = $resource('/userManagement/downloadEmails?')
        downloadEmails.get(null, function() {
            
        })
    }

}]);