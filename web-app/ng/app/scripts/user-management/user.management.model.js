/* global models, augment */

'use strict';

models.factory('UserManagementModel', ['BaseModel', function(BaseModel) {
    var AutoResponderModel = augment(BaseModel, function(uber) {
        var clazz;

        this.resourceName = 'userManagement';

        this.customActions = {
        };

        this.constructor = function(data) {
            clazz = uber.constructor.call(this, data);

            this.postConstruct();
            return clazz;
        };

        this.postConstruct = function() {
            
            clazz.prototype.toHTMLSubject = function() {
                return '<a href="#/user-management/edit/' + this.id + '">' + this.name + '</a>';
            };

            clazz.getClazzName = function() {
                return 'UserManagementModel';
            };

            clazz.getColumnNames = function() {
                return ['firstName', 'lastName', 'email', 'dateCreated', 'lastUpdated'];
            };

            clazz.getSortProperties = function() {
                return ['amount', 'expiresOnDate'];
            };
        };
    });

    // Returning the resource. This is not really a instance.
    return new AutoResponderModel({});
}]);