const express = require('express');
const validator = require('validator');
const bcrypt = require('bcrypt');
const userModel = require('../models/user_model');
const chatModel = require('../models/chat_model')
const jwt = require('jsonwebtoken');
const { ObjectID } = require('bson');
const localizationModel = require('../models/localization_model');
const request = require('request');
const axios = require('axios');
const anModel = require('../models/an_model');
const evModel = require('../models/events_model');
const myHModel = require('../models/my_help_model');
const actModel = require('../models/action_model');
const ideaModel = require('../models/idea_model');
const statsModel = require('../models/stats_model');
const router = new express.Router();
const GEO_API_KEY = "API_KEY_API_KEY";
const articleModel = require('../models/artilce_model');

router.post('/api/announcements/get-articles' ,async (req, res)=>{
    const articles = await articleModel.find({});
    articles.forEach((e, i)=>{
        articles[i].description = "";
    })
    res.send({type: 0, data:{articles}})

});

router.post('/api/announcements/get-article' ,async (req, res)=>{
    const articles = await articleModel.findOne({_id: req.body.id});
    res.send({type: 0, data:{articles}})

});

router.post('/api/announcements/add_type_1' ,async (req, res)=>{

    req.body.things = JSON.parse(req.body.things);
    const date = new Date(req.body.endingDate+"T00:00:00");
    req.body.endingDate = date.getTime(); 
    const verify = jwt.verify(req.body.token, "ORYGINAL_STRING"+req.body.token_ID+"ORYGINAL_STRING");
    const user = await userModel.findOne({_id: verify._id});
    
    const loc1 = await localizationModel.findOne({_id: user.location});
    let loc;
    if(loc1.time_1<date.getTime()){
         loc = await localizationModel.findOneAndUpdate({_id: user.location}, {$inc:{is_1: 1}, time_1: date.getTime()})
    }
    delete req.body.token
    delete req.body.token_ID
    req.body.userID = verify._id;
    req.body.get = 0;
    const an = new anModel({type: 0, data: req.body, location: user.location, lat: loc1.lat, lon: loc1.lon});
    await an.save();
    res.send({type: 0, data: {}})

});

router.post('/api/announcements/update_type_1' ,async (req, res)=>{
    const verify = jwt.verify(req.body.token, "ORYGINAL_STRING"+req.body.token_ID+"ORYGINAL_STRING");
    const id = req.body.id;
    delete req.body.token
    delete req.body.token_ID
    delete req.body.id
    const date = new Date(req.body.endingDate+"T00:00:00");
    req.body.endingDate = date.getTime();
    req.body.userID = verify._id;
    req.body.things = JSON.parse(req.body.things)
    const updated = await anModel.findOneAndUpdate({_id: id}, {data: req.body}, {runValidators: true, new: true});
    res.send({type: 0, data: {}})

});

router.post('/api/announcements/add_type_2' ,async (req, res)=>{
    const date = new Date(req.body.date+"T00:00:00");
    req.body.date = date.getTime(); 
    const verify = jwt.verify(req.body.token, "ORYGINAL_STRING"+req.body.token_ID+"ORYGINAL_STRING");
    const user = await userModel.findOne({_id: verify._id});
    const loc1 = await localizationModel.findOne({_id: user.location});
    let loc;
    if(loc1.time_2<date.getTime()){
         loc = await localizationModel.findOneAndUpdate({_id: user.location}, {$inc:{is_2: 1}, time_2: date.getTime()})
    }    delete req.body.token
    delete req.body.token_ID
    req.body.userID = verify._id;
    req.body.location = user.location;
    req.body.get = 0;
    const an = new myHModel({lat: loc1.lat, lon: loc1.lon, ...req.body});
    await an.save();
    res.send({type: 0, data: {}})
});

