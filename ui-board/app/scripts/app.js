'use strict';

/**
 * @ngdoc overview
 * @name boardApp
 * @description
 * # boardApp
 *
 * Main module of the application.
 */
var boardModule = angular.module('boardModule', []);
var app = angular.module('boardApp', [ 'ngResource', 'ui.router', 'angular-websocket', 'boardModule', 'ngDialog', 
                         'duScroll', 'config', 'angulartics', 'angulartics.google.analytics', 'angulartics.scroll']);
