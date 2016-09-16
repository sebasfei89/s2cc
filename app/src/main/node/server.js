var net = require('net');
var sockets = [];

var srv = net.createServer( sock => {
    console.log('Connected: ' + sock.remoteAddress + ':' + sock.remotePort);
    sockets.push(sock);

    sock.on('data', function(data) {
        let results = JSON.parse(data);
        console.log('Received data: %s', JSON.stringify(results,null,4));
    });

    sock.on('end', function() {
        console.log('Disconnected: ' + sock.remoteAddress + ':' + sock.remotePort);
        var idx = sockets.indexOf(sock);
        if (idx != -1) {
            delete sockets[idx];
        }
    });
});

srv.listen({host: '192.168.0.15', port: 9876}, () => console.log('Server Created at %j', srv.address()));
