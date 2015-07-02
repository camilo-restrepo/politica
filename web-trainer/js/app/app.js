'use strict';

var app = angular.module('politica', ['ngSanitize']);

app.controller('MainController',
  function ($scope, FacebookService, TwitterService) {
    var _this = this;
    this.categories = [
      {
        name: 'Positivo',
        internal_value: 1,
        class: 'success',
        key_code: 65
      },
      {
        name: 'Neutro',
        internal_value: 0,
        class: 'default',
        key_code: 83
      },
      {
        name: 'Negativo',
        internal_value: 2,
        class: 'danger',
        key_code: 68
      },
      {
        name: 'Sarcasmo',
        internal_value: 3,
        class: 'info',
        key_code: 70
      },
      {
        name: 'Saltar/ignorar',
        internal_value: 4,
        class: 'inverse',
        key_code: 71
      }
    ];

    // Setup key listeners
    var $window = $(window);
    this.categories.forEach(function (category, _i) {
      $window.keyup(function (e) {
        if(e.keyCode == category.key_code) {
          _this.selectOption(category);
          $scope.$apply();
        }
      });
    });

    this.selectOption = function (category) {
      this.next();
    };

    this.next = function () {
      // Select provider
      var currentProvider = {
        fb: [FacebookService],
        twtr: [TwitterService],
        both: [FacebookService, TwitterService]
      }[_this.active];
      _this.$providerIndex = ((_this.$providerIndex || 0) + 1) % currentProvider.length;
      currentProvider = currentProvider[_this.$providerIndex];

      // Get next
      _this.current = currentProvider.next();

    };


  });

app.factory('FacebookService',
  function () {
    return (function () {
      var _this = this,
          _index = -1;

      var data = [
    {
        "facebookId": "10153226078491926_10153227022611926",
        "message": "Y el resto de los candidatos? No se lo preguntan?",
        "likesCount": 0,
        "timestamp": 1428753003000,
        "post": {
            "facebookId": "51571361925_10153226078491926",
            "message": null,
            "commentsCount": 0,
            "likesCount": 0,
            "sharesCount": 1,
            "timestamp": 1428712081000,
            "page": {
                "facebookId": "51571361925",
                "name": "Carlos Vicente de Roux",
                "likes": 1055,
                "timestamp": 1429051058109
            }
        }
    },
    {
        "facebookId": "10153187253441926_10153190081096926",
        "message": "https://www.youtube.com/watch?v=2vYeyWPAxtI",
        "likesCount": 0,
        "timestamp": 1427390571000,
        "post": {
            "facebookId": "51571361925_10153187253441926",
            "message": "Hoy después de las 8 a.m. estaré en el Primer Café, programa del canal Capital, dando a conocer cómo avanza mi precandidatura a la Alcaldía de Bogotá por el partido Alianza Verde. Gracias por su sintonía.",
            "commentsCount": 0,
            "likesCount": 0,
            "sharesCount": 0,
            "timestamp": 1427285390000,
            "page": {
                "facebookId": "51571361925",
                "name": "Carlos Vicente de Roux",
                "likes": 1055,
                "timestamp": 1429051058109
            }
        }
    }
];

      var text = function (str) {
          var li = 0;
          var i = -1;
          // Get links
          while((i = str.indexOf('http', li) || str.indexOf('https', li)) > -1) {
            var next = str.indexOf(' ', i) || str.indexOf("\n", i);
            if(next === -1) { next = str.length; }
            var href = str.substring(i, next);
            str = str.substring(0, i) + "<a target='_blank' href='" + href + "'>" +
                  str.substring(i, next)+ "</a>" +
                  str.substring(next);
            li = i + ("<a target='_blank' href='" + href + "'>").length + "</a>".length;
          }
          return str;
      };

      return {
        next: function () {
          _index = ( _index + 1 ) % data.length
          var d = data[_index];
          return {
            $provider: _this,
            id: d.facebookId,
            content: text(d.message),
            provider: 'facebook'
          };
        }
      };
    })();
  });

app.factory('TwitterService',
  function () {
    return (function() {
      var _this = this,
          _index = -1;
      var data = [
        // Tweet 1
        {
        "twitterId": 610237984424783900,
        "createdAt": 1434327047000,
        "favoriteCount": 0,
        "retweetedCount": 13,
        "text": "RT @CLOPEZanalista: Un motivo para sonreír � @CVderoux \"Mike Wazowski\"...nos compite por lo verde y lo popular del nombre! http://t.co/6n86…",
        "target": {
            "id": "CVderoux"
        },
        "user": {
            "twitterId": 62945553,
            "name": "CVderoux"
        },
        "latitude": "NaN",
        "longitude": "NaN"
        },
        // Tweet 2
        {
        "twitterId": 609796260074291200,
        "createdAt": 1434221732000,
        "favoriteCount": 1,
        "retweetedCount": 1,
        "text": "@CarlgarNN @aceromora @CiudadTintal @Angelapinzon @MaFeRojas @luisferincon Lástima. El encanto del bicitaxi es su amabilidad con el ambiente",
        "target": {
            "id": "CVderoux"
        },
        "user": {
            "twitterId": 62945553,
            "name": "CVderoux"
        },
        "latitude": 4.64921891,
        "longitude": -74.06251184
        }
      ];

      var text = function (str) {
        var li = 0;
        var i = -1;
        while((i = str.indexOf('@', li)) > -1) {
          var next = str.indexOf(' ', i);
          str = str.substring(0, i) + "<span class='mention'>" +
                str.substring(i, next)+ "</span>" +
                str.substring(next);
          li = i + "<span class='mention'>".length + "</span>".length;
        }
        return str;
      };

      return {
        next: function () {
          _index = ( _index + 1 ) % data.length
          var d = data[_index];
          return {
            $provider: _this,
            id: d.twitterId,
            content: text(d.text),
            provider: 'twitter'
          };
        }
      };
    })();
  });
