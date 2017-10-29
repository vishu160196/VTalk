'use strict'

var pool=require('./userController').pool;

exports.searchUser=function(req, res){
    var contact = req.query.contact;

    //fetch data from db corresponding to carName
    pool.query("SELECT name, username FROM user_info WHERE name= $1;", [contact], function (err, result) {

        if (err) {
            res.status(500).send(JSON.stringify([{message:err.toString()}]));
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
};

exports.getMessages=function(req, res){
    var id = req.query.id;
    //fetch data from db corresponding to carName
    pool.query("SELECT content, sender_id FROM message_cache WHERE receiver_id= $1;", [id], function (err, result) {

        if (err) {
            res.status(500).send(JSON.stringify([{message:err.toString()}]));
        }

        else {
            //fill the car object's properties from that data
            var messages=[];
            var length = result.rows.length;
            for(var i=0;i<length;i++){
                messages.push({
                    content:result.rows[i].content,
                    sender_id:result.rows[i].sender_id,
                    time:result.rows[i].time_sent
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
};

exports.sendMessage=function (req, res) {
    var content=req.body.content;
    var senderId=req.body.senderId;
    var receiverId=req.body.receiverId;

    pool.query("insert into message_cache(content, sender_id, receiver_id) values($1, $2, $3) ;", [content, senderId, receiverId], function (err, result) {

        if (err) {
            res.status(500).send(JSON.stringify(err.toString()));
        }

        else {
            res.status(200).send(JSON.stringify('Message sent successfully'));
        }
    });
};
