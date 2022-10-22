const mongoose = require('mongoose');


const schema = new mongoose.Schema({
    userID:{
        type: String
    },
    name:{
        type: String,
        default:"Brak nazwy"
    },
    description:{
        type: String,
        default:"Brak opisu"
    },
    startDate:{
        type: Number
    },
    endingDate:{
        type:Number
    },
    poster:{
        type:Buffer
    },
    declared:[
        {
            id: {
                type: String
            }
        }
    ],
    type:{
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

const model = mongoose.model('ev', schema);

module.exports = model;