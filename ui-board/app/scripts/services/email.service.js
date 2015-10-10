"use strict"

boardModule.factory('emailService', emailService);
emailService.$inject = ['$resource'];

function emailService($resource) {

    var url = 'http://e3-dev.granpanda.com:9022/mofficer/api/emails/julianespinel';
    var paramDefaults = {};

    var actions = {
        sendEmail: { method: 'POST' }
    }

    return $resource(url, paramDefaults, actions);
}
