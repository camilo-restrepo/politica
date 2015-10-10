'use strict';

app.config(function($stateProvider, $urlRouterProvider) {

    $urlRouterProvider.otherwise('/home');

    $stateProvider.state('home', {
        url: '/home',
        templateUrl: 'views/board.html'
    })
    
    .state('candidate', {
      url: '/candidatos/:twitterId',
      templateUrl: 'views/candidate.html'
    })

    .state('polarity', {
    	url: '/polaridad/:prediction',
    	templateUrl: 'views/polaridad.html'
    })
    
    .state('about', {
        url: '/about',
        controller: 'aboutController',
        templateUrl: 'views/about.html'
    });
});
