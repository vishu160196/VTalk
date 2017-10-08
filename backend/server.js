// JavaScript source code

// node_modules
var express = require('express');
var morgan = require('morgan');
var path = require('path');
var pool = require('pg').Pool;
var crypto = require('crypto');
var bodyParser = require('body-parser');
var session = require('express-session');

var app=express();

app.use(morgan('combined'));
app.use(bodyParser.json());
app.use(session({
    secret: crypto.randomBytes(32).toString(),
    cookie: { maxAge: 1000 * 60 * 24 * 60 * 30 }
}));
app.use(bodyParser.json());

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

var pool = new pool(config);

var port = 8080; // Use 8080 for local development because you might already have apache running on 80
app.listen(port, function () {
    console.log(`IMAD course app listening on port ${port}!`);
});


/*---------------------------------------------------------------------Create new user-------------------------------------------------------------*/

function hash(userPassword, salt) {
    var hashedPassword;

    hashedPassword = crypto.pbkdf2Sync(userPassword, salt, 100000, 512, 'sha512').toString('hex');

    return ['pbkdf2Sync', salt, hashedPassword].join('#');
}

app.post('/create-user', function (req, res) {
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
            res.status(500).send(err.toString());

        else
            res.status(200).send('User created succesfully. Please Login to continue');
    });
});
/*-------------------------------------------------------------------------------------------------------------------------------------------------*/

/*---------------------------------------------------------------------Login/Logout-------------------------------------------------------------*/

app.post('/login', function (req, res) {
    var userName = req.body.userName;
    var userPassword = req.body.userPassword;

    //start a session only if there is no earlier session
    if(!req.session.auth){
        pool.query("SELECT * FROM user_info WHERE username = $1", [userName], function (err, result){
           if (err)
            res.status(500).send(err.toString());

           else {
            if (result.rows.length === 0)
                res.status(404).send('Username not found');

            else {
                var actualPassPhrase = result.rows[0].password;
                var salt = actualPassPhrase.split('#')[1];

                userPassword = hash(userPassword, salt);

                if (userPassword === actualPassPhrase)
                {
                    //start session
                    req.session.auth = {userName: result.rows[0].username};

                    var user={
                        username:result.rows[0].username,
                        id:result.rows[0].id,
                        cookie:req.session
                    };
                    res.status(200).send(JSON.stringify(user));
                }

                else
                    res.status(401).send("Incorrect password");
        }
        }
        });
    }

    else{
        res.status(403).send(`You are already logged in as ${req.session.auth.userName} first logout then try logging in again`);
    }

});

app.get('/login-check', function (req, res) {
    if(req.session.auth)
        res.status(200).send(req.session.auth.userName);
    else {
        res.status(200).send('You are not logged in');
    }
});

app.get('/logout', function (req, res) {
    if(req.session && req.session.auth && req.session.auth.userName){
        delete req.session.auth;
    	res.status(200).send('Logged out succesfully');
    }

    else
        res.status(403).send('Forbidden request');
});

/*-------------------------------------------------------------------------------------------------------------------------------------------------*/

/*---------------------------------------------------------------------Search people-------------------------------------------------------------*/

app.get('/search', function(req, res){
    var contact = req.query.contact;

    //fetch data from db corresponding to carName
    pool.query("SELECT name, username FROM user_info WHERE name= ($1);", [contact], function (err, result) {

        if (err) {
            res.status(500).send(err.toString());
        }

        else {
            //fill the car object's properties from that data
            var contacts=[];
            var length = result.rows.length;
            for(var i=0;i<length;i++){
                contacts.push({
                    name:contact,
                    username:result.rows[i].username
                });
            }

            //pass the object as argument to createTemplate() and send back the return value
            res.status(200).send(JSON.stringify(contacts));
        }
    });
});

/*------------------------------------------------------------------------------------------------------------------------------------------*/

/*---------------------------------------------------------------------Fetch Messages-------------------------------------------------------------*/
app.get('/get-messages', function(req, res){
    var id = req.query.id;
    //fetch data from db corresponding to carName
    pool.query("SELECT content, sender_id FROM message_cache WHERE receiver_id= ($1);", [id], function (err, result) {

        if (err) {
            res.status(500).send(err.toString());
        }

        else {
            //fill the car object's properties from that data
            var messages=[];
            var length = result.rows.length;
            for(var i=0;i<length;i++){
                messages.push({
                    content:result.rows[i].content,
                    sender_id:result.rows[i].sender_id
                });
            }

            //pass the object as argument to createTemplate() and send back the return value
            res.status(200).send(JSON.stringify(messages));

            pool.query("DELETE FROM message_cache WHERE id= ($1);", [id], function (err, result) {

                if (err) {
                    console.log(`delete all messages with receiver_id ${id}`);
                }

            });
        }
    });
});
/*----------------------------------------------------------------------------------------------------------------------------------------------*/

/*---------------------------------------------------------------------Send Message-------------------------------------------------------------*/

app.post('/send-message', function(req,res){
    var content=req.body.content;
    var senderId=req.body.senderId;
    var receiverId=req.body.receiverId;

    pool.query("insert into message_cache(content, sender_id, receiver_id) values($1, $2, $3) ;", [content, senderId, receiverId], function (err, result) {

        if (err) {
            res.status(500).send(err.toString());
        }

        else {
            res.status(200).send('message sent successfully');
        }
    });
});

/*----------------------------------------------------------------------------------------------------------------------------------------------*/
