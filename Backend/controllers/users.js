const User = new require('../models/user');
const Event = new require('../models/event')
var mongoose = require('mongoose');
var bcrypt = require('bcryptjs');
var jwt = require('jsonwebtoken');

exports.users_get_all = function(req, res, next) {
    User.find()
        .select()
        .exec()
        .then(function(docs) {
            res.status(200).json(docs)
        })
        .catch(function(err) {
            res.status(404).json({error: err})
        });
}



exports.users_post_signup = function(req, res ,next) {
    User.find({email: req.body.email})
        .exec()
        .then(user => {
            if(user.length >= 1) {
                return res.status(409).json({
                    message: "Email already exists"
                });
            } else {
                bcrypt.hash(req.body.password, 10, (err, hash)=> {
                    if(err) {
                        return res.status(500).json({
                            error: err
                        });
                    } else {
                        const user = new User({
                            _id: new mongoose.Types.ObjectId,
                            email: req.body.email,
                            firstName: req.body.firstName,
                            lastName: req.body.lastName,
                            password: hash
                        });
                        user.save()
                            .then(result => {
                                res.status(201).json({
                                    message: "User Created"
                                });
                            })
                            .catch(err => {
                                console.log(err);
                                res.status(500).json({error: err})
                            });
                    }
                });
            }
        });
    }


exports.users_post_login = (req, res, next)=> {
    User.find({email: req.body.email})
        .exec()
        .then(user => {
            if(user.length < 1) {
                return res.status(401).json({
                    message: "Auth failed"
                });
            }
            bcrypt.compare(req.body.password, user[0].password, (err, result)=> {
                if(err) {
                    return res.status(401).json({
                        message: "Auth failed"
                    });
                }
                if(result) {
                    const token = jwt.sign(
                        {
                            email: user[0].email,
                            userId: user[0]._id
                        },
                        process.env.TOKEN_KEY,
                        {
                            expiresIn: "1h"
                        }
                    );
                    return res.status(200).json({
                        message: "Authentication Successful",
                        token: token,
                        id: user[0]._id
                    })
                }
            });
        })
        .catch(err=> {
            console.log(err);
            res.status(500).json({error: err});
        });
}

exports.users_delete_all = function(req, res, next) {
    User.remove()
        .exec()
        .then(function(result) {
            res.status(200).json(result);
        });
}



exports.users_delete = function(req, res, next) {
    User.remove({_id: req.params.userId})
        .exec()
        .then(function(result) {
            res.status(200).json({
                message: "user deleted"
            });
        })
        .catch(err => {
            res.status(500).json({error: err})
        });
}

exports.users_location_patch = (req, res, next) => {
    const id = req.params.userId;
    const longitude = req.body.longitude;
    const latitude = req.body.latitude;
    const altitude = req.body.altitude;
    User.update({_id: id}, {$set: {longitude: longitude, latitude: latitude, altitude: altitude}})
    .exec()
    .then(result => {
        console.log(result);
        res.status(200).json({result});
        
    })
    .catch(err => {
        console.log(err);
        res.status(500).json({err: err});
    });

    //TODO pass longitude and latitude to locationCheck function
    locationCheck(id, longitude, latitude, altitude);
}


function locationCheck(id, longitude, latitude, altitude) {
    console.log("locationCheck RUNNING");
    console.log("LONGITUDE: " + longitude + "\n" + "LATITUDE: " + latitude);
    var currentDate = new Date().toISOString();
    console.log("CURRENT DATE: " + currentDate);
    Event.find({
        "invitedUsers.name": id
    })
    .and([
        {
            "startDate": {$lte: currentDate}
        },
        {
            "endDate": {$gte: currentDate}
        }
    ])
    .select('_id name location startDate endDate completed createdBy invitedUsers')
    .populate('location', 'P1Lat P1Long P2Lat P2Long P3Lat P3Long P4Lat P4Long altitude')
    .exec()
    .then(doc => {
        console.log(doc);
        for(i = 0; i < doc.length; i++) {
            if(((boundryCheck(doc[i].location.P3Lat, doc[i].location.P3Long, doc[i].location.P1Lat, doc[i].location.P1Long, doc[i].location.P2Lat, doc[i].location.P2Long) < 0
                            && (boundryCheck(latitude, longitude, doc[i].location.P1Lat, doc[i].location.P1Long, doc[i].location.P2Lat, doc[i].location.P2Long) < 0))
                            || (boundryCheck(doc[i].location.P3Lat, doc[i].location.P3Long, doc[i].location.P1Lat, doc[i].location.P1Long, doc[i].location.P2Lat, doc[i].location.P2Long) >= 0
                            && boundryCheck(latitude, longitude, doc[i].location.P1Lat, doc[i].location.P1Long, doc[i].location.P2Lat, doc[i].location.P2Long) >= 0))) {
                        //x,y is on the correct side of P1 P2
                        console.log("FIRST IF STATEMENT SATISFIED");
                        if(((boundryCheck(doc[i].location.P1Lat, doc[i].location.P1Long, doc[i].location.P2Lat, doc[i].location.P2Long, doc[i].location.P3Lat, doc[i].location.P3Long) < 0
                            && (boundryCheck(latitude, longitude, doc[i].location.P2Lat, doc[i].location.P2Long, doc[i].location.P3Lat, doc[i].location.P3Long) < 0))
                            || (boundryCheck(doc[i].location.P1Lat, doc[i].location.P1Long, doc[i].location.P2Lat, doc[i].location.P2Long, doc[i].location.P3Lat, doc[i].location.P3Long) >= 0
                            && boundryCheck(latitude, longitude, doc[i].location.P2Lat, doc[i].location.P2Long, doc[i].location.P3Lat, doc[i].location.P3Long) >= 0))) {
                                //x,y is on the correct side of P2 P3
                                console.log("SECOND IF STATEMENT SATISFIED");
                                if(((boundryCheck(doc[i].location.P2Lat, doc[i].location.P2Long, doc[i].location.P4Lat, doc[i].location.P4Long, doc[i].location.P3Lat, doc[i].location.P3Long) < 0
                                    && (boundryCheck(latitude, longitude, doc[i].location.P4Lat, doc[i].location.P4Long, doc[i].location.P3Lat, doc[i].location.P3Long) < 0))
                                    || (boundryCheck(doc[i].location.P2Lat, doc[i].location.P2Long, doc[i].location.P4Lat, doc[i].location.P4Long, doc[i].location.P3Lat, doc[i].location.P3Long) >= 0
                                    && boundryCheck(latitude, longitude, doc[i].location.P4Lat, doc[i].location.P4Long, doc[i].location.P3Lat, doc[i].location.P3Long) >= 0))) {
                                        //x,y is on the correct side of P3 P4
                                        console.log("THIRD IF STATEMENT SATISFIED");
                                        if(((boundryCheck(doc[i].location.P3Lat, doc[i].location.P3Long, doc[i].location.P4Lat, doc[i].location.P4Long, doc[i].location.P1Lat, doc[i].location.P1Long) < 0
                                                && (boundryCheck(latitude, longitude, doc[i].location.P4Lat, doc[i].location.P4Long, doc[i].location.P1Lat, doc[i].location.P1Long) < 0)
                                                || (boundryCheck(doc[i].location.P3Lat, doc[i].location.P3Long, doc[i].location.P4Lat, doc[i].location.P4Long, doc[i].location.P1Lat, doc[i].location.P1Long) >= 0)
                                                && boundryCheck(latitude, longitude, doc[i].location.P4Lat, doc[i].location.P4Long, doc[i].location.P1Lat, doc[i].location.P1Long) >= 0))) {
                                                console.log("FINAL IF STATEMENT SATISFIED");
                                                console.log("USER ATTENDED");
                                                updateAttendance(doc[i]._id, id);
                                        
                        } 
                    } 
                }
            }
            
        }
    })
    .catch(function(err) {
        console.log(err);
        res.status(500).json({err: err});
    });
}

function updateAttendance(eventId, userId) {
    console.log("UPDATE ATTENDANCE");
    console.log(userId + "\n" + eventId);
    
    Event.findOneAndUpdate({
        _id: eventId,
        'invitedUsers.name': userId
    }, {
        $set: {
            'invitedUsers.$.attended': true
        }
    })
    .exec()
    .then(result=> {
        console.log(result);
    })
    .catch(err => {
        res.status(500).json({err: err});
        console.log(err);
    });
}

function altitudeAccuracyCheck(userAltitude, eventAltitude) {
    console.log("Checking Altitude");
    //implement 2 meter margin of error for altitude as altitude is not completely accurate
    if((userAltitude < eventAltitude + 2) && (userAltitude > eventAltitude - 2)) {
        return true;
    }
}

function boundryCheck(x, y, P1Lat, P1Long, P2Lat, P2Long) {
    var result;
    //function boundryCheck(x, y, y1, y2, x1, x2)
    result = y - P1Lat - (((P1Long - P1Lat) / (P2Long - P2Lat))*(x - P2Lat));
    return result;
}