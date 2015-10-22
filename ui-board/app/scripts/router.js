'use strict';

app.config(function($stateProvider, $urlRouterProvider) {

    $urlRouterProvider.otherwise('/select');

    $stateProvider.state('select', {
      url: '/select',
      templateUrl: 'views/select.html'
    })

    .state('city', {
      url: '/city/:cityId',
      templateUrl: 'views/board.html'
    })

    .state('candidate', {
      url: '/candidatos/:twitterId/:cityId',
      templateUrl: 'views/candidate.html'
    })

    .state('polarity', {
      url: '/polaridad/:prediction/:cityId',
      templateUrl: 'views/polaridad.html'
    })

    .state('about', {
      url: '/about',
      controller: 'aboutController',
      templateUrl: 'views/about.html'
    });
});
