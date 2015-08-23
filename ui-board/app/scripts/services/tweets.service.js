'use strict';

boardModule.factory('tweetsService', tweetsService);
tweetsService.$inject = ['$resource'];

function tweetsService($resource) {

  var url = 'http://localhost:9001/board/api/tweets';
  var defaultParams = {};

  var actions = {
    getAllCandidatesImageToday: { method: 'GET', url: url + '/polarity?time=today', isArray: true },
    getAllCandidatesImageMonth : { method: 'GET', url: url + '/polarity?time=month', isArray: true }
  };

  return $resource(url, defaultParams, actions);
}
