var App = {}
window.App = App;
App.jqval = App.jqval || {};
App.Tokens = App.Tokens || {};

$(window).on("scroll", function() {
    if($(window).scrollTop() > 15) {
        $("nav.main-menu").addClass("scrolled-up");
    } else {
        $("nav.main-menu").removeClass("scrolled-up");
    }
}).resize(function() {
    setMinimumHeight();
})

$(document).ajaxStart(function() {
    spinner(true);
}).ajaxStop(function() {
    spinner(false);
}).on("mouseleave", ".popover", function() {
    var $popover = $(this);
    var timeout = $popover.data('hide');
    if(!timeout) return false;
    
    setTimeout(function() {
        if(!$popover.is(':hover')) {
            destroyPopover(null, "div.popover");
        }
    }, timeout)
}).ready(function() {
    try {
        $(".date-picker").datepicker({
            format : 'mm/dd/yyyy'
        }).on("changeDate", function() {
            $(this).datepicker("hide");
        })
    } catch(e) {
    }

    $("[rel=tooltip]").tooltip();
    $("[rel=popover]").popover();
})

function setMinimumHeight() {
    var minHeight = $(window).height() - $("#content-footer").height();
    $("div#content-body > .container").css("min-height", minHeight - 10);
}

setMinimumHeight();

(function($) {
    $.fn.disable = function(action) {
        this.each(function() {
            $(this).toggleClass("disabled", action);
            if(action) {
                $(this).prop("disabled", "");
            } else {
                $(this).removeProp("disabled");
            }
            return $(this);
        })
    }
}(jQuery))

$('form.jquery-form').each(function(index) {
    var form = this;
    var validatorInstance = $(this).validate({
        errorElement : 'span',
        showErrors: function(errorMap, errorList) {
            this.defaultShowErrors();
            if(!$(form).hasClass("auto-disable")) {
                return;
            }
            $("[type=submit]", form).disable(this.numberOfInvalids() != 0);
        },
        errorClass : 'help-block icon-exclamation-sign',
        errorPlacement : function(error, element) {
            if(element.data("error-placement")) {
                error.appendTo($(element.data("error-placement")));
            } else {
                error.appendTo(element.parent());
            }
        },
        highlight : function(element, errorClass) {
            $(element).parents('.form-group').addClass("has-error");
        },
        unhighlight : function(element, errorClass) {
            $(element).parents('.form-group').removeClass("has-error");
        },
    });
    App.jqval[$(form).attr('id')] = validatorInstance;

});

/**
 * A script to select/un-select same all same name of checkbox
 * by click of a single checkbox of class <b>check-uncheck</b>
 * Use data attribute <b>checkbox-name</b> to specify selector.
 */
var $allSelector = $("input[type=checkbox].check-uncheck");
var selectorName = $allSelector.data("checkbox-name");
selectorName = selectorName ? selectorName : "selectedUser";

$allSelector.on("change", function() {;
    $('input[name='+selectorName+']').prop('checked', $allSelector.is(':checked'));
});

$('input[name='+selectorName+']').change(function() {
    var allCount = $('input[name='+selectorName+']').length;
    var selectedCount = $('input[name='+selectorName+']:checked').length;
    if(selectedCount == 0) {
        $allSelector.prop({
            checked: false,
            indeterminate: false
        });
    } else if(allCount == selectedCount) {
        $allSelector.prop({
            checked: true,
            indeterminate: false
        });
    } else {
        $allSelector.prop("indeterminate", true);
    }
});

$("a", ".pagination").wrap("<li></li>");
$("span.step.gap", ".pagination").wrap("<li></li>");
$("span.currentStep", ".pagination").wrap("<li class='active'></li>")
$("div.pagination").wrapInner("<ul class='pagination'></ul>");

$("th.sortable.sorted").each(function() {
    if($(this).hasClass("asc"))
        $(this).append("<span class='order'>&nbsp;&uarr;</span>");
    else
        $(this).append("<span class='order'>&nbsp;&darr;</span>");
});

$("div#block-page-overlay").modal({
    backdrop: "static",
    keyboard: false,
    show: false
})

$("div.modal").on('hidden.bs.modal', function() {
    resetSection($(this));
    setDefaultValues($(this));
}).on('shown.bs.modal', function() {
    $(this).find("[autofocus='autofocus']").focus();
}).each(function() {
    var $this = $(this);
    $this.data("tempContent", function(data) {
        var $originalBody = $(".modal-body", $this).addClass("original");
        var $temporaryBody = $(".modal-body.temporary", $this);
        if($temporaryBody.length == 0) {
            $(".modal-body", $this).after("<div class=\"modal-body temporary\"></div>");
        }
        $temporaryBody = $(".modal-body.temporary", $this);
        if(data.body) {
            $temporaryBody.hide().html(data.body);
        }
        $originalBody.fadeOut("slow", function() {
            $temporaryBody.fadeIn();
        })
        return $this;
    }).data("hideFooter", function(action) {
        if(action)
            $(".modal-footer", $this).fadeOut();
        else
            $(".modal-footer", $this).fadeIn();
        return $this;
    }).data("showSpinner", function(action) {
        var $spinner = $(".modal-header i.icon-spinner", $this);
        if($spinner.length == 0) {
            $(".modal-header .modal-title", $this).after("&nbsp;<i class=\"icon-spinner icon-spin\"></i>");
            $spinner = $(".modal-header i.icon-spinner", $this);
        }
        $spinner.toggle(action);
        return $this;	// Providing chaining
    })
})

/**
 * Collapsing feature of twitter bootstrap varies height
 * of division instead of hiding or showing the divisions. This will prevent
 * auto tab indexing in any form having this collapsible elements. To
 * prevent this adding 'hide' class to the division when it completely
 * hidden and removing it back when show action is called. See example in
 * Opportunity form after commenting the code below.
 */
$('.collapse').each(function(index, el) {
    if (!$(el).hasClass('in'))
        $(el).addClass('hide');
    $(el).on('show.bs.collapse', function() {
        $(el).removeClass('hide');
    }).on('hidden.bs.collapse', function() {
        $(el).addClass('hide');
    })
})

/**
 * Giving automatic focus to all those elements which are hidden by token 
 * auto completes.
 */
$('input[data-action="input-token-focus"]').each(function(index, el) {
    var elementName = "token-input-" + $(el).attr('id');
    $('input#' + elementName).focus();
})

$("[data-default]").each(function(index, el) {
    var defaultToStore = $(el).data('default')
    if(defaultToStore != undefined) {
        $(el).data('default-' + defaultToStore, $(el).attr(defaultToStore));
    }
})

function blockPage(action) {
    $("div#block-page-overlay").modal(action ? "show" : "hide");
}

/**
 * Used to display alert messages at top of any page.
 * @param message: (Required) Message to show on alert box
 * @param type: type of alert message- success, error, info. Default to 'warning'.
 * @param params: JavaScript object containing two values-
 *  1) element: If other alert message to shown. Default to '#alert-message'.
 *  2) makeStrong: Set it to true if the text in alert message needs to be bold.
 *  3) innerElement: Id, class or HTML element inside alert block in which message to be inserted
 *  4) timeout: Set timeout to automatically hide alert block after a particular time.
 *  4) scrollToAlert: set to true to automatic scroll window to alert message.
 */
function showAlertMessage(message, type, params) {
    if (!params)
        params = {};
    if (!type)
        type = 'warning';
    if (!params.timeout)
        params.timeout = 10000;
    if (params.makeStrong == undefined)
        params.makeStrong = true;
    if (!params.innerElement)
        params.innerElement = 'p';
    if (!params.element)
        params.element = 'div#alert-message';

    $(params.element).removeClass('alert-warning alert-error alert-success alert-info');
    $(params.innerElement, params.element).html(message);

    if (params.makeStrong)
        $(params.innerElement, params.element).wrapInner('<strong>');

    $(params.element).addClass('alert-' + type).fadeIn().removeClass("hide");

    if (params.scrollToAlert && params.scrollToAlert != undefined)
        $("html, body").animate({
            scrollTop : 0
        }, 600);

    if (params.timeout != "clear") {
        setTimeout(function() {
            $(params.element).fadeOut();
        }, params.timeout);
    }
}

function resetSection(element) {
    var cc = cc || {}
    if (element.is("form")) {
        element[0].reset();
    } else {
        if ($('form', element).size() > 0) {
            $('form', element)[0].reset();
        }
    }
    $(element).disable(false);
    if($(element).hasClass("modal")) {
        var $modal = $(element);
        $(".modal-footer", $modal).show();
        $(".modal-body.original", $modal).show();
        $(".modal-body.temporary", $modal).hide().html("");
        $(".modal-header i.icon-spinner", $modal).hide();
    }
    /**
     * Iterating through each element and clearing all the data
     */
    cc.formElements = $(element).find(
            "input[type=text], input[type=email], select, textarea")
    $.each(cc.formElements, function(index, el) {
        $(el).parents('.form-group.has-error').removeClass('has-error'); // Remove validation errors
        $(el).parent().find('span[class~=icon-exclamation-sign], label[class~=error]').hide();
    });

    /**
     * Hiding all alert messages
     */
    cc.alertElements = $(element).find("div.alert");
    $.each(cc.alertElements, function(index, el) {
        $(el).hide();
    })

    /**
     * Enable all fieldsets.
     */
    cc.fieldsets = $(element).find("fieldset");
    $.each(cc.fieldsets, function() {
        $(this).removeAttr("disabled");
    })

    /**
     * Reloading captcha if data-captcha attribute given.
     */
    if ($(element).data('captcha'))
        Recaptcha.reload();

    $(element).find('ul#upload-list').html('<li>No file chosen</li>')

    if (App.jcropAPI) {
        App.jcropAPI.destroy();
    }
}

function calculateModalDimensions(element) {
    var App = window.App || {};
    App.modal = App.modal || {};
    App.modal.width = $(element).width();
    App.modal.height = $(element).height();
    App.modal.bodyWidth = $('.modal-body', element).width();
    App.modal.bodyHeight = $('.modal-body', element).height();
}

function insertSpinner(container, append, fontSize) {
    if(fontSize == undefined) fontSize: '13px';
    var template = '<i class="icon-spinner icon-spin" id="main-spinner" style="font-size:' + fontSize + '"></i>';
    if(append)
        $(container).append(template);
    else
        $(container).html(template);
}

function spinner(show, selector) {
    var $selector = $("i#main-spinner");
    if(selector) {
        $selector = $(selector);
    }
    if(show) {
        $selector.addClass('icon-spin').show().css("display", "inline-block");
    } else {
        $selector.removeClass('icon-spin').hide();
    }
}

function setDefaultValues(container) {
    var cc = cc || {};
    if (!container)
        container = document;
    cc.imageElements = $(container).find("img");
    $.each(cc.imageElements, function(index, el) {
        var defaultValue = $(el).data('default-src');
        if(defaultValue)
            $(el).attr('src', defaultValue);
        var defaultRemove = $(el).data('default-remove');
        if(defaultRemove != undefined) {
            $(el).removeAttr(defaultRemove);
        }
    })

    cc.inputElements = $(container).find("input[type=text], input[type=hidden], select, textarea")
    $.each(cc.inputElements, function(index, el) {
        var defaultValue = $(el).data('default-value');
        if(defaultValue != undefined)
            $(el).val(defaultValue);
    })
}