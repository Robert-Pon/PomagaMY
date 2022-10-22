const mongoose = require('mongoose');

const schema = new mongoose.Schema({
    lat:{
        type: Number
    },
    lon:{
        type: Number
    },
    street:{
        type: String
    },
    village:{
        type: String
    },
    region:{
        type: String
    },
    district:{
        type: String
    }, 
    is_1:{
        type: Number,
        default:0 
    },
    is_2:{
        type: Number,
        default:0 
    },
    is_3:{
        type: Number,
        default:0 
    },
    is_4:{
        type: Number,
        default:0 
    },
    is_5:{
        type: Number,
        default:0 
    },
    time_1:{
        type: Number,
        default:0
    },
    time_2:{
        type: Number,
        default:0
    },
    time_3:{
        type: Number,
        default:0
    },
    time_4:{
        type: Number,
        default:0
    },
    time_5:{
        type: Number,
        default:0
    }
    
    
})

const model = mongoose.model('localization', schema);

module.exports = model;