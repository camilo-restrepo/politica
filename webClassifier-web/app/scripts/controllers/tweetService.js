'use strict';

angular.module('webClassifierWebApp').factory('TweetService', ['$resource', function ($resource) {
  	
  	var url = 'http://localhost:8080/tweets/:id';
  	var defaultParams = {};
  	var actions= {
  		updateTweet: {method: 'PUT'},
  		deleteTweet: {method: 'DELETE'}
  	};
  	return $resource(url, defaultParams, actions);
 }]);