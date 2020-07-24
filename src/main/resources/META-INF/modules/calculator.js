define( ["jquery"], function ($) {

    let num1, num2;

    function AddAChar( event )
    {
        let box = $("#txtbox_area");
        if( box.val() == null || box.val() === "0")
            box.val( event.data.inChar );
        else
            box.val( box.val() + event.data.inChar );
    }

    function Clear()
    {
        $("#txtbox_area").val("0");
    }

    function Answer()
    {
        let box = $("#txtbox_area");
        box.val( eval(box.val()) );
    }


    function sin()
    {
        let box = $("#txtbox_area");
        box.val( Math.sin(box.val()) );
    }


    function cos()
    {
        let box = $("#txtbox_area");
        box.val( Math.cos(box.val()) );
    }

    function tan()
    {
        let box = $("#txtbox_area");
        box.val( Math.tan(box.val()) );
    }

    function pi()
    {
        let box = $("#txtbox_area");
        box.val( Math.PI  );
    }

    function acos()
    {
        let box = $("#txtbox_area");
        box.val( Math.acos(box.val()) );
    }

    function asin()
    {
        let box = $("#txtbox_area");
        box.val( Math.asin(box.val()) );
    }

    function atan()
    {
        let box = $("#txtbox_area");
        box.val( Math.atan(box.val()) );
    }

    function rand()
    {
        let box = $("#txtbox_area");
        box.val( Math.random() );
    }

    function Euler()
    {
        let box = $("#txtbox_area");
       box.val( Math.E  );
    }

    function sqrt()
    {
        let box = $("#txtbox_area");
        box.val( Math.sqrt(box.val()) );
    }

    function natLog()
    {
        let box = $("#txtbox_area");
        box.val( Math.log(box.val()) );
    }

    function Log()
    {
        let box = $("#txtbox_area");
        box.val( ( 1/Math.LN10 ) * Math.log(box.val()) );
    }

    function percent()
    {
        let box = $("#txtbox_area");
        box.val( (box.val())/100  );
    }

    function Round()
    {
        let box = $("#txtbox_area");
        box.val( Math.round(box.val()) );
    }

    function square()
    {
        let box = $("#txtbox_area");
        num1=box.val();
        num2=box.val();
        var inChar=num1 * num2;
        box.val( inChar );
    }

    function negative()
    {
        let box = $("#txtbox_area");
        box.val( 0 - box.val() );
    }

    function setAllClicks() {
        $("#Nine").on( "click", {inChar:"9"}, AddAChar );
        $("#Eight").on( "click", {inChar:"8"}, AddAChar );
        $("#Seven").on( "click", {inChar:"7"}, AddAChar );
        $("#Six").on( "click", {inChar:"6"}, AddAChar );
        $("#Five").on( "click", {inChar:"5"}, AddAChar );
        $("#Four").on( "click", {inChar:"4"}, AddAChar );
        $("#Three").on( "click", {inChar:"3"}, AddAChar );
        $("#Two").on( "click", {inChar:"2"}, AddAChar );
        $("#One").on( "click", {inChar:"1"}, AddAChar );
        $("#Zero").on( "click", {inChar:"0"}, AddAChar );
        $("#TwoZeros").on( "click", {inChar:"00"}, AddAChar  );
        $("#btclear").on( "click", Clear );
        $("#equals").on( "click", Answer );
        $("#btplus").on( "click", {inChar:"+"}, AddAChar );
        $("#btsub").on( "click", {inChar:"-"}, AddAChar );
        $("#btsin").on( "click", sin );
        $("#btcos").on( "click", cos );
        $("#bttan").on( "click", tan );
        $("#btmult").on( "click", {inChar:"*"}, AddAChar );
        $("#btdiv").on( "click", {inChar:"/"}, AddAChar );
        $("#btasin").on( "click", asin );
        $("#btacos").on( "click", acos );
        $("#btatan").on( "click", atan );
        $("#Percent").on( "click", percent );
        $("#Sqrt").on( "click", sqrt );
        $("#Pi").on( "click", pi );
        $("#cubbut").on( "click", square );
        $("#e").on( "click", Euler );
        $("#dot").on( "click", {inChar:"."}, AddAChar );
        $("#neg").on( "click", negative );
        $("#rounding").on( "click", Round );
        $("#btrdm").on( "click", rand );
        $("#btlog").on( "click", Log );
        $("#btln").on( "click", natLog );
    }

    //$().on( "load", setAllClicks() );

    return { setAllClicks : setAllClicks }
});