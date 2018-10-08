var express = require('express');
var router = express.Router();
var userController = require('../controllers/users');
var tokenAuth = require('../middleware/token-auth');

router.post('/signup', userController.users_post_signup);

router.post('/login', userController.users_post_login);

router.patch('/:userId', userController.users_location_patch);

router.get('/', userController.users_get_all);

router.delete('/', tokenAuth, userController.users_delete_all);

router.delete('/:userId', tokenAuth, userController.users_delete);

module.exports = router;