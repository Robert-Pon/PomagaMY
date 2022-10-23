const socketio = require('socket.io');
const mongoose = require('mongoose');
const { ObjectID } = require('bson');
const jwt = require('jsonwebtoken');
const chat_model = require('../models/chat_model')
const user_model = require('../models/user_model')
var socket = null;

const socketSetter = (server) => {
    io = socketio(server);
    let room = "";

    io.on('connection', (socket)=>{
        let ID = "";
        

        socket.on('login', async ({token, token_id})=>{
            const verify = jwt.verify(req.body.token, "ORYGINAL_STRING"+req.body.token_ID+"ORYGINAL_STRING");
            socket.join(verify._id);
            ID = verify._id;
            socket.emit('connected', "Connected to room "+verify._id)
        })

        socket.on('disconnect', async ()=>{
            await user_model.findOneAndUpdate({_id: ID}, {staus: {active: false, time: (new Date()).getTime()}});
        })

        socket.on('room', async ({userID, lastID})=>{
            const response = await chat_model.find({collaborators:{ $all: [{$elemMatch: {id: userID}}, {$elemMatch: {id: ID}}]}})
            if(response.length>0){
                if(!lastID){
                    const messages = await chat_model.find({collaborators:{ $all: [{$elemMatch: {id: userID}}, {$elemMatch: {id: ID}}]}}, {messages: {$slice:-50}})
                    socket.emit("lastMessages", {more: false, messages})
                }else{
                    const messages = await chat_model.find({collaborators:{ $all: [{$elemMatch: {id: userID}}, {$elemMatch: {id: ID}}]}}, {messages: {$gt: lastID}})
                    socket.emit("lastMessages", {more: true, messages})
                }
            }else{

            }

        })

        

        socket.on('sendMessage', async ({userID, message})=>{
            const response = await chat_model.find({collaborators:{ $all: [{$elemMatch: {id: userID}}, {$elemMatch: {id: ID}}]}})
            const date = new Date();
                if(response.length==0 && ID!=userID){
                    const getUSER1 = await user_model.findOne({_id: ID})
                    const getUSER2 = await user_model.findOne({_id: userID})
                    const us1Name = getUSER2.name+" "+getUSER2.surname;
                    const us2Name = getUSER1.name+" "+getUSER1.surname;
                    const user1 = await user_model.findOneAndUpdate({_id: ID}, {$push: {chats:{id:userID, message: message, time: date.getTime(), seen: false, name: (getUSER2.name+" "+getUSER2.surname)}}})
                    const user2 = await user_model.findOneAndUpdate({_id: userID}, {$push: {chats:{id:ID, message: message, time: date.getTime(), seen: false, name: (getUSER1.name+" "+getUSER1.surname)}}})
                    const id = new ObjectID();
                    const newConversation = new chat_model({
                        collaborators: [
                            {id: userID, nick: user2.name+" "+user2.surname},
                            {id: ID, nick: user1.name+" "+user1.surname}
                        ],
                        messages: [{
                            _id:id,
                            user: ID,
                            content: message,
                            state: 0,
                            type: "text",
                            time: date.getTime()
                        }]
                    })
                    await newConversation.save();
                    io.to(ID).emit('newConversation', {byMe: true, newConversation: {_id: "newConversation._id",id:userID, message: message, time: date.getTime(), seen: false, name: getUSER2.name+" "+getUSER2.surname}, messages: [{
                        _id:"id",
                        user: ID,
                        content: message,
                        state: 0,
                        type: "text",
                        time: date.getTime()
                    }]})
                    io.to(userID).emit('newConversation',  {byMe: false, newConversation:{_id: "newConversation._id", id:ID, message: message, time: date.getTime(), seen: false, name: getUSER1.name+" "+getUSER1.surname},messages: [{
                        _id:"id",
                        user: ID,
                        content: message,
                        state: 0,
                        type: "text",
                        time: date.getTime()
                    }]})
                    
                    socket.to(userID).emit('newMessage', {room: ID, message:{
                        _id:id,
                        user: ID,
                        content: message,
                        state: 0,
                        type: "text",
                        time: date.getTime()
                    }}, 
                    (err, suc)=>{
                        if(err){
                            socket.emit('delivered', {result: false, message: "Emit error"})
                            io.to(ID).emit('newMessage', {room: userID, message:{
                                _id:id,
                                user: ID,
                                content: message,
                                state: 0,
                                type: "text",
                                time: date.getTime()
                            }})
                        }else{
                            io.to(ID).emit('newMessage', {room: userID, message:{
                                _id:id,
                                user: ID,
                                content: message,
                                state: 0,
                                type: "text",
                                time: date.getTime()
                            }})
                            socket.emit('delivered', {result: true, message: "Emit success"})
                        }
                    })
                    
                }else{
                    const user1 = await user_model.findOneAndUpdate({_id: ID, 'chats.id':userID}, {$set: {'chats.$.message': message, 'chats.$.time': date.getTime()}});
                    const user2 = await user_model.findOneAndUpdate({_id: userID, 'chats.id':ID}, {$set: {'chats.$.message': message, 'chats.$.time': date.getTime()}})
                     const id = new ObjectID();
                const mess1 = await chat_model.findOneAndUpdate({_id:response[0]._id}, 
                {$push:{messages:{
                    _id:id,
                    user: ID,
                    content: message,
                    state: 0,
                    type: "text",
                    time: date.getTime()
                }}});

                socket.to(userID).emit('newMessage', {room: ID, message:{
                    _id:id,
                    user: ID,
                    content: message,
                    state: 0,
                    type: "text",
                    time: date.getTime()
                }}, 
                (err, suc)=>{
                    if(err){
                        socket.emit('delivered', {result: false, message: "Emit error"})
                        io.to(ID).emit('newMessage', {room: userID, message:{
                            _id:id,
                            user: ID,
                            content: message,
                            state: 0,
                            type: "text",
                            time: date.getTime()
                        }})
                    }else{
                        io.to(ID).emit('newMessage', {room: userID, message:{
                            _id:id,
                            user: ID,
                            content: message,
                            state: 0,
                            type: "text",
                            time: date.getTime()
                        }})
                        socket.emit('delivered', {result: true, message: "Emit success"})
                    }
                })
                }
            
        })
    })
    return io;
}

module.exports = socketSetter

