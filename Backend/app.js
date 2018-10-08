var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var mysql = require('mysql');
var mongoose = require('mongoose');
require('dotenv').config();


var events = require('./routes/events');
var users = require('./routes/users');
var locations = require('./routes/locations');


mongoose.connect(
    'mongodb://rorymag:' + process.env.MONGOOSE_PASSWORD + '@pmcm01-shard-00-00-e5bjv.mongodb.net:27017,pmcm01-shard-00-01-e5bjv.mongodb.net:27017,pmcm01-shard-00-02-e5bjv.mongodb.net:27017/test?ssl=true&replicaSet=PMCM01-shard-0&authSource=admin'
);

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

//Handling CORS Errors
app.use(function(req, res, next) {
     res.header('Access-Control-Allow-Origin', '*');
     res.header('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept, Authorization');
     if(req.method === 'OPTIONS') {
         res.header('Access-Control-Allow-Methods', 'PUT, POST, PATCH, DELETE, GET');
         return res.status(200).json({});
     }
     next();
});


app.use('/events', events);
app.use('/users', users);
app.use('/locations', locations);


// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});



module.exports = app;

