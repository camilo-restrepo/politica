'use strict';

angular.module('webClassifierWebApp').factory('TweetsService', ['$resource', 'environment', function ($resource, environment) {
  	
  	var url = environment.webClassifier + '/tweets/';
  	var defaultParams = {};
  	var actions= {
  		getTweets: {method: 'GET', isArray: true}
  	};
  	return $resource(url, defaultParams, actions);
 }]);
