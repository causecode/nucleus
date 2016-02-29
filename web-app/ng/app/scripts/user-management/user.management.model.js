'use strict';

models.factory('UserManagementModel', ['BaseModel', function(BaseModel) {
    var AutoResponderModel = augment(BaseModel, function(uber) {
        var clazz;
        this.resourceName = 'userManagement';

        // Adding Custom actions except for those defined in $resource
        this.customActions = {
            makeUserActiveInactive: {
                url: '@/userManagement/action/makeUserActiveInactive',
                method : 'POST',
            },

            modifyRoles : {
                url: '@/userManagement/action/modifyRoles',
                method : 'POST',
            }
        };

        this.constructor = function(data) {
            clazz = uber.constructor.call(this, data);

            this.postConstruct();
            return clazz;
        };

        this.postConstruct = function() {

            clazz.getClazzName = function() {
                return 'UserManagementModel';
            };

            clazz.getColumnNames = function() {
                return ['firstName', 'lastName', 'email', 'dateCreated', 'lastUpdated'];
            };

            clazz.getSortProperties = function() {
                return ['dateCreated', 'firstName', 'email', 'lastName'];
            };
        };
    });
    // Returning the resource. This is not really an instance.
    return new AutoResponderModel({});
}]);
