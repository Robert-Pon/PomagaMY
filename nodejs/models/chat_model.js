const mongoose = require('mongoose');

const schema = new mongoose.Schema({
    blocked:{
        type: String, 
        default: ""
    },
    collaborators:[
        {
            id:{
                type: String
            },
            nick:{
                type: String
            }
        }
    ],
    messages:[
        {
            user: {
                type: String
            },
            content: {
                type: String
            },
            state: {
                type: Number
            },
            type: {
                type: String
            },
            name: {
                type: String
            },
            time: {
                type: Number,
                default: 0
            }
        }
    ]
});

const model = new mongoose.model('chat',schema);

module.exports = model;