'use strict';

app.config(function($stateProvider, $urlRouterProvider) {

    $urlRouterProvider.otherwise('/home');

    $stateProvider.state('home', {
        url: '/home',
        controller: boardController,
        templateUrl: 'views/board.html'
    });
});
