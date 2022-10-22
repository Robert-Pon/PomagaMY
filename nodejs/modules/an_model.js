const mongoose = require('mongoose');


const schema = new mongoose.Schema({
    type:{
        type: Number
    },
    data:{
        type: Object
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
    anType:{
        type: String
    }
})

const model = mongoose.model('an', schema);

module.exports = model;