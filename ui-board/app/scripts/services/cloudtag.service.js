'use strict';

boardModule.factory('cloudtagService', cloudtagService);
cloudtagService.$inject = ['$resource'];

function cloudtagService($resource) {

  var url = 'http://104.236.26.163:9001/board/api/words';
  var defaultParams = {};

  var actions = {

    getCandidateCloudTag: { method: 'GET', url: url + '/:twitterId', isArray: false },
    getAllCandidatesCloudTags: { method: 'GET', url: url, isArray: true }
  };

  return $resource(url, defaultParams, actions);
}
