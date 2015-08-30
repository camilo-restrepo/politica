'use strict';

angular.module('boardApp').factory('targetsService', ['$resource', function ($resource) {
  	
  	var url = 'http://104.236.26.163:9001/board/api/targets';
  	var defaultParams = {};
  	var actions= {
  		getSingleTarget: { method: 'GET', url: url + '/:twitterId', isArray: false },
  		getTargets: {method: 'GET', isArray: true}
  	};
  	return $resource(url, defaultParams, actions);
 }]);
