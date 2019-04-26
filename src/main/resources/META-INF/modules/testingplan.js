
define( ["jquery"], function ($) {
    $("input[id^=scoreCostField]").on( "change", function () {
        var idRe = /scoreCostField(.*)/;
        var res = idRe.exec( this.id );
        var suffix = res[1];

        var cost = $(this).val();
        var score = $("#maxScore" + suffix ).val();
        var count = $("#questionCountField" + suffix ).val();
        $("#maxMark" + suffix).val( cost * score );
        $("#totalRuleMark" + suffix).val( cost * score * count );

        countTotal();
    })

    $("input[id^=questionCountField]").on( "change", function ()  {
        var idRe = /questionCountField(.*)/;
        var res = idRe.exec( this.id );
        var suffix = res[1];

        var count = $(this).val();
        var mark = $("#maxMark" + suffix ).val();
        $("#totalRuleMark" + suffix).val( mark * count );

        countTotal()
    } )

    function countTotal() {
        var total = 0;
        $("input[id^=maxMark]").each( function () {
            var idRe = /maxMark(.*)/;
            var res = idRe.exec( this.id );
            var suffix = res[1];

            var maxMark = $(this).val();
            var quant = $("#questionCountField" + suffix).val();
            total += maxMark * quant;
        })
        $("#examMaxResult").val( total );
    }

    $().on( "load", countTotal() );

} );