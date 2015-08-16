'use strict';

angular.module('webClassifierWebApp').factory('TweetService', ['$resource', 'environment', function ($resource, environment) {
  	
  	var url = environment.webClassifier + '/tweets/:id';
  	var defaultParams = {};
  	var actions= {
  		updateTweet: {method: 'PUT'},
  		deleteTweet: {method: 'DELETE'}
  	};
  	return $resource(url, defaultParams, actions);
 }]);
