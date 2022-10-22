const mongoose = require('mongoose');


const schema = new mongoose.Schema({
    title:{
        type: String
    },
    description:{
        type: String
    },
    category:{
        type: String
    },
    secondCategory:{
        type: String
    },
    quantity:{
        type: Number,
        default: 1
    },
    localization:{
        type: String
    },
    priority:{
        type: String
    },
    date:{
        type: Number
    },
    location:{
        type: String
    },
    lat:{
        type: Number
    },
    lon:{
        type: Number
    },
    get:{
        type: Number,
        default: 0
    },
    userID:{
        type: String
    },
    anType:{
        type: String
    }
})

const model = mongoose.model('my', schema);

module.exports = model;