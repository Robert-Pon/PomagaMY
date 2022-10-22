const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
    name:{
        type: String
    },
    surname:{
        type: String
    },
    email:{
        type: String
    },
    password:{
        type: String
    },
    description:{
        type: String,
        default: ""
    },
    profile:{
        type: Buffer
    },
    tags:[
        {
            id:{
                type: String
            },
            name:{
                type: String
            }
        }
    ],
    location:{
        type: String   
    },
    stats:[
        {
            type: {
                type: String
            }
        }
    ],
    tokens:[{
        token:{
            type: String
        },
        time:{
            type: Number
        },
        newTokenID:{
            type: String
        }

    }],
    since:{
        type: Number
    },
    chats:[{
        id:{
            type: String
        },
        time:{
            type: Number
        },
        message:{
            type: String
        },
        seen:{
            type: Boolean
        },
        name:{
            type: String
        }
    }],
    profession:{
        type: String
    }
});

const user = new mongoose.model('user',userSchema);

module.exports = user;