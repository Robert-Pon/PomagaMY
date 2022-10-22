require('./connection');

const express = require('express');

const http = require('http');
const app = new express();
const server = http.createServer(app)
const user_route = require('./routes/user_route')
const auctions_route = require('./routes/auctions_route')
const chat_route = require('./routes/chat_route')
const parser = require('body-parser')
const socket = require('./routes/chat_io')
const path = require('path');
const io = socket(server)
const chat_route1 = chat_route;
chat_route1.io = io;

const multer = require('multer');

const upload = multer({
    fieldSize: 10000000
});

app.get("/privacy", (req, res)=>{
    res.sendFile(path.join(__dirname, '../src', 'x.html'));
})



app.use(upload.any(), (req, res, next )=>{
    res.setHeader("Content-Type", "application/json; charset=utf-8");
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS, PUT, PATCH, DELETE');
    res.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
    res.setHeader('Access-Control-Allow-Credentials', true);
    next();
});


app.use(user_route)
app.use(auctions_route)
app.use(chat_route1);
app.set('io', io)

server.listen(3001)



