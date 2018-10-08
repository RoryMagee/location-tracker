var express = require('express');
var router = express.Router();
var tokenAuth = require('../middleware/token-auth');
var locationController = require('../controllers/locations');
var multer = require('multer');
var upload = multer({dest: 'uploads/'});

router.get('/', locationController.locations_get_all);

router.get('/:locationId', locationController.locations_get);

router.post('/', tokenAuth, locationController.locations_post);

//router.patch('/:locationId', tokenAuth, locationController.locations_patch);

router.delete('/', tokenAuth, locationController.locations_delete);

//router.delete('/:locationId', tokenAuth, locationController.locations_delete);

module.exports = router;