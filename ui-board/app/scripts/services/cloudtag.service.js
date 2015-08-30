'use strict';

boardModule.factory('cloudtagService', cloudtagService);
cloudtagService.$inject = ['$resource', 'environment'];

function cloudtagService($resource, environment) {

  var url = environment.board + '/board/api/words';
  var defaultParams = {};

  var actions = {

    getCandidateCloudTag: { method: 'GET', url: url + '/:twitterId', isArray: false },
    getAllCandidatesCloudTags: { method: 'GET', url: url, isArray: true }
  };

  return $resource(url, defaultParams, actions);
}
