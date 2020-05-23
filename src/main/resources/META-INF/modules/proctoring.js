/* JS for integration with proctoring system from ProctorEdu
* Integration by SDK */
define( ["jquery"/*, "proctorEdu"*/], function ($/*, pE*/) {

    // создать экземпляр класса Supervisor
    let supervisor;

    var startSession = function ( serverAddress ) {
        supervisor = new Supervisor({url: serverAddress });    //'https://demo.proctoring.online'
        // инициализация сессии прокторинга
        // в поле token можно указать строку, функцию или промис
        supervisor.init({
            // указать провайдер авторизации, по умолчанию 'jwt'
            provider: 'jwt',
            // получить строку с токеном JWT от вашего сервера
            // на стороне вашего сервера должен быть реализован соответствующий API
            token: fetch('proctoring/token')
                .then(function(response) {
                    if (response.ok) return response.text();
                    else throw Error('Failed to get JWT');
                })
            /*provider: 'plain',
            id: '123456_RSMU_123456',
            subject: document.title,
            username: '123456RSMU_test',
            nickname: 'my nick name'*/
        }).then(function() {
            // запустить сессию прокторинга сразу после инициализации
            return supervisor.start();
        }).catch(function(err) {
            // в случае ошибки отобразить соответствующее сообщение
            alert(err.toString());
            // выполнить переадресацию на главную страницу,
            // чтобы не дать начать тест без прокторинга
            location.href = '';
        });
    };

    var finishSession = function () {
        supervisor.stop();
        supervisor.logout();
    };

    return {
        startSession : startSession,
        finishSession : finishSession
    }
});