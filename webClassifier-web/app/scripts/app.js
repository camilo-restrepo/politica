'use strict';

/**
 * @ngdoc overview
 * @name webClassifierWebApp
 * @description
 * # webClassifierWebApp
 *
 * Main module of the application.
 */
angular
  .module('webClassifierWebApp', [
    'ngResource',
    'ngRoute'
  ])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'views/main.html',
        controller: 'MainCtrl',
        controllerAs: 'main'
      })
      .otherwise({
        redirectTo: '/'
      });
  });
