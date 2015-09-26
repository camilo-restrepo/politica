'use strict';

boardModule.factory('targetsService', targetsService);
targetsService.$inject = ['$resource', 'environment'];

function targetsService($resource, environment) {

  var url = environment.board + '/board/api/targets';
  var defaultParams = {};

  var actions= {
    getSingleTarget: { method: 'GET', url: url + '/:twitterId', isArray: false },
    getTargets: { method: 'GET', isArray: true }
  };

  return $resource(url, defaultParams, actions);
}
