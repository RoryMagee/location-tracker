var mongoose = require('mongoose');

const locationSchema = mongoose.Schema({
    _id: mongoose.Schema.Types.ObjectId,
    name: {type: String, required: true},
    altitude: {type: Number, required: true},
    P1Lat: {type: Number, required: true},
    P1Long: {type: Number, required: true},
    P2Lat: {type: Number, required: true},
    P2Long: {type: Number, required: true},
    P3Lat: {type: Number, required: true},
    P3Long: {type: Number, required: true},
    P4Lat: {type: Number, required: true},
    P4Long: {type: Number, required: true}
});

module.exports = mongoose.model('Location', locationSchema);