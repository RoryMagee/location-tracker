var mongoose = require('mongoose');

const eventSchema = mongoose.Schema({
    _id: mongoose.Schema.Types.ObjectId,
    name: {type: String, required: true},
    location: {type: mongoose.Schema.ObjectId, ref: 'Location'},
    startDate: {type: Date, default: Date.now},
    endDate: {type: Date, default: Date.now},
    createdBy: {type: mongoose.Schema.Types.ObjectId, required: true, ref: 'User'},
    invitedUsers: [{
        name: {type : mongoose.Schema.Types.ObjectId, ref: 'User'},
        attended: {type: Boolean, default: 'false'}
    }],
    completed: {type: Boolean, default: false}

});

module.exports = mongoose.model('Event', eventSchema);