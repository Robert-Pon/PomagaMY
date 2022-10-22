const mongoose = require('mongoose');

const schema = new mongoose.Schema({
    photo:{
        type: Buffer
    },
    user:{
        type: String
    },
    type:{
        type: String
    },
    ext:{
        type: String
    }
})


const model = new mongoose.model('photo', schema);

module.exports = model;