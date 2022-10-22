const mongoose = require('mongoose');


const schema = new mongoose.Schema({
    userID:{
        type: String
    },
    id:{
        type: String
    },
    hUser:{
        type: String
    },
    time:{
        type: Number
    }, 
    quantity:{
        type: Number
    },
    type:{
        type: String
    },
    category:{
        type: String
    },
    secondCategory:{
        type: String
    },
    location:{
        type: String
    },
    lat:{
        type: Number
    },
    lon:{
        type: Number
    }
})

const model = mongoose.model('stats', schema);

module.exports = model;