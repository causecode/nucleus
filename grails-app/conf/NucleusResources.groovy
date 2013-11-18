modules = {
    nucleusCore {
        resource url: [dir: 'js', file: "application.js"]
    }
    validation {
        resource url: [dir: "js", file: "jquery.validate.js"]
        resource url: [dir: "js", file: "jquery.validate.additional-methods.js"]
        resource url: [dir: "js", file: "jquery.validate.instance.js"]
    }
    datetimePicker {
        resource url: [dir: "css/datepicker", file: "bootstrap-datepicker.css"]
        resource url: [dir: "css/datepicker", file: "jquery.timepicker.css"]
        resource url: [dir: "js/datepicker", file: "bootstrap-datepicker.js"]
        resource url: [dir: "js/datepicker", file:"jquery.timepicker.min.js"]
    }
    trackTimeZone {
        resource url: [dir: 'js', file: "track-time-zone.js"]
    }
}