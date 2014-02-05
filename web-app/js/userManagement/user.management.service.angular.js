/*nucleusServices.factory("userService", ["$resource", "$rootScope", "$http", "$filter", function($resource, $rootScope, $http, $filter) {
    var User = $resource("/user/:id?ajax=true");

    var userInstance;

    console.debug("Fetching user profile data.");
    var userDataResponse = User.get({id: "me"}, function() {
        console.debug("Resolved user profile data.", userDataResponse)
        userInstance = userDataResponse.userInstance;
        $rootScope.firstName = userInstance.firstName;
    });

    return {
        get: function() {
            return userDataResponse;
        },
        getUserInstance: function() {
            return userDataResponse.userInstance;
        },
        getResponseObject: function() {
            return userDataResponse;
        },
    }
}]);*/