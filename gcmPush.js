/* gcmPush.js */

var gcm = require('node-gcm');

// create a message with default values
var message = new gcm.Message();

// or with object values
var message = new gcm.Message({
    collapseKey: 'demo',
    delayWhileIdle: true,
    timeToLive: 3,
    data: {
        key1: '안녕하세요.',
        key2: 'saltfactory push demo'
    }
});

var server_access_key = 'AIzaSyBPue7Sih78oIym7tt3NDMWN7A8Sqi3awA';
var sender = new gcm.Sender(server_access_key);
var registrationIds = [];

var registration_id = 'APA91bFjtl-aajm-8ykTlkAHNZEzn_pWMDe9eShHE-JQmfSMT1UwcYLmhcCXWNeEO58qVBzyMt88_ehDMCOd-ZSVGHtN6wGArETEw5MA0xvngcEbcZmAIjZAiJXfEX7NeizaI5BtzrRrgFdTQMu8desCifQXuLldqQ';
// At least one required
registrationIds.push(registration_id);

/**
 * Params: message-literal, registrationIds-array, No. of retries, callback-function
 **/
sender.send(message, registrationIds, 4, function (err, result) {
    console.log(result);
});