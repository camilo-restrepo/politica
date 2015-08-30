'use strict';

boardModule.factory('scoreService', scoreService);
scoreService.$inject = ['$resource'];

function scoreService($resource) {

  var url = 'http://104.236.26.163:9001/board/api/tweets';
  var defaultParams = {};

  var actions = {
    getAllTargetsScore: { method: 'GET', isArray: true }
  };

  return $resource(url, defaultParams, actions);
}
