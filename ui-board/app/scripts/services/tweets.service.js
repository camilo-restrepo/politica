'use strict';

boardModule.factory('tweetsService', tweetsService);
tweetsService.$inject = ['$resource'];

function tweetsService($resource) {

  var url = 'http://localhost:9001/board/api/tweets';
  var defaultParams = {};

  var actions = {
    getAllCandidatesPolarity: { method: 'GET', url: url + '/polarity?time=:time', isArray: true }
  };

  return $resource(url, defaultParams, actions);
}