router.post('/api/announcements/update_type_2' ,async (req, res)=>{
    const verify = jwt.verify(req.body.token, "ORYGINAL_STRING"+req.body.token_ID+"ORYGINAL_STRING");
    const id = req.body.id;
    delete req.body.token
    delete req.body.token_ID
    delete req.body.id
    const date = new Date(req.body.date+"T00:00:00");
    req.body.date = date.getTime();
    req.body.userID = verify._id;
    const updated = await myHModel.findOneAndUpdate({_id: id}, req.body, {runValidators: true, new: true});
    res.send({type: 0, data: {}})
});

router.post('/api/announcements/add_type_3' ,async (req, res)=>{
    const startDate =(new Date(req.body.startDate)).getTime();
    const endingDate =(new Date(req.body.endingDate)).getTime();
    let photo="";
    if(req.files[0]!=undefined){
        photo = req.files[0].buffer;
    }
    const loc1 = await geolocation(req.body.address, 3);
    const object = {name: req.body.name, description: req.body.description, startDate, endingDate, poster: photo, location: loc1.id, lat: loc1.lat, lon: loc1.lon, type: req.body.type, declared: []};

    const verify = jwt.verify(req.body.token, "ORYGINAL_STRING"+req.body.token_ID+"ORYGINAL_STRING");
    delete req.body.token
    delete req.body.token_ID
    const loc = req.body.address;
    const loc11 = await localizationModel.findOne({_id: loc1.id});
    let loc12;
    if(loc11.time_3<endingDate){
         loc12 = await localizationModel.findOneAndUpdate({_id: loc1.id}, {$inc:{is_3: 1}, time_3: endingDate})
    }   
    object.userID = verify._id;
    
    const an = new evModel(object);
    await an.save();
    res.send({type: 0, data: {}})
});

router.post("/api/announcements/get-update-1", async (req, res)=>{
    const verify = jwt.verify(req.body.token, "ORYGINAL_STRING"+req.body.token_ID+"ORYGINAL_STRING");
    const quantity = JSON.parse(req.body.quantity);
    const ann = await anModel.findOne({_id: req.body.id});
    const things = ann.data.things;
    quantity.forEach(async (e, i)=>{
        const time = (new Date()).getTime();

        const newStats = new statsModel({userID: req.body.userID, id: req.body.id, time: time, quantity:quantity[i], type: "1", category: things[i].category, secondCategory: things[i].secondCategory, location: ann.location, lat: ann.lat, lon: ann.lon, hUser:verify._id});
        await newStats.save();
        const obj = {$set: {}};
        obj.$set["data.things."+i+".get"] = (ann.data.things[i].get+quantity[i]);
        const user1 = await anModel.findOneAndUpdate({_id: req.body.id}, obj);
    });
    res.send({type: 0, data:{}})
})

router.post("/api/announcements/get-update-2", async (req, res)=>{
    const verify = jwt.verify(req.body.token, "ORYGINAL_STRING"+req.body.token_ID+"ORYGINAL_STRING");
    const quantity = JSON.parse(req.body.quantity);
    const ann = await myHModel.findOne({_id: req.body.id});
    const time = (new Date()).getTime();
    ann.get = ann.get+req.body.quantity;
    await ann.save();
    const newStats = new statsModel({userID: verify._id, id: req.body.id, time: time, quantity:req.body.quantity, type: "2", category: ann.category, secondCategory: ann.secondCategory, location: ann.location, lat: ann.lat, lon: ann.lon, hUser:req.body.userID});
    await newStats.save();
    res.send({type: 0, data:{}})
})

router.post("/api/announcements/get-stats", async (req, res)=>{
    const verify = jwt.verify(req.body.token, "ORYGINAL_STRING"+req.body.token_ID+"ORYGINAL_STRING");
    const stats = await statsModel.find({userID: verify._id});
    const myHL = (await myHModel.find({userID: verify._id})).length
    const ids = []
    stats.forEach(e=>{
        if(!ids.find(x=> x==e.hUser )){
            ids.push(e.hUser)
        }
    });
    res.send({type: 0, data:{stats, myHL, hUsers: ids.length}})
})

router.post('/api/announcements/update_type_3' ,async (req, res)=>{
    const ann = await evModel.findOne({_id: req.body.id})
    const startDate =(new Date(req.body.startDate)).getTime();
    const endingDate =(new Date(req.body.endingDate)).getTime();
    let photo = null;
    if(req.files[0]!=undefined){
        photo = req.files[0].buffer;
    }else{
        photo = ann.poster;
    }
    const object = {name: req.body.name, description: req.body.description, startDate, endingDate, poster: photo, type: req.body.type};

    const verify = jwt.verify(req.body.token, "ORYGINAL_STRING"+req.body.token_ID+"ORYGINAL_STRING");
    delete req.body.token
    delete req.body.token_ID
    const loc = req.body.address;
    const loc11 = await localizationModel.findOne({_id: ann.location});
    let loc12;
    if(loc11.time_3<endingDate){
         loc12 = await localizationModel.findOneAndUpdate({_id: ann.location}, {$inc:{is_3: 1}, time_3: endingDate})
    }
    await evModel.findOneAndUpdate({_id: req.body.id}, object)
    res.send({type: 0, data: {}})
});

router.post('/api/profile/get-profile-an', async (req, res) => {
    let selected = [];
    switch(req.body.type){
        case "1":
            selected = await anModel.find({'data.userID': req.body.user})
            break;
        case "2":
            selected = await myHModel.find({'userID': req.body.user})
            break;
        case "3":
            selected = await evModel.find({'userID': req.body.user})
            selected.forEach((e, i) => {
                selected[i].poster = '';
                selected[i].declared = [{id: selected[i].declared.length}];

            })
            break;
        case "4":
            selected = await actModel.find({'userID': req.body.user})
            selected.forEach((e, i)=>{
                selected[i].photos.forEach((e1, i1)=>{
                    selected[i].photos[i1].img='';
                })
            })
            break;
        case "5":
            selected = await ideaModel.find({'userID': req.body.user})
            selected.forEach((e, i)=>{
                selected[i].photos.forEach((e1, i1)=>{
                    selected[i].photos[i1].img='';
                })
            })
            selected.forEach((e, i)=>{
                selected[i].declared=[];
            })
            break;
            
    }
    res.send({type: 0, data:{selected}})
})


router.post("/api/announcements/delete", async (req, res)=>{
    const verify = jwt.verify(req.body.token, "ORYGINAL_STRING"+req.body.token_ID+"ORYGINAL_STRING");
    
    switch(req.body.type){
        case "1":
            await anModel.findOneAndDelete({_id: req.body.id, 'data.userID': verify._id})
            break;
        case "2":
            await myHModel.findOneAndDelete({_id: req.body.id, userID: verify._id})
            break;
        case "3":
            await evModel.findOneAndDelete({_id: req.body.id, userID: verify._id})
            break;
        case "4":
            await actModel.findOneAndDelete({_id: req.body.id, userID: verify._id})
            break;
        case "5":
            await ideaModel.findOneAndDelete({_id: req.body.id, userID: verify._id})
            break;
            
    }
    res.send({type: 0, data:{}})

});


router.post('/api/announcements/get-all' ,async (req, res)=>{
    const time = (new Date()).getTime()
    const [up, down, right, left] = [parseFloat(req.body.up), parseFloat(req.body.down), parseFloat(req.body.right), parseFloat(req.body.left)];
    const selected = {}
    selected.type_1 = await localizationModel.find({lat: {$gte: down, $lte: up}, lon:{$gte: left, $lte: right}, is_1:{$gt: 0}, time_1:{$gte: time}})  
    let i1 = 0;
    let f = async () => {
        let i = 0;
        const x = [];
        for(const e of selected.type_1){
            let selected1 = await anModel.findOne({location: e._id, endingDate: {$gte: time}});
            if(selected1!=null){
                x.push(e)
            }
        }
        selected.type_1 = x;
        
    }
    await f();
    i1=0;
    selected.type_2 = await localizationModel.find({lat: {$gte: down, $lte: up}, lon:{$gte: left, $lte: right}, is_2:{$gt: 0}, time_2:{$gte: time}})   
    f = async () => {
        let i = 0;
        const x = [];
        for(const e of selected.type_2){
            let selected1 = await myHModel.findOne({location: e._id, endingDate: {$gte: time}});
            if(selected1!=null){
                x.push(e)
            }
        }
        selected.type_2 = x;
        
    }
    await f();
    i1=0;
    selected.type_3 = await localizationModel.find({lat: {$gte: down, $lte: up}, lon:{$gte: left, $lte: right}, is_3:{$gt: 0}, time_3:{$gte: time}})
    f = async () => {
        let i = 0;
        const x = [];
        for(const e of selected.type_3){
            let selected1 = await evModel.findOne({location: e._id, endingDate: {$gte: time}});
            if(selected1!=null){
                x.push(e)
            }
        }
        selected.type_3 = x;
        
    }
    await f();
    i1=0;
    selected.type_4 = await localizationModel.find({lat: {$gte: down, $lte: up}, lon:{$gte: left, $lte: right}, is_4:{$gt: 0}, time_4:{$gte: time}})
    f = async () => {
        let i = 0;
        const x = [];
        for(const e of selected.type_4){
            let selected1 = await actModel.findOne({location: e._id, endingDate: {$gte: time}});
            if(selected1!=null){
                x.push(e)
            }
        }
        selected.type_4 = x;
        
    }
    await f();
    i1=0;
    selected.type_5 = await localizationModel.find({lat: {$gte: down, $lte: up}, lon:{$gte: left, $lte: right}, is_5:{$gt: 0}})
    f = async () => {
        let i = 0;
        const x = [];
        for(const e of selected.type_5){
            let selected1 = await ideaModel.findOne({location: e._id, endingDate: {$gte: time}});
            if(selected1!=null){
                x.push(e)
            }
        }
        selected.type_5 = x;
        
    }
    await f();
    res.send({type: 0, data: {selected}})
});

router.post("/api/announcements/get-with-filters", async (req, res) => {
    const time = (new Date()).getTime()

    const [up, down, right, left] = [parseFloat(req.body.up), parseFloat(req.body.down), parseFloat(req.body.right), parseFloat(req.body.left)];
    const filters = JSON.parse(req.body.filters);
    const main_types = filters.main_types;

    const selected = {}
    for(let i = 0; i < main_types.length; i++) {
        switch(main_types[i]) {
            case 1:
                selected.type_1 = await localizationModel.find({lat: {$gte: down, $lte: up}, lon:{$gte: left, $lte: right}, is_1:{$gt: 0}, time_1:{$gte: time}})
                f = async () => {
                    let i = 0;
                    const x = [];
                    for(const e of selected.type_1){
                        let selected1 = await anModel.findOne({location: e._id, endingDate: {$gte: time}});
                        if(selected1!=null){
                            x.push(e)
                        }
                    }
                    selected.type_1 = x;
                    
                }
                await f();
                break;
            case 2:
                selected.type_2 = await localizationModel.find({lat: {$gte: down, $lte: up}, lon:{$gte: left, $lte: right}, is_2:{$gt: 0}, time_2:{$gte: time}})
                f = async () => {
                    let i = 0;
                    const x = [];
                    for(const e of selected.type_2){
                        let selected1 = await myHModel.findOne({location: e._id, endingDate: {$gte: time}});
                        if(selected1!=null){
                            x.push(e)
                        }
                    }
                    selected.type_2 = x;
                    
                }
                await f();
                break;
            case 3:
                selected.type_3 = await localizationModel.find({lat: {$gte: down, $lte: up}, lon:{$gte: left, $lte: right}, is_3:{$gt: 0}, time_3:{$gte: time}})
                f = async () => {
                    let i = 0;
                    const x = [];
                    for(const e of selected.type_3){
                        let selected1 = await evModel.findOne({location: e._id, endingDate: {$gte: time}});
                        if(selected1!=null){
                            x.push(e)
                        }
                    }
                    selected.type_3 = x;
                    
                }
                await f();
                break;
            case 4:
                selected.type_4 = await localizationModel.find({lat: {$gte: down, $lte: up}, lon:{$gte: left, $lte: right}, is_4:{$gt: 0}})
                f = async () => {
                    let i = 0;
                    const x = [];
                    for(const e of selected.type_4){
                        let selected1 = await actModel.findOne({location: e._id, endingDate: {$gte: time}});
                        if(selected1!=null){
                            x.push(e)
                        }
                    }
                    selected.type_4 = x;
                    
                }
                await f();
                break;
            case 5:
                selected.type_5 = await localizationModel.find({lat: {$gte: down, $lte: up}, lon:{$gte: left, $lte: right}, is_5:{$gt: 0}})
                f = async () => {
                    let i = 0;
                    const x = [];
                    for(const e of selected.type_5){
                        let selected1 = await ideaModel.findOne({location: e._id, endingDate: {$gte: time}});
                        if(selected1!=null){
                            x.push(e)
                        }
                    }
                    selected.type_5 = x;
                    
                }
                await f();
                break;
            
        }
    }
    res.send({type: 0, data: {selected}})
});

router.post("/api/announcements/search", async (req, res) => {
    const [up, down, right, left] = [parseFloat(req.body.up), parseFloat(req.body.down), parseFloat(req.body.right), parseFloat(req.body.left)];
    const search = JSON.parse(req.body.search);
    const main_types = search.types;
    const time = (new Date()).getTime();
    const selected = {}
    for(let i = 0; i < main_types.length; i++) {
        switch(main_types[i]) {
            case 1:
                selected.type_1 = await anModel.find({lat: {$gte: down, $lte: up}, lon:{$gte: left, $lte: right}, 'data.things': {$elemMatch:{$or:[{ title:{$regex: ".*"+search.search+".*"}}, { description:{$regex: ".*"+search.search+".*"}}]}, $elemMatch:{$or:[{ category:search.category}, { secondCategory:search.category}]}}, 'data.endingDate': {$gte: time}});
                const ids = [];
                selected.type_1.forEach(e=>{
                    if(!ids.find(x=> x==e.location )){
                        ids.push(e.location)
                    }
                });
                selected.type_1 = await localizationModel.find({_id: {$in:ids}});
                break;
            case 2:
                selected.type_2 = await myHModel.find({$and:
                    [
                        {$or:[{ title:{$regex: ".*"+search.search+".*"}}, { description:{$regex: ".*"+search.search+".*"}}]}
                        , {lat: {$gte: down, $lte: up}}
                        , {lon:{$gte: left, $lte: right}}, {$or:[{ category:search.category}, { secondCategory:search.category}]}, 
                        {date: {$gt: time}}
                ]});
                const ids1 = [];
                selected.type_2.forEach(e=>{
                    if(!ids1.find(x=> x==e.location )){
                        ids1.push(e.location)
                    }
                });
                selected.type_2 = await localizationModel.find({_id: {$in:ids1}});
                break;
            case 3:
                selected.type_3 = await evModel.find({$and:
                    [
                        {$or:[{ name:{$regex: ".*"+search.search+".*"}}, { description:{$regex: ".*"+search.search+".*"}}]}
                        , {lat: {$gte: down, $lte: up}}
                        , {lon:{$gte: left, $lte: right}},
                        {endingDate: {$gt: time}}
                ]});
                const ids2 = [];
                selected.type_3.forEach(e=>{
                    if(!ids2.find(x=> x==e.location )){
                        ids2.push(e.location)
                    }
                });
                selected.type_3 = await localizationModel.find({_id: {$in:ids2}});
                break;
            case 4:
                selected.type_4 = await actModel.find({$and:
                    [
                        {$or:[{ title:{$regex: ".*"+search.search+".*"}}, { description:{$regex: ".*"+search.search+".*"}}]}
                        , {lat: {$gte: down, $lte: up}}
                        , {lon:{$gte: left, $lte: right}}
                ]});
                const ids3 = [];
                selected.type_4.forEach(e=>{
                    if(!ids3.find(x=> x==e.location )){
                        ids3.push(e.location)
                    }
                });
                selected.type_4 = await localizationModel.find({_id: {$in:ids3}});
                break;
            case 5:
                selected.type_5 = await ideaModel.find({$and:
                    [
                        {$or:[{ title:{$regex: ".*"+search.search+".*"}}, { description:{$regex: ".*"+search.search+".*"}}]}
                        , {lat: {$gte: down, $lte: up}}
                        , {lon:{$gte: left, $lte: right}}
                ]});
                const ids4 = [];
                selected.type_5.forEach(e=>{
                    if(!ids4.find(x=> x==e.location )){
                        ids4.push(e.location)
                    }
                });
                selected.type_5 = await localizationModel.find({_id: {$in:ids4}});
                break;
            
        }
    }
    res.send({type: 0, data: {selected}})
});

router.post('/api/announcements/get-from-localization' ,async (req, res)=>{
    const date = new Date();
    const verify = jwt.verify(req.body.token, "ORYGINAL_STRING"+req.body.token_ID+"ORYGINAL_STRING");
    const announcementsA = [];
    req.body.types = JSON.parse(req.body.types)
    for(let i1 = 0; i1 < req.body.types.length; i1++){
        switch(req.body.types[i1]){
            case 1:
                const announcements = await anModel.find({'data.endingDate': {$gte: date.getTime()}, location: req.body.location});
                announcements.forEach((e, i)=>{
                    announcements[i].anType = "1"
                })
                announcementsA.push.apply(announcementsA, announcements);
                break;
            case 2:
                const announcements1 = await myHModel.find({'data.endingDate': {$gte: date.getTime()}, location: req.body.location});
                announcements1.forEach((e, i)=>{
                    announcements1[i].anType = "2"
                })
                announcementsA.push.apply(announcementsA, announcements1);

                break;
            case 3:
                const announcements2 = await evModel.find({'data.endingDate': {$gte: date.getTime()}, location: req.body.location});
                announcements2.forEach((e, i)=>{
                    announcements2[i].poster = '';
                    announcements2[i].anType = "3";
                })
                announcementsA.push.apply(announcementsA, announcements2);

    
                break;
            case 4:
                const announcements3 = await actModel.find({'data.endingDate': {$gte: date.getTime()}, location: req.body.location});
                announcements3.forEach((e, i)=>{
                    announcements3[i].anType="4"
                    announcements3[i].photos.forEach((e1, i1)=>{
                        announcements3[i].photos[i1].img='';
                    })
                })
                
                announcementsA.push.apply(announcementsA, announcements3);
                break;
             case 5:
                const announcements4 = await ideaModel.find({'data.endingDate': {$gte: date.getTime()}, location: req.body.location});
                announcements4.forEach((e, i)=>{
                    announcements4[i].anType="5"
                    announcements4[i].photos.forEach((e1, i1)=>{
                        announcements4[i].photos[i1].img='';
                    });
                })
                announcements4.forEach((e, i)=>{
                    announcements4[i].declared=[];
                })
                announcementsA.push.apply(announcementsA, announcements4);
                break;
            }
      
    }

    res.send({type: 0, data: {announcements: announcementsA}});
    
    
});

router.post("/api/announcements/get-announcement", async (req, res) => {
    const date = new Date();
    const verify = jwt.verify(req.body.token, "ORYGINAL_STRING"+req.body.token_ID+"ORYGINAL_STRING");
    switch(req.body.type){
        case "1":
            const announcements = await anModel.findOne({_id: req.body.id});
            const user = await userModel.findOne({_id: announcements.data.userID})
            
            res.send({type: 0, data: {announcements, user:{_id: user._id, name: user.name, surname: user.surname}}})
            break;
        case "2":
            const announcements1 = await myHModel.findOne({_id: req.body.id});
            const user1 = await userModel.findOne({_id: announcements1.userID})

            res.send({type: 0, data: {announcements: announcements1, user:{_id: user1._id, name: user1.name, surname: user1.surname}}})
            break;
        case "3":
            const announcements2 = await evModel.findOne({_id: req.body.id});
            let want = false;
            want = (announcements2.declared.find(e=>verify._id==e.id)!=undefined);
            announcements2.declared = [{id: announcements2.declared.length}];
            announcements2.poster = "";

            const user2 = await userModel.findOne({_id: announcements2.userID})
            res.send({type: 0, data: {announcements: announcements2, want, user:{_id: user2._id, name: user2.name, surname: user2.surname}}})
            break;
         case "4":
            const announcements3 = await actModel.findOne({_id: req.body.id});
            announcements3.photos.forEach((e1, i1)=>{
                announcements3.photos[i1].img='';
            })

            const user3 = await userModel.findOne({_id: announcements3.userID})
            res.send({type: 0, data: {announcements: announcements3, user:{_id: user3._id, name: user3.name, surname: user3.surname}}})
            break;
        case "5":
            const ann1 = await ideaModel.findOne({_id: req.body.id});

        const announcements4 = {...ann1}._doc
        announcements4.photos.forEach((e1, i1)=>{
            announcements4.photos[i1].img='';
        })
        announcements4.support = 0;
        
        announcements4.votes.forEach((e2, i1)=>{
            if(e2.support){
                announcements4.support++;
            }
            if(e2.id==verify._id){
                announcements4.myVote = e2.support;
            }
        })
        announcements4.votes = announcements4.votes.length;
        const user4 = await userModel.findOne({_id: announcements4.userID})
        res.send({type: 0, data: {announcements: announcements4, user:{_id: user4._id, name: user4.name, surname: user4.surname}}})
        break;
            
    }
})
router.get("/api/announcements/get-poster/:id", async (req, res) => {
    const param = req.params.id;
    const announcements2 = await evModel.findOne({_id: param});
    res.setHeader("Content-Type", "image/png").send(announcements2.poster);

})
router.get("/api/profile/get-profile-image/:id", async (req, res) => {
    const param = req.params.id;
    const announcements2 = await userModel.findOne({_id: param});
    res.setHeader("Content-Type", "image/png").send(announcements2.profile);
})

router.post('/api/announcements/add_type_4' ,async (req, res)=>{
    const today = new Date();
    const endingDate =new Date(today.getTime()+3600*24*1000);
    const photos = [];
    if(req.files[0]!=undefined) {
        photos.push({img: req.files[0].buffer})
    }
    if(req.files[1]!=undefined) {
        photos.push({img: req.files[1].buffer})
    }
    if(req.files[2]!=undefined) {
        photos.push({img: req.files[2].buffer})
    }
    
    const loc1 = await geolocation(req.body.localization, 3);
    const object = {title: req.body.title, description: req.body.description, endingDate, photos, location: loc1.id, lat: loc1.lat, lon: loc1.lon, category: req.body.category};

    const verify = jwt.verify(req.body.token, "ORYGINAL_STRING"+req.body.token_ID+"ORYGINAL_STRING");
    delete req.body.token
    delete req.body.token_ID
    const loc = req.body.address;
    const loc11 = await localizationModel.findOne({_id: loc1.id});
    let loc12;
    if(loc11.time_4<endingDate){
         loc12 = await localizationModel.findOneAndUpdate({_id: loc1.id}, {$inc:{is_4: 1}, time_4: endingDate})
    }   
    object.userID = verify._id;
    
    const an = new actModel(object);
    await an.save();
    res.send({type: 0, data: {}})
});

router.post('/api/announcements/add_type_5' ,async (req, res)=>{
    const today = new Date();
    const photos = [];
    if(req.files[0]!=undefined) {
        photos.push({img: req.files[0].buffer})
    }
    if(req.files[1]!=undefined) {
        photos.push({img: req.files[1].buffer})
    }
    
    const loc1 = await geolocation(req.body.localization, 3);
    const object = {title: req.body.name, description: req.body.description, created: today.getTime(), photos, location: loc1.id, lat: loc1.lat, lon: loc1.lon, category: req.body.category};

    const verify = jwt.verify(req.body.token, "ORYGINAL_STRING"+req.body.token_ID+"ORYGINAL_STRING");
    delete req.body.token
    delete req.body.token_ID
    const loc = req.body.address;
    const loc11 = await localizationModel.findOne({_id: loc1.id});
    let loc12;
    loc12 = await localizationModel.findOneAndUpdate({_id: loc1.id}, {$inc:{is_5: 1}})
    
    object.userID = verify._id;
    
    const an = new ideaModel(object);
    await an.save();
    res.send({type: 0, data: {}})
});



router.post('/api/announcements/vote_type_5' ,async (req, res)=>{
    const verify = jwt.verify(req.body.token, "ORYGINAL_STRING"+req.body.token_ID+"ORYGINAL_STRING");
    const idea = await ideaModel.findOne({_id: req.body.id});
    let voted = false;
    idea.votes.forEach((e, i)=>{
        if(e.id==verify._id){
            voted = true;
        }
    })
    if(voted){
        
    }else{
        await ideaModel.findOneAndUpdate({_id: req.body.id}, {$push:{votes:{id: verify._id, support: (req.body.support=='true')}}})
    }
    res.send({type: 0, data: {}})
});
router.post('/api/announcements/getUser' ,async (req, res)=>{
    const verify = jwt.verify(req.body.token, "ORYGINAL_STRING"+req.body.token_ID+"ORYGINAL_STRING");
    const user = await userModel.findOne({_id: req.body.user})
    res.send({type: 0, data: {name: user.name, surname: user.surname, description: user.description}})
});
router.post('/api/announcements/set-profile-proffesion' ,async (req, res)=>{
    const verify = jwt.verify(req.body.token, "ORYGINAL_STRING"+req.body.token_ID+"ORYGINAL_STRING");
    const user = await userModel.findOneAndUpdate({_id: verify._id}, {profession: req.body.profession})
    res.send({type: 0, data: {}})
});

router.post('/api/announcements/vote_type_3' ,async (req, res)=>{
    const verify = jwt.verify(req.body.token, "ORYGINAL_STRING"+req.body.token_ID+"ORYGINAL_STRING");
    const idea = await evModel.findOne({_id: req.body.id});
    let voted = false;
    idea.declared.forEach((e, i)=>{
        if(e.id==verify._id){
            voted = true;
        }
    })
    if(voted){
        await evModel.findOneAndUpdate({_id: req.body.id}, {$pull:{declared:{id: verify._id}}})
    }else{
        await evModel.findOneAndUpdate({_id: req.body.id}, {$push:{declared:{id: verify._id}}})
    }
    res.send({type: 0, data: {}})
});


router.get('/api/announcements/get-image-from-ann/:type/:id/:position' ,async (req, res)=>{
    switch (req.params.type) {
        case "4":
            const ann = await actModel.findOne({_id: req.params.id});
            res.setHeader("Content-Type", "image/png").send(ann.photos[parseInt(req.params.position)].img);
            break;
        case "5":
            const ann1 = await ideaModel.findOne({_id: req.params.id});
            res.setHeader("Content-Type", "image/png").send(ann1.photos[parseInt(req.params.position)].img);
            break;
    }
});

const geolocation = async (address, type) => {
    const path = `http://api.geocodify.com/v2/geocode?api_key=${GEO_API_KEY}&q=${address}`.toString();
    const response = await axios.get(path);
    const features = response.data.response.features[0];
    const t1 = 0;
    const t2 = 0;
    const t3 = type == 3 ? 1 : 0;
    const localization = await localizationModel.findOne({lat: features.geometry.coordinates[1], lon: features.geometry.coordinates[0]})
    
    if(localization==null) {
        const model = new localizationModel({lat: features.geometry.coordinates[1], lon: features.geometry.coordinates[0], street: features.properties.street, village: features.properties.localadmin, region: features.properties.county, district: features.properties.region, is_1:0, is_2:0, is_3:t3});
        await model.save();
        return  {id: model._id, lat: features.geometry.coordinates[1], lon: features.geometry.coordinates[0]};
    }else{
        return {id: localization._id, lat: features.geometry.coordinates[1], lon: features.geometry.coordinates[0]};
    }

}

module.exports = router;