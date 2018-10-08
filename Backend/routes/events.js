var express = require('express');
var router = express.Router();
var tokenAuth = require('../middleware/token-auth');
var eventController = require('../controllers/events');


router.get('/', eventController.events_get_all);

router.get('/:userId', tokenAuth, eventController.events_get_user_events);

router.get('/:eventId', eventController.events_get);

router.get('/all/:userId', tokenAuth, eventController.events_get_user_events_all);

router.post('/', tokenAuth, eventController.events_post);

router.delete('/:eventId',tokenAuth, eventController.events_delete);

router.patch('/:eventId', tokenAuth, eventController.events_patch);

router.delete('/', tokenAuth, eventController.events_delete_all);



module.exports = router;