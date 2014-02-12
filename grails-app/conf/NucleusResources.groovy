modules = {
    nucleusCore {
        resource url: [dir: 'js', file: "core.js", plugin: "nucleus"]
    }

    validation {
        resource url: [dir: "js", file: "jquery.validate.js", plugin: "nucleus"]
        resource url: [dir: "js", file: "jquery.validate.additional-methods.js", plugin: "nucleus"]
        resource url: [dir: "js", file: "jquery.validate.instance.js", plugin: "nucleus"]
    }

    datetimePicker {
        resource url: [dir: "css/datepicker", file: "bootstrap-datepicker.css", plugin: "nucleus"]
        resource url: [dir: "css/datepicker", file: "jquery.timepicker.css", plugin: "nucleus"]
        resource url: [dir: "js/datepicker", file: "bootstrap-datepicker.js", plugin: "nucleus"]
        resource url: [dir: "js/datepicker", file:"jquery.timepicker.min.js", plugin: "nucleus"]
    }

    trackTimeZone {
        resource url: [dir: 'js', file: "track-time-zone.js", plugin: "nucleus"]
    }

    prettyprint {
        resource url: 'js/core.js', linkOverride: "https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js"
    }

    userManagementAJ {
        resource url: [dir: 'js/userManagement', file: "user.management.angular.js", plugin: "nucleus"]
    }
}