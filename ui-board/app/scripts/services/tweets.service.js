'use strict';

boardModule.factory('tweetsService', tweetsService);
tweetsService.$inject = ['$resource', 'environment'];

function tweetsService($resource, environment) {

  var url = environment.board + '/board/api/tweets';
  var defaultParams = {};

  var actions = {

    getCandidateTweetStats: { method: 'GET', url: url + '/:twitterId/stats', isArray: false },
    getAllCandidatesPolarity: { method: 'GET', url: url + '/polarity?time=:time', isArray: true },
    getAllTweetsCount : {method: 'GET', url: url + '/count'},
    getLastTweetsCandidate: {method: 'GET', url: url + '/:twitterId/last', isArray: true},
    getLastTweetsPolarity: {method: 'GET', url: url + '/prediction/:prediction/last', isArray:true}
  };

  return $resource(url, defaultParams, actions);
}
