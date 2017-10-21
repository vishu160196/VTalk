// JavaScript source code

// node_modules
var express = require('express');
var morgan = require('morgan');
var bodyParser = require('body-parser');
var jwt = require('jsonwebtoken');

exports.jwt=jwt;

var app=express();
var secret=require('./api/controllers/userController').secret;

app.use(morgan('combined'));
app.use(bodyParser.json());

app.use(function(req, res, next) {
  if (req.headers && req.headers.authorization && req.headers.authorization.split(' ')[0] === 'JWT') {
    jwt.verify(req.headers.authorization.split(' ')[1], secret, function(err, decode) {
      if (err) req.user = undefined;
      else req.user = decode;
      next();
    });
  } else {
    req.user = undefined;
    next();
  }
});

var port = 8080; // Use 8080 for local development because you might already have apache running on 80

var routes = require('./api/routes/VTalkRoutes');
routes(app);

app.use(function(req, res) {
  res.status(404).send({ url: req.originalUrl + ' not found' })
});

app.listen(port, function () {
    console.log(`VTalk app listening on port ${port}!`);
});


// /*---------------------------------------------------------------------Create new user-------------------------------------------------------------*/
//
// function hash(userPassword, salt) {
//     var hashedPassword;
//
//     hashedPassword = crypto.pbkdf2Sync(userPassword, salt, 100000, 512, 'sha512').toString('hex');
//
//     return ['pbkdf2Sync', salt, hashedPassword].join('#');
// }
//
// app.post('/create-user', function (req, res) {
//     //extract username and password sent by the user from message body
//     var userName = req.body.userName;
//     var userPassword = req.body.userPassword;
//     var salt = crypto.randomBytes(256).toString('hex');
//
//     userPassword = hash(userPassword, salt);
//     var name = req.body.name;
//     var email = req.body.email;
//
//     //create a new entry in users table
//     pool.query("insert into user_info(name,username,password,email) values($1, $2, $3, $4);", [name, userName, userPassword, email], function (err, result) {
//         if (err)
//             res.status(500).send(JSON.stringify({message:err.toString()}));
//
//         else
//             res.status(200).send(JSON.stringify({message:'User created successfully please login to continue'}));
//     });
// });
// /*-------------------------------------------------------------------------------------------------------------------------------------------------*/
//
// /*---------------------------------------------------------------------Login/Logout-------------------------------------------------------------*/
//
// app.post('/login', function (req, res) {
//     var userName = req.body.userName;
//     var userPassword = req.body.userPassword;
//
//     //start a session only if there is no earlier session
//     if(!req.session.auth){
//         pool.query("SELECT * FROM user_info WHERE username = $1", [userName], function (err, result){
//            if (err)
//             res.status(500).send(JSON.stringify({message:err.toString()}));
//
//            else {
//             if (result.rows.length === 0)
//                 res.status(404).send(JSON.stringify({message:'Username not found'}));
//
//             else {
//                 var actualPassPhrase = result.rows[0].password;
//                 var salt = actualPassPhrase.split('#')[1];
//
//                 userPassword = hash(userPassword, salt);
//
//                 if (userPassword === actualPassPhrase)
//                 {
//                     //start session
//                     req.session.auth = {userName: result.rows[0].username};
//
//                     var expires = moment().add('days', 7).valueOf();
//                     var token = jwt.encode({
//                       iss: result.rows[0].id,
//                       exp: expires
//                     }, app.get('jwtTokenSecret'));
//
//                     var user={
//                         username:result.rows[0].username,
//                         id:result.rows[0].id
//                     };
//
//                     res.status(200).json({
//                       token : token,
//                       expires: expires,
//                       user: user.toJSON()
//                     });
//                 }
//
//                 else
//                     res.status(401).send(JSON.stringify({message:'Incorrect password'}));
//         }
//         }
//         });
//     }
//
//     else{
//         res.status(403).send(JSON.stringify({message:`You are already logged in as ${req.session.auth.userName} first logout then try logging in again`}));
//     }
//
// });
//
// app.get('/login-check', function (req, res) {
//     if(req.session.auth)
//         res.status(200).send(JSON.stringify({userName:req.session.auth.userName}));
//     else {
//         res.status(200).send(JSON.stringify({message:'You are not logged in'}));
//     }
// });
//
// app.get('/logout', function (req, res) {
//     if(req.session && req.session.auth && req.session.auth.userName){
//         delete req.session.auth;
//     	res.status(200).send(JSON.stringify({message:'Logged out successfully'}));
//     }
//
//     else
//         res.status(403).send(JSON.stringify({message:'Forbidden'}));
// });
//
// /*-------------------------------------------------------------------------------------------------------------------------------------------------*/
//
// /*---------------------------------------------------------------------Search people-------------------------------------------------------------*/
//
// app.get('/search', function(req, res){
//     var contact = req.query.contact;
//
//     //fetch data from db corresponding to carName
//     pool.query("SELECT name, username FROM user_info WHERE name= ($1);", [contact], function (err, result) {
//
//         if (err) {
//             res.status(500).send(JSON.stringify({message:err.toString()}));
//         }
//
//         else {
//             //fill the car object's properties from that data
//             var contacts=[];
//             var length = result.rows.length;
//             for(var i=0;i<length;i++){
//                 contacts.push({
//                     name:contact,
//                     username:result.rows[i].username
//                 });
//             }
//
//             //pass the object as argument to createTemplate() and send back the return value
//             res.status(200).send(JSON.stringify(contacts));
//         }
//     });
// });
//
// /*------------------------------------------------------------------------------------------------------------------------------------------*/
//
// /*---------------------------------------------------------------------Fetch Messages-------------------------------------------------------------*/
// app.get('/get-messages', function(req, res){
//     var id = req.query.id;
//     //fetch data from db corresponding to carName
//     pool.query("SELECT content, sender_id FROM message_cache WHERE receiver_id= ($1);", [id], function (err, result) {
//
//         if (err) {
//             res.status(500).send(JSON.stringify({message:err.toString()}));
//         }
//
//         else {
//             //fill the car object's properties from that data
//             var messages=[];
//             var length = result.rows.length;
//             for(var i=0;i<length;i++){
//                 messages.push({
//                     content:result.rows[i].content,
//                     sender_id:result.rows[i].sender_id
//                 });
//             }
//
//             //pass the object as argument to createTemplate() and send back the return value
//             res.status(200).send(JSON.stringify(messages));
//
//             pool.query("DELETE FROM message_cache WHERE id= ($1);", [id], function (err, result) {
//
//                 if (err) {
//                     console.log(`delete all messages with receiver_id ${id}`);
//                 }
//
//             });
//         }
//     });
// });
// /*----------------------------------------------------------------------------------------------------------------------------------------------*/
//
// /*---------------------------------------------------------------------Send Message-------------------------------------------------------------*/
//
// app.post('/send-message', function(req,res){
//     var content=req.body.content;
//     var senderId=req.body.senderId;
//     var receiverId=req.body.receiverId;
//
//     pool.query("insert into message_cache(content, sender_id, receiver_id) values($1, $2, $3) ;", [content, senderId, receiverId], function (err, result) {
//
//         if (err) {
//             res.status(500).send(JSON.stringify({message:err.toString()}));
//         }
//
//         else {
//             res.status(200).send(JSON.stringify({message:'Message sent successfully'}));
//         }
//     });
// });
//
// /*----------------------------------------------------------------------------------------------------------------------------------------------*/
//
// /*---------------------------------------------------------------------Check user exists-------------------------------------------------------------*/
//
// app.get('/user-exists', function(req,res){
//     var username=req.params.username;
//
//     pool.query("select username, id from user_info where username=$1;", [username], function (err, result) {
//
//         if (err) {
//             res.status(500).send(JSON.stringify({message:err.toString()}));
//         }
//
//         else {
//             res.status(200).send(JSON.stringify({exists:true, id:result.rows[0].id}));
//         }
//     });
// });
//
// /*-------------------------------------------------------------------------------------------------------------------------------------------*/
