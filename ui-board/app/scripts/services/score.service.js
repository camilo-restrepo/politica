'use strict';

boardModule.factory('scoreService', scoreService);
scoreService.$inject = ['$resource', 'environment'];

function scoreService($resource, environment) {

  var url = environment.board + '/board/api/scores';
  var defaultParams = {};

  var actions = {
    getAllTargetsScore: { method: 'GET', isArray: true }
  };

  return $resource(url, defaultParams, actions);
}
