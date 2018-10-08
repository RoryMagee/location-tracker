var jwt = require('jsonwebtoken');

module.exports = (req, res, next) => {
    try {
        const decoded_token = jwt.verify(req.headers.authorization.split(" ")[1], process.env.TOKEN_KEY);
        req.userData = decoded_token;
        next();
    } catch(error) {
        return res.status(401).json({
            message: "Auth failed"
        });
    }
};