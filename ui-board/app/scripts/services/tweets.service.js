'use strict';

boardModule.factory('tweetsService', tweetsService);
tweetsService.$inject = ['$resource'];

function tweetsService($resource) {

  var url = 'http://104.236.26.163:9001/board/api/tweets';
  var defaultParams = {};

  var actions = {

    getCandidateTweetStats: { method: 'GET', url: url + '/:twitterId/stats', isArray: false },
    getAllCandidatesPolarity: { method: 'GET', url: url + '/polarity?time=:time', isArray: true }
  };

  return $resource(url, defaultParams, actions);
}
