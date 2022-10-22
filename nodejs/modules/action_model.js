const mongoose = require('mongoose');


const schema = new mongoose.Schema({
    userID:{
        type: String
    },
    title:{
        type: String
    },
    description:{
        type: String
    },
    photos:[{
        img: {
            type: Buffer
        
        }
    }

    ], 
    endingDate:{
        type: Number
    },
    category:{
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
    },
    anType:{
        type: String
    }

})

const model = mongoose.model('action', schema);

module.exports = model;