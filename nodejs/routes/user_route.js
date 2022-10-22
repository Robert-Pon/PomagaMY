const express = require('express');
const router = new express.Router();

const validator = require('validator');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');

const { ObjectID } = require('bson');
const request = require('request');
const axios = require('axios');

const localizationModel = require('../models/localization_model');
const userModel = require('../models/user_model');
const chatModel = require('../models/chat_model')
const anModel = require('../models/an_model');

const createUserToken = async (id, tokenID) => {
    return await jwt.sign({_id: id}, "ORYGINAL_STRING"+tokenID.toString()+"ORYGINAL_STRING", {expiresIn: "10d"})
}

const multer = require('multer');
const upload = multer({
    fieldSize: 10000000
});

const GEO_API_KEY = "API_KEY_API_KEY";

router.post('/api/users/register', async (req, res)=>{  
    if(validator.isEmail(req.body.email)){
        const time1 = (new Date()).getTime();
        req.body.password = await bcrypt.hash(req.body.password, 13);
        const newUser = new userModel({since: time1, email: req.body.email, password: req.body.password, surname: req.body.surname, name: req.body.name, location: await geolocation(req.body.street, req.body.village, req.body.region), description:""});
        const newTokenID = new ObjectID();
        const token = await createUserToken(newUser._id.toString(), newTokenID);
        const time = (new Date()).getTime()+86400000;
        if(newUser.tokens === undefined){
            newUser.tokens = [];
        }
        newUser.tokens.push({token: await bcrypt.hash(token, 8), newTokenID: newTokenID, time: time});
        await newUser.save();
        res.status(200).send({type:1, data:{token: token, tokenID: newTokenID, userID: newUser._id}})
    }else{
        res.status(200).send({type:0, data:{id: 080, message:"Wrong e-mail"}});
    }

})

router.post('/api/users/login', async (req, res)=>{
    if(validator.isEmail(req.body.email)){
        const user = await userModel.findOne({email: req.body.email});
        if(await bcrypt.compare(req.body.password, user.password)){
             const newTokenID = new ObjectID();
             const token = await createUserToken(user.id, newTokenID);
             const time = (new Date()).getTime()+86400000;
             if(user.tokens === undefined){
                user.tokens = [];
             }
             user.tokens.push({token: bcrypt.hash(token, 8), newTokenID: newTokenID, time: time});
             user.tokens = user.tokens.filter(token=>{
                if(token != null && token.time>(new Date()).getTime()){
                    return true;
                }else{
                    return false;
                }
             })
             await user.save();
             
             res.status(200).send({type:1, data:{token: token, tokenID: newTokenID, userID: user._id, email: user.email, name: user.name, surname: user.surname, description: user.description}})
             
        }
    }else{
        res.status(200).send({type:0, data:{id: 080, message:"Wrong e-mail"}})
    }

})


router.post("/api/chat/getChats", async (req, res)=>{
    const lastID = req.body.lastID;
    const verify = jwt.verify(req.body.token, "ORYGINAL_STRING"+req.body.token_ID+"ORYGINAL_STRING");
    if(!lastID){
        const chats = (await userModel.findOne({_id: verify._id})).chats;
        res.send({type: 0, data:{chats}})
    }else{
        
    }

    
})

router.post('/api/chat/get_messages',async (req, res)=>{
    const lastID = req.body.lastID;
    const verify = jwt.verify(req.body.token, "ORYGINAL_STRING"+req.body.token_ID+"ORYGINAL_STRING");
    if(!lastID){
        const messages = await chatModel.find({collaborators:{ $all: [{$elemMatch: {id: req.body.userID}}, {$elemMatch: {id: verify._id}}]}}, {messages: {$slice:-50}})
        res.send({type: 0, data:{messages}})
    }else{
        
    }
})

router.post("/api/chat/get_chats", async (req, res)=>{
    const verify = jwt.verify(req.body.token, "ORYGINAL_STRING"+req.body.token_ID+"ORYGINAL_STRING");
    const lastID = req.body.lastID;
    if(!lastID){
        const chats = await chatModel.find({collaborators:{ $all: [{$elemMatch: {id: verify._id}}]}},{messages: {$slice:-1}})
        chats.forEach((e, i)=>{
            if(e.collaborators[0].id == verify._id){
                chats[i].collaborators[0] = e.collaborators[1];
            }
        })
        res.send({type: 0, data: {chats}})

    }else{
        const chats = await chatModel.find({collaborators:{ $all: [{$elemMatch: {id: verify._id}}]}, _id: {$gt: lastID}}, {messages: {$slice:-1}})
        chats.forEach((e, i)=>{
            if(e.collaborators[0].id == verify._id){
                chats[i].collaborators[0] = e.collaborators[1];
            }
        })
        res.send({type: 0, data: {chats}})

    }
})


router.post('/api/profile/set-data', async (req, res)=>{
    const verify = jwt.verify(req.body.token, "ORYGINAL_STRING"+req.body.token_ID+"ORYGINAL_STRING");
    delete req.body.token;
    delete req.body.token_ID;
    const updated = await userModel.findOneAndUpdate({_id: verify._id}, req.body);

    res.send({type: 0, data: {updated}})
})

router.post('/api/profile/set-password', async (req, res)=>{
    const verify = jwt.verify(req.body.token, "ORYGINAL_STRING"+req.body.token_ID+"ORYGINAL_STRING");
    delete req.body.token;
    delete req.body.token_ID;
    if(req.body.p1==req.body.p2){
        const newPassword = await bcrypt.hash(req.body.p1, 13);
        const updated = await userModel.findOneAndUpdate({_id: verify._id}, {password: newPassword});
    
        res.send({type: 0, data: {updated}})
    }
   
})

router.post('/api/profile/set-profile', async (req, res)=>{
    const verify = jwt.verify(req.body.token, "ORYGINAL_STRING"+req.body.token_ID+"ORYGINAL_STRING");
    
        const profile = req.files[0].buffer;
        const updated = await userModel.findOneAndUpdate({_id: verify._id}, {profile: profile});
    
        res.send({type: 0, data: {updated}})
    
   
})

const geolocation = async (street, village, region) => {
    const path = `http://api.geocodify.com/v2/geocode?api_key=${GEO_API_KEY}&q=${street},${village},${region}`.toString();
    const response = await axios.get(path);
    const features = response.data.response.features[0];

    const localization = await localizationModel.findOne({lat: features.geometry.coordinates[1], lon: features.geometry.coordinates[0]})
    
    if(localization==null) {
        const model = new localizationModel({lat: features.geometry.coordinates[1], lon: features.geometry.coordinates[0], street: features.properties.street, village: features.properties.localadmin, region: features.properties.county, district: features.properties.region});
        model.save();
        return  model._id;
    }else{
        return localization._id;
    }

}

module.exports = router;