'use strict'

var crypto = require('crypto');

var Pool = require('pg').Pool;
var jwt=require('../../server').jwt;


// create a config to configure both pooling behavior
// and client options
var config = {
    user: process.env.PGUSER, //env var: PGUSER
    database: process.env.PGDATABASE, //env var: PGDATABASE
    password: process.env.DB_PASSWORD, //env var: PGPASSWORD
    host: 'localhost', // Server hosting the postgres database
    port: process.env.PGPORT, //env var: PGPORT
    max: 10, // max number of clients in the pool
    idleTimeoutMillis: 30000, // how long a client is allowed to remain idle before being closed
};

var secret=crypto.randomBytes(32).toString();
exports.secret=secret;

var pool = new Pool(config);
exports.pool=pool;

function hash(userPassword, salt) {
    var hashedPassword;

    hashedPassword = crypto.pbkdf2Sync(userPassword, salt, 100000, 512, 'sha512').toString('hex');

    return ['pbkdf2Sync', salt, hashedPassword].join('#');
}

exports.register = function(req, res) {

    //extract username and password sent by the user from message body
    var userName = req.body.userName;
    var userPassword = req.body.userPassword;
    var salt = crypto.randomBytes(256).toString('hex');

    userPassword = hash(userPassword, salt);
    var name = req.body.name;
    var email = req.body.email;

    //create a new entry in users table
    pool.query("insert into user_info(name,username,password,email) values($1, $2, $3, $4);", [name, userName, userPassword, email], function (err, result) {
        if (err)
            res.status(500).send(JSON.stringify({message:err.toString()}));

        else
            res.status(200).send(JSON.stringify({message:'User created successfully please login to continue'}));
    });
};

exports.login=function(req, res){
    var userName = req.body.userName;
    var userPassword = req.body.userPassword;

    //start a session only if there is no earlier session
    if(!req.user){
        pool.query("SELECT * FROM user_info WHERE username = $1", [userName], function (err, result){
           if (err)
            res.status(500).send(JSON.stringify({message:err.toString()}));

           else {
            if (result.rows.length === 0)
                res.status(404).send(JSON.stringify({message:'Username not found'}));

            else {
                var actualPassPhrase = result.rows[0].password;
                var salt = actualPassPhrase.split('#')[1];

                userPassword = hash(userPassword, salt);

                if (userPassword === actualPassPhrase)
                {
                    var user={
                        username:result.rows[0].username,
                        id:result.rows[0].id
                    };

                    return res.json({ token: jwt.sign(user, secret, {expiresInMinutes: 60*24}) });
                }

                else
                    res.status(401).send(JSON.stringify({message:'Incorrect password'}));
        }
        }
        });
    }

    else{
        res.status(403).send(JSON.stringify({message:`You are already logged in as ${req.user.username} first logout then try logging in again`}));
    }
};

exports.loginCheck=function(req, res){
    if(req.user)
        res.status(200).send(JSON.stringify(req.user));
    else {
        res.status(200).send(JSON.stringify({message:'You are not logged in'}));
    }
};

exports.logout=function(req, res){
    if(req.user){
        req.user=undefined;
    	res.status(200).send(JSON.stringify({message:'Logged out successfully'}));
    }

    else
        res.status(403).send(JSON.stringify({message:'Forbidden'}));
};

exports.loginRequired = function(req, res, next) {
  if (req.user) {
    next();
  } else {
    return res.status(401).json({ message: 'Unauthorized user!' });
  }
};

exports.userExists=function (req, res) {
    var username=req.params.username;

    pool.query("select username, id from user_info where username=$1;", [username], function (err, result) {

        if (err) {
            res.status(500).send(JSON.stringify({message:err.toString()}));
        }

        else {
            res.status(200).send(JSON.stringify({exists:true, id:result.rows[0].id}));
        }
    });
}
