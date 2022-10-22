const mongoose = require('mongoose');


const schema = new mongoose.Schema({
    title:{
        type: String
    },
    description:{
        type: String
    }
})

const model = mongoose.model('article', schema);

module.exports = model;