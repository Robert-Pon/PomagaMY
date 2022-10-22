const mongoose = require('mongoose');


const schema = new mongoose.Schema({
    userID:{
        type: String
    },
    title:{
        type: String, 
        default: ''
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
    created:{
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
    votes:[
        {
            support:{
                type: Boolean
            },
            id:{
                type: String
            }
        }
    ],
    anType:{
        type: String
    }
})

const model = mongoose.model('idea', schema);

module.exports = model;