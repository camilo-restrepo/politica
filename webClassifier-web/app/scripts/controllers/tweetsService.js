'use strict';

angular.module('webClassifierWebApp').factory('TweetsService', ['$resource', function ($resource) {
  	
  	var url = 'http://localhost:8080/tweets/';
  	var defaultParams = {};
  	var actions= {
  		getTweets: {method: 'GET', isArray: true}
  	};
  	return $resource(url, defaultParams, actions);
 }]);