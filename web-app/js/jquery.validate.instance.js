$(document).ready(function () {

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
                    if(element.parent().hasClass("input-group")) {
                        error.appendTo(element.parent().parent());
                    } else {
                        error.appendTo(element.parent());
                    }
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

    jQuery.validator.setDefaults({ 
        onfocusout: function(element) {
            if($(element).hasClass('url'), $(element).hasClass('email')) { // trimming only for url & email
                $(element).val($.trim($(element).val()));
                this.element(element);
            }
        },
        debug: false
    }); 

    $.validator.addMethod("maxlengthWithNewLine", function(value, element, arg) {
        if(!value) return false;

        var lineBreaks = $(element).val().match(/[^\r]\n/g);
        var newLineCharacters = 0;
        if(lineBreaks) {
            newLineCharacters = lineBreaks.length
        }
        if((value.length + newLineCharacters) <= arg) {
            return true;
        } else {
            $(element).val(value.substring(0, arg));
            return true;
        }
    },
    'Your message should be less than 1024'
    );

    /**
     * Return true, if the value is a valid date, also making this formal check mm/dd/yyyy.
     *
     * @example <input name="pippo" class="{dateUS:true}" />
     * @desc Declares an optional input element whose value must be a valid date.
     *
     */
    jQuery.validator.addMethod("dateUS", function(value, element) {
        var check = false;
        var re = /^\d{1,2}\/\d{1,2}\/\d{4}$/;
        if( re.test(value)){
            var adata = value.split('/');
            var mm = parseInt(adata[0],10);
            var gg = parseInt(adata[1],10);
            var aaaa = parseInt(adata[2],10);
            var xdata = new Date(aaaa,mm-1,gg);
            if ( ( xdata.getFullYear() == aaaa ) && ( xdata.getMonth () == mm - 1 ) && ( xdata.getDate() == gg ) )
                check = true;
            else
                check = false;
        } else
            check = false;
        return this.optional(element) || check;
    }, "Please enter a correct date");


    jQuery.validator.addMethod("emailList", function(value, element) {
        var exp = /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))$/i
        var current = this
        var emailList = value.split(',')
        var result = true
        $.each(emailList, function(index, value) {
            result = current.optional(element) || exp.test(value.trim())
            if(!result) {
                invalidEmail = value.trim()
                return false
            }
        })
        return result;
    }, 'Invalid email found in email List.');

    jQuery.validator.addMethod("uniqueInList", function(value, element) {
        var itemList = value.split(',')
        var trimmedItemList = new Array()
        $.each(itemList, function(index, value) {   // Trimming the list to remove duplicate items
            trimmedItemList.push(value.trim())
        })
        trimmedItemList = jQuery.unique(trimmedItemList)
        if(itemList.length != trimmedItemList.length)
            return false
            return true
    },"List contains duplicate items.");
    
    jQuery.extend(jQuery.validator.messages, {
        url: "Please enter a valid URL (http://example.com)"
    });
    
    jQuery.validator.addMethod("lettersandspace", function(value, element) {
        return this.optional(element) || /^[ a-z]+$/i.test(value);
    }, "Letters & space only please");
    
    jQuery.validator.addMethod("titleAlphaNum", function(value, element) {
        return this.optional(element) || /^[a-z0-9_\-\s]+$/i.test(value);
    }, "Letters, numbers, space, dash or underscore only please");
    
    jQuery.validator.addMethod("notEqualTo", function(value, element, param) {
        return this.optional(element) || value != param;
    }, jQuery.validator.format("Please fix this field. (Must not be {0})"));

});