var express = require('express')
var app = express()
var port = process.env.PORT || 3000,
    http = require('http'),
    fs = require('fs'),
    exec = require("child_process").exec,
    html = fs.readFileSync('index.html');

app.get('/*', function (req, res) {
	//"http://sample-env.jbtpykbsa9.us-west-2.elasticbeanstalk.com/mass%202.5%20Oxygen%20voulme%20Water%20Hydrogen%20+%20Oxygen%20->%20Water"
	console.log(req.url);
  console.log("method was get");
    	console.log("req.url: "+req.url);
    	console.log("let's try running a child process!");
    	req.url = req.url.slice(1);
    	var data = decodeURIComponent(req.url).split(' ');
    	var str = 'java -jar chembot.jar'
    	for (var i = 0; i < data.length; i++) {
    		str += ' \"' + data[i] + '\"';
    	}
    	console.log(str);
    	//res.send(str);
    	child = exec(str,
		function (error, stdout, stderr){
			console.log('stdout: ' + stdout);
			console.log('stderr: ' + stderr);
			if(error !== null){
  				console.log('exec error: ' + error);
			}else{
				console.log("no errors!");
			}
        	res.send(stdout);
		});
})

app.listen(port, function () {
  console.log('Example app listening on port ' + port)
})



/*var port = process.env.PORT || 3000,
    http = require('http'),
    fs = require('fs'),
    exec = require("child_process").exec,
    html = fs.readFileSync('index.html');
var log = function(entry) {
    fs.appendFileSync('/tmp/sample-app.log', new Date().toISOString() + ' - ' + entry + '\n');
};

var server = http.createServer(function (req, res) {
    if (req.method === 'POST') {
    	console.log("method was post");
        var body = '';

        req.on('data', function(chunk) {
            body += chunk;
        });

        req.on('end', function() {
        	console.log("req.url: "+req.url);
        	console.log('Received message: ' + body);
            if (req.url === '/') {
                console.log("");
            }

            res.writeHead(200, 'OK', {'Content-Type': 'text/plain'});
            res.end();
        });
    } else if(req.method=="GET"){
    	console.log("method was get");
    	console.log("req.url: "+req.url);
    	console.log("let's try running a child process!");
    	child = exec('java -jar C:\\Users\\ninja\\Desktop\\test.jar',
		function (error, stdout, stderr){
			console.log('stdout: ' + stdout);
			console.log('stderr: ' + stderr);
			if(error !== null){
  				console.log('exec error: ' + error);
			}else{
				console.log("no errors!");
			}
			res.writeHead(200);
        	res.write("req response");
        	res.end();
		});
        
    }
});

// Listen on port 3000, IP defaults to 127.0.0.1
server.listen(port);

// Put a friendly message on the terminal
console.log('Server running at http://127.0.0.1:' + port + '/');
console.log(exec);

child = exec('java -jar C:\\Users\\ninja\\Desktop\\runnabletest.jar "hello"',
	function (error, stdout, stderr){
		console.log('stdout: ' + stdout);
		console.log('stderr: ' + stderr);
		if(error !== null){
  			console.log('exec error: ' + error);
		}else{
			console.log("no errors!");
		}
	});
	*/