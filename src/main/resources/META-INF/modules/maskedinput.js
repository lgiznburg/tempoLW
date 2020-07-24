define( ["jquery", "jquery.maskedinput"], function($,mask){
    return function ( clientId, maskDefinition, placeholder ) {
        if ( placeholder.length > 0 ) {
            $("#" + clientId).mask( maskDefinition, {placeholder:placeholder} );
        } else {
            $("#" + clientId).mask( maskDefinition);
        }
    }
});