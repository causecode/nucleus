'use strict';
var nucleusApp = angular.module('nucleus', ['ngCookies', 'ngSanitize', 'ngResource', 'ui.router','ui.bootstrap','ngcore']);

nucleusApp.controller('UserManagementController', ['$scope', '$modal', '$state', 'UserManagementModel', '$resource', 'appService',
    function($scope, $modal, $state, UserManagementModel, $resource, appService) {
    var User = $resource('/api/userManagement/action/index');
    $scope.selectedRoleFilter = [];
    $scope.responseCallback = 'onPagedListResponse';

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

    $scope.getSelectedUserIdList = function() {
        var selectedUserId = [];
        angular.forEach($scope.selectedUser, function(user) {
            selectedUserId.push(user.id);
        });
        return selectedUserId;
    };
    
    $scope.addOrRemoveSelectedUser = function() {
        var currentUser = this.userInstance;
        if(currentUser.selected) { // Reverse selection value. Means un-selecting.
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

    $scope.onPagedListResponse = function(data) {
    	$scope.roleList = data.roleList;
    };

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

    $scope.filterByLetter = function() {
        $scope.letter = this.char;
        $scope.fetchAndDisplayUserList();
    };

    $scope.searchQuery = function() {
        $scope.fetchAndDisplayUserList();
    };

    $scope.userActions = {
            export : 'Export User Report',
            makeUserActive : 'Make user active',
            makeUserInactive : 'Make user in-active',
            openModifyOverlay : 'Modify Role'
    }

    $scope.makeUserActive= function(params) {
        params.type = true;
        $scope.makeUserActiveInactive(params);
    }

    $scope.makeUserInactive= function(params) {
        params.type = false;
        $scope.makeUserActiveInactive(params);
    }

    $scope.makeUserActiveInactive = function(params) {
        UserManagementModel.makeUserActiveInactive(params, function(data) {
            appService.alert(data.message, 'success');
        });
    };

    // Modal for Modifying User Roles

    var modalInstance;
    $scope.overlay = {};
    
    $scope.openModal = function () {

    	modalInstance = $modal.open({
          templateUrl: 'modifyModal.html',	// Small HTML code to be rendered
          size: 'md',
          backdrop: 'static', 
          scope: $scope
        });

        modalInstance.result.then(function (selectedItem) {
            $scope.selected = selectedItem;
          }, function () {
            console.log('Modal dismissed at: ' + new Date());
          });
    };
    
    $scope.cancel = function () {
        modalInstance.dismiss('cancel');
      };
      
    $scope.openModifyOverlay = function(params) {
        $scope.openModal();	//Opens a Modal Page for Modification of User Roles
        console.log('params',params);
        $scope.selectedIdsfromParams = params.selectedIds;
    }
    
    $scope.modifyRoles = function() {
    	 $scope.cancel();	//Closes the Modal: Modal.hide()
    	 UserManagementModel.modifyRoles({userIds: $scope.selectedIdsfromParams, roleIds: $scope.overlay.assignableRoles , roleActionType: $scope.overlay.roleActionType}, function(data){
             appService.alert(data.message, 'success');
         })
     };
    
    //Pushing A to Z
    $scope.letterArray = [];
    for(var i = 0; i < 26; i++) {
        $scope.letterArray.push(String.fromCharCode(65 + i));
    }

}])
