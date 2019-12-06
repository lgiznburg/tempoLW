define( ["jquery"], function ($) {
    $(".upload-template").on("click", function () {
        var type = $(this).attr("value"); //get the type of the template (universal)
        var selector = "#" + type.toLowerCase() + "UploadRow"; //build a selector for a specific field
        $(selector).removeClass("hidden"); //show field by selector
    });

    $(".cancel-upload").on("click", function (e) {
        e.preventDefault();
        var type = $(this).attr("value"); //get the type of the template (universal)
        var selectorRow = "#" + type.toLowerCase() + "UploadRow"; //build a selector for a row of table
        var selectorForm = "form[selector=" + type + "]"; //build a selector for a form
        $(selectorForm).trigger('reset'); //reset form
        $(selectorRow).addClass("hidden"); //hide field
    });

    $(".comment-textarea").on("change", function () {
        var type = $(this).attr("param");
        var selector = "form[param=" + type + "]"; //build specific selector for the form with param (artificial attr)
        $(selector).submit();
    });

});
