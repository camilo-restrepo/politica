'use strict';

angular.module('boardApp').factory('TargetsService', ['$resource', function ($resource) {
  	
  	var url = 'http://localhost:9001/board/api/targets/';
  	var defaultParams = {};
  	var actions= {
  		getTargets: {method: 'GET', isArray: true}
  	};
  	return $resource(url, defaultParams, actions);
 }]);