const express = require('express');
const { r } = require('tar');
const chat_model = require('../models/chat_model')
const router = new express.Router();
const photo_model = require('../models/photos_model');
const multer = require('multer');
const mongoose = require('mongoose');
const jwt = require('jsonwebtoken');
const upload = multer(
    {
        fieldSize: 10000000
    }
)

router.post('/api/chat/uploadImage', async (req, res)=>{
    const date = new Date();
    const verify = jwt.verify(req.body.token, "ORYGINAL_STRING"+req.body.token_ID+"ORYGINAL_STRING");
    const myID = verify._id;
    const userID = req.body.userID;
    const type = "image"
    const photo = new photo_model({photo: req.files[0].buffer, user: myID, type: type, ext: "png"});
    const id = new mongoose.Types.ObjectId();
    photo.save();
    const chat = await chat_model.findOneAndUpdate({collaborators:{ $all: [{$elemMatch: {id: userID}}, {$elemMatch: {id: myID}}]}},{$push:{messages:{
        _id:id,
        user: myID,
        content: photo._id,
        state: 0,
        type: type, 
        name: req.files[0].filename,
        time: date.getTime()
    }}})
    if(chat){
        io.to(userID).emit('newMessage',{room: myID, message:{
            _id:id,
            user: myID,
            content: photo._id,
            state: 0,
            type: type, 
            name: req.files[0].filename
        }})
        io.to(myID).emit('newMessage',{room: userID, message:{
            _id:id,
            user: myID,
            content: photo._id,
            state: 0,
            type: type, 
            name: req.files[0].filename,
            time: date.getTime()

        }})
    }

    res.send(type);
})
router.get("/api/chat/get-chat-image/:id/:token/:token_ID", async (req, res) => {
    const verify = jwt.verify(req.body.token, "ORYGINAL_STRING"+req.body.token_ID+"ORYGINAL_STRING");

    const param = req.params.id;
    const announcements2 = await photo_model.findOne({_id: param});
    res.setHeader("Content-Type", "image/png").send(announcements2.photo);
})


module.exports = router

