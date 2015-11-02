'use strict';

boardModule.factory('usersService', usersService);
usersService.$inject = ['$resource', 'environment'];

function usersService($resource, environment) {

  var url = environment.board + '/board/api/users';
  var defaultParams = {};

  var actions = {
    getAllUserCount: { method: 'GET', url: url + '/count', isArray: true },
    getTargetUsersCount: {method: 'GET', url: url + '/:twitterId/count', isArray: true},
    getCreationSummary: {method: 'GET', url: url + '/time', isArray: true},
    getUserCreationCandidate: {method: 'GET', url: url + '/:twitterId/time', isArray: true},
    getVennData: {method: 'GET', url: url + '/venn', isArray: true}
  };

  return $resource(url, defaultParams, actions);
}