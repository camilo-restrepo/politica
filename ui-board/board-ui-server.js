var compression = require('compression');
var express = require('express');

var app = express();
app.use(compression());
app.use(express.static('dist'));
app.listen(80);
