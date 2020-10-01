define( ["jquery"], function($){

    function lockSubmitButton( id ) {

        let selector = "#" + id;
        $( selector ).prop('disabled', true);
        $( selector ).addClass('disabled');

    }

    return function ( clientId ) {
        $( "#" + clientId ).on('click', function () {
            lockSubmitButton( $(this).id );
        })
    }
});