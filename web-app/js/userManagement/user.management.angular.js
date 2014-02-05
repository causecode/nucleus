var nucleusApp = angular.module("nucleus", ["ngCookies", "ngResource", "ngRoute"], 
  function($routeProvider, $locationProvider, $httpProvider) {});

nucleusApp.controller('UserManagementCtrl',['$scope', '$rootScope', '$resource', function ($scope, $rootScope, $resource) {
    var User = $resource("/userManagement/list?ajax=true");

    console.log('zfas');

    User.get(null, function(data) {
        console.log(data,'data');
        $scope.userInstanceList = data.userInstanceList;
        $scope.userInstanceTotal = data.userInstanceTotal;
        $scope.roleList = data.roleList;
        $scope.currentUserInstance = data.currentUserInstance;
    })

    $scope.selectUnselectUser = function() {
        $scope.userInstance = this
        console.log('sasa');
    }
}]);

/*
var App = window.App;

$('select#userAction').on("change", function() {
    var action = $(this).val()
    console.log('ss');
    console.log(action);
    if (action.indexOf('null') == 0)
        return false;

    var check = $('input[name=selectedUser]:checked').size();
    $(this).val($(this).prop('defaultSelected'));
    if (check == 0) {
        showAlertMessage('Please select at least one user at current page.')
        return false
    }

    var confirmAction = confirm("Are you sure want to perform this action- " + action);
    if(!confirmAction)  return false;
    switch (action) {
        case 'Make user in-active':
            makeUserActiveInactive('false')
            break;
        case 'Make user active':
            makeUserActiveInactive('true')
            break;
        case 'Send bulk message':
            var url = '/altruHelpDashboard/fetchEmails?' + $('#manage-user-form').serialize();
            fetchEmails(url);
            break;
        case 'Export email list':
            window.location.href = '/altruHelpDashboard/downloadEmails?' + $('#manage-user-form').serialize();
            break;
    }
})

var makeUserActiveInactive = function(type) {
    showAlertMessage('Please wait ..', 'warning')
    $.ajax({
        type: 'POST',
        url: '/altruHelpDashboard/makeUserActiveInactive?type=' + type + "&" + $('#manage-user-form').serialize(),
        success: function(data, textStatus) {
            showAlertMessage(data, 'success')
            if(data.indexOf('in-active') != 0) {
                $('input[name=selectedUser]:checked').each(function() {
                    var id = $(this).val()
                    $('.user'+id).removeClass('icon-ok-sign')
                })
            } else {
                $('input[name=selectedUser]:checked').each(function() {
                    var id = $(this).val()
                    $('.user'+id).addClass('icon-ok-sign')
                })
            }
        },
        statusCode: {
            401: function() {
                showAlertMessage('Please <a href="/login/auth">sign in</a> to continue.', 'error')
            }
        },
        error: function(jqXHR, textStatus, errorThrown) {
            showAlertMessage(App.server.error.message, 'error')
        }
    });
}

$('a.step, a.nextLink, a.prevLink, a.letter-sort').on("click", function(){
    var url = $(this).attr('href')
    url += "&"+ $('#manage-user-form').serialize();
    $(this).attr('href', url)
})

var clearSelectedUsers = function() {
    $('input[name=selectedUser]:checked').each(function(index) {
        $(this).attr('checked', false)
    })
}

$('form[name=user-search]').on("submit", function() {
    var url = $(this).attr('action')
    url += "?"+ $('#manage-user-form').serialize()
    $(this).attr('action', url)
})

$('input[name=selectedUser]:checked').click(function(){
    $.ajax({
        type: 'POST',
        url: '/altruHelpDashboard/clearSelection/' + $(this).val(),
        success: function(data, textStatus) {

        },
        statusCode: {
            401: function() {
                showAlertMessage('Please <a href="/login/auth">sign in</a> to continue.', 'error')
            }
        },
        error: function(jqXHR, textStatus, errorThrown) {
            showAlertMessage(App.server.error.message, 'error')
        }
    });
})


$(document).on("click", "ul.pagination a", function() {
    var $this = $(this);
    var url = $this.attr('href');
    var queryString = url.substring(url.indexOf("?") + 1);
    var result = {};
    queryString.split("&").forEach(function(pair) {
        pair = pair.split('=');
        result[pair[0]] = decodeURIComponent(pair[1] || '');
    });
    max = result["max"];
    offset = result["offset"];
    fetchAndDisplayList();

    return false;
})

$("a", "div#sort-list").click(function() {
    sort = $(this).data("value");
    $("div#sort-list button span.value").text($(this).text());
    fetchAndDisplayList();
})

$("a", "div#order-list").click(function() {
    order = $(this).data("value");
    $("div#order-list button span.value").text($(this).text());
    fetchAndDisplayList();
})

$("a", "div#role-filter").click(function() {
    var $this = $(this);
    var value = $this.data("value");
    var selecting = $this.hasClass("active");

    if(selecting) {
        $this.removeClass("active");
        var index = filterRoleList.indexOf(value);
        if(index > -1) {
            filterRoleList.splice(index, 1);
        }
    } else {
        $this.addClass("active");
        filterRoleList.push(value)
    }
    console.log(filterRoleList);
    fetchAndDisplayList();
    return false;
})

$("input#roleType").change(function() {
    roleType = $(this).val();
    fetchAndDisplayList();
    return false;
})

$(document).on("click", "div#user-list-container div.list-group-item", function(e) {
    if(!$(e.target).is("input")) {
        var userCheckbox = $(this).find("input#userId");
        var checked = userCheckbox.is(":checked");

        userCheckbox.prop("checked", !checked);
        if(checked) {
            userCheckbox.removeAttr("checked");
        } else {
            userCheckbox.attr("checked", "checked");
        }
        handleUserSelect();
    }
})

var $modifyRoleOverlayModal = $("div#modify-role-overlay")
var $modifyRoleForm = $("form", $modifyRoleOverlayModal);

$("a#modify-role").click(function() {
    var userIds = "";
    $("input[name=userId]:checked").each(function(index, item) {
        userIds += $(item).val();
        if(index < $("input[name=userId]:checked").length - 1) {
            userIds += ",";
        }
    })
    $("input#userIds", $modifyRoleForm).val(userIds);
    $modifyRoleOverlayModal.modal("show");
})

$modifyRoleForm.submit(function() {
    if(!$modifyRoleForm.valid()) {
        return false;
    }
    $.ajax({
        data: $modifyRoleForm.serialize(),
        url: "/user/modifyRoles",
        success: function() {
            $modifyRoleOverlayModal.modal("hide");
            fetchAndDisplayList();
        }
    })

    return false;
})//.data("validator").settings.ignore = " ";

function fetchAndDisplayList(data) {
    var stateObj = {sort: sort, order: order, max: max, offset: offset, roleFilter: filterRoleList.join(","), roleType: roleType};

    var dataToSend = stateObj;
    if(data) {
        dataToSend = data;
    } else {
        String uri = "/user/list?"
        if(filterRoleList.length > 0) {
            uri += "roleFilter=" + filterRoleList.join(",");
        }
        //history.pushState(stateObj, "", "/user/list?roleFilter=" + filterRoleList.join(","));
    }
    $.ajax({
        data: dataToSend,
        url: '/user/list',
        success: function(response) {
            if(response.listContent.trim() == "") {
                response.listContent = "<div class=\"list-group-item\"><i class=\"fa fa-meh-o\"></i> No matching records found.</div>";
            }
            $("div#user-list-container").html(response.listContent);
            $("ul.pagination").html(response.paginationContent).find("a").wrap("<li></li>");
            $("span.step.gap", ".pagination").wrap("<li></li>");
            $("span.currentStep", ".pagination").wrap("<li class='active'></li>")
        }
    })
}

window.onpopstate = function(event) {
    //fetchAndDisplayList(event.state);
}*/