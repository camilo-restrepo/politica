var compression = require('compression');
var express = require('express');

var app = express();

app.get('/ping', function(request, response) {
  response.send('pong');
});

app.use(compression());
app.use(express.static('dist'));
app.listen(80);
