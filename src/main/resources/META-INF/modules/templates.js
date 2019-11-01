define( ["jquery"], function ($) {
    $(".upload-template").on("click", function () {
        var type = $(this).attr("value");
        if ( type == "examresult" ) {
            $("#examResultUploadRow").removeClass("hidden");
        } else if ( type == "examstatement" ) {
            $("#examStatementUploadRow").removeClass("hidden");
        } else if ( type == "logins" ) {
            $("#loginsUploadRow").removeClass("hidden");
        } else {
            //do nothing
        }
    });

    $(".cancel-upload").on("click", function (e) {
        e.preventDefault();
        var type = $(this).attr("value");
        if ( type == "examresult" ) {
            $("#examResultUploadForm")[0].reset();
            $("#examResultUploadRow").addClass("hidden");
        } else if ( type == "examstatement" ) {
            $("#examStatementUploadForm")[0].reset();
            $("#examStatementUploadRow").addClass("hidden");
        } else if ( type == "logins" ) {
            $("#loginsUploadForm")[0].reset();
            $("#loginsUploadRow").addClass("hidden");
        } else {
            //do nothing
        }
    });

    $("#examResultComment").on("change", function () {
        $("#examResultCommentForm").submit();
    });

    $("#examStatementComment").on("change", function () {
        $("#examStatementCommentForm").submit();
    });

    $("#loginsComment").on("change", function () {
        $("#loginsCommentForm").submit();
    });
});
