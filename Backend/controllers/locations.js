const Location = new require('../models/location');
var mongoose = require('mongoose');
var multer = require('multer');
var upload = multer({dest: 'uploads/'});

exports.locations_get_all = (req, res, next)=> {
    Location.find()
        .select('_id name P1Lat P1Long P2Lat P2Long P3Lat P3Long P4Lat P4Long altitude')
        .exec()
        .then((docs)=> {
            if(docs.length >=0) {w
                res.status(200).json(docs);
            } else {
                res.status(404).json ({
                    message: "No locations"
                });
            }
        })
        .catch((err)=> {
            res.status(500).json({
                error: err
            })
        });
}

exports.locations_get = (req, res, next) => {
    const id = req.params.locationId;
    Location.findById(id)
        .select('name P1Lat P1Long P2Lat P2Long P3Lat P3Long P4Lat P4Long altitude')
        .exec()
        .then((docs)=> {
            const response = {
                count: docs.length,
                locations: docs.map(doc => {
                    return {
                        name: doc.name,
                        longitude: doc.longitude,
                        latitude: doc.latitude,
                        altitude: doc.altitude
                    }
                })
            };
            if(docs.length >=0) {
                res.status(200).json(response);
            } else {
                res.status(404).json ({
                    message: "No locations"
                });
            }
        })
        .catch((err)=> {
            res.status(500).json({
                error: err
            })
        });
}

exports.locations_post = (req, res, next) => {
    const location = new Location({
        _id: new mongoose.Types.ObjectId(),
        name: req.body.name,
        P1Lat: req.body.P1Lat,
        P1Long: req.body.P1Long,
        P2Lat: req.body.P2Lat,
        P2Long: req.body.P2Long,
        P3Lat: req.body.P3Lat,
        P3Long: req.body.P3Long,
        P4Lat: req.body.P4Lat,
        P4Long: req.body.P4Long,
        altitude: req.body.altitude
    });
    location
        .save()
        .then((result)=> {
            console.log(result);
            res.status(201).json({
                message: "Location Created",
                location: location
            })
        })
        .catch((err)=> {
            console.log(err);
            res.status(500).json({
                error: err
            })
        });
}

exports.locations_delete = (req, res, next) => {
    Location.remove()
        .exec()
        .then((result)=> {
            res.status(200).json({
                message: "All locations deleted"
            })
        });
}

