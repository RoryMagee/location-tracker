const Event = new require('../models/event');
const Location = new require('../models/location');
const User = new require('../models/user');
var mongoose = require('mongoose');

exports.events_get_all = function(req, res, next) {
    Event.find()
        .select('_id name location startDate endDate createdBy invitedUsers completed')
        .populate('location','name')
        .populate('invitedUsers.name', 'firstName lastName')
        .exec()
        .then(function(docs) {
            const response = {
                count: docs.length,
                events: docs.map(doc => {
                    return {
                        _id: doc._id,
                        name: doc.name,
                        location: doc.location,
                        startDate: doc.startDate,
                        endDate: doc.endDate,
                        createdBy: doc.createdBy,
                        invitedUsers: doc.invitedUsers,
                        completed: doc.completed 
                    }
                })
            };
            if(docs.length >= 0) {
                res.status(200).json(response);
                User.findById()

            } else {
                res.status(404).json({
                    message: 'No records'
                });
            }
        })
        .catch(function(err){
            res.status(500).json({
                error: err
            });
        });
}

exports.events_get = function(req, res, next) {
    const id = req.params.eventId;
    Event.findById(id)
        .select('_id name location startDate endDate createdBy invitedUsers completed')
        .exec()
        .then(function(doc) {
            console.log("From database", doc);
            if (doc) {
                res.status(200).json(doc);
            } else {
                res.status(404).json({message: 'no valid entry found for provided ID'});
            }
        })
        .catch(function(err) {
            console.log(err);
            res.status(500).json({err: err});
        });
}

exports.events_get_user_events = (req, res, next) => {
    const id = req.params.userId;
    var currentDate = new Date().toISOString();
    Event.find({
        "invitedUsers.name": id
    })
    .and([
        {
            "endDate": {$gte: currentDate}
    }
    ])
    .populate('createdBy', 'firstName lastName')
    .populate('location', 'name')
    .populate('invitedUsers.name', 'firstName lastName _id')
    .select('name location startDate endDate createdBy invitedUsers completed')
    .exec()
    .then((doc)=> {
        console.log("From database", doc);
        if(doc) {
            res.status(200).json(doc);
        } else {
            res.status(404).json({message: "no valid entry found for provided ID"});
        }
    })
    .catch((err)=> {
        console.log(err);
        res.status(500).json({err: err});
    });
}

exports.events_get_user_events_all = (req, res, next) => {
    const id = req.params.userId;
    var currentDate = new Date().toISOString();
    Event.find({
        "invitedUsers.name": id
    })
    .populate('createdBy', 'firstName lastName')
    .populate('location', 'name')
    .populate('invitedUsers.name', 'firstName lastName _id')
    .select('name location startDate endDate createdBy invitedUsers completed')
    .exec()
    .then((doc)=> {
        console.log("From database", doc);
        if(doc) {
            res.status(200).json(doc);
        } else {
            res.status(404).json({message: "no valid entry found for provided ID"});
        }
    })
    .catch((err)=> {
        console.log(err);
        res.status(500).json({err: err});
    });
}

exports.events_post = function(req, res, next) {
    const event = new Event({
        _id: new mongoose.Types.ObjectId(),
        name: req.body.name,
        location: req.body.location,
        createdBy: req.body.createdBy,
        invitedUsers: req.body.invitedUsers,
        completed: req.body.completed,
        startDate: req.body.startDate,
        endDate: req.body.endDate
    });
    event
        .save()
        .then(function(result) {
            console.log(result);
            res.status(201).json({
                event: event
            })
        }).catch(function(err) {
        console.log(err);
        console.log(event);
        res.status(500).json({
            error:err
        })
    });
}

exports.events_delete = function(req, res, next) {
    const id = req.params.eventId;
    Event.remove({_id: id})
        .exec()
        .then(function(result) {
            res.status(200).json(result);
        })
        .catch(function(err) {
            res.status(500).json({
                error: err
            });
        });
}

exports.events_patch = function(req, res, next) {
    const id = req.params.eventId;
    const updateOps = {};
    for(const ops of req.body) {
        updateOps[ops.propName] = ops.value;
    }
    Event.update({_id: id}, { $set: updateOps})
        .exec()
        .then(result => {
            console.log(result);
            res.status(200).json({result});
        })
        .catch(err => {
            console.log(err);
            res.status(500).json({error: err});
        });
}

exports.events_delete_all = (req, res, next) => {
    Event.remove()
        .exec()
        .then(function(result) {
            res.status(200).json(result);
        })
        .catch(function(err) {
            res.status(500).json({
                error: err
            });
        });
}

