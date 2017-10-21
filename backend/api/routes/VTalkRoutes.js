'use strict';



module.exports = function(app) {

    var VTalk = require('../controllers/VtalkController'),
    userHandlers = require('../controllers/userController');

	// VTalk Routes
	app.route('/auth/create-user')
		.post(userHandlers.register);

	app.route('/auth/login')
		.post(userHandlers.login);

    app.route('/auth/login-check')
		.get(userHandlers.loginCheck);

	app.route('/auth/user-exists')
    		.get(userHandlers.loginRequired, userHandlers.userExists);

    app.route('/search')
		.get(userHandlers.loginRequired, VTalk.searchUser);

    app.route('/get-messages')
		.get(userHandlers.loginRequired, VTalk.getMessages);

    app.route('/send-message')
		.post(userHandlers.loginRequired, VTalk.sendMessage);
};
