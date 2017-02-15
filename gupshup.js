/*var totalvalue = context.simpledb.roomleveldata.totalcount;
 if (!totalvalue)
      context.simpledb.roomleveldata.totalcount= parseInt(event.message,10);
    else
      context.simpledb.roomleveldata.totalcount = totalvalue + parseInt(event.message,10);
      context.sendResponse("");
} */

function MessageHandler(context, event) {
    //context.simplehttp.makeGet('http://sample-env.jbtpykbsa9.us-west-2.elasticbeanstalk.com/Stoichiometry%20mass%202.5%20Oxygen%20volume%20Water%20Hydrogen%20+%20Oxygen%20-%3E%20Water');//+dat, req);
    //context.sendResponse("Processing data...");
    if(event.message == "get me info"){
        console.log("got here");
        context.sendResponse(context.simpledb.roomleveldata.curConversationInfo);
        return;
    }
    var curConversationInfo = context.simpledb.roomleveldata.curConversationInfo;
    var inputData = context.simpledb.roomleveldata.inputData;
    if(context.simpledb.roomleveldata.curConversationInfo==undefined)curCoversationInfo=[];
    if(context.simpledb.roomleveldata.inputData==undefined)inputData=[];
    var currentProblems = '\n Current solvable problems are: Stoichiometry';
    var msg = event.message;
    console.log(msg);
    var greetings = ["hi","hey","sup","hello","good morning"];
    var greetingsp2=["what do you need help on?","what are you stuck on?","is there something you don't know how to do?","do you need some help?"]
    for(var i = 0;i<greetings.length;i++){
        if(msg.toLowerCase().startsWith(greetings[i])){
            console.log("determined it's greeting");
            curConversationInfo = [];
            inputData = [];
            curConversationInfo.push(true);
            var gResp = greetings[Math.floor(Math.random()*greetings.length)]+", "+greetingsp2[Math.floor(Math.random()*greetingsp2.length)];
            console.log("gResp:"+gResp)
            
            assignValues(context,inputData,curConversationInfo);
            context.sendResponse(gResp+currentProblems);
            return;
            
        }
    }
    if(!curConversationInfo[1]){
        msg = msg.match(/([^\w#]+|^)([\w#]+)/g).map(function(s){return s.trim() });
        curConversationInfo.push(true);
    }
    console.log(msg);
    if(!curConversationInfo[0]||!curConversationInfo[1]){
        assignValues(context,inputData,curConversationInfo);
        context.sendResponse("Wait, we still haven't determined what we should work on yet" + currentProblems);
        return;
    }
    var probType = ["stoich","balanc","sig","molar"];
    for(var j = 0;j<msg.length;j++){
        var curWord = msg[j].toLowerCase();
        for(var k = 0;k<probType.length;k++){
            if(curWord.startsWith(probType[k])){
                //handle prob type
                curConversationInfo.push(probType[k]);
                console.log("got here");
                var decidedType = (k==0?"stoichiometry":k==1?"balancing equations":k==2?"Sig Fig calculations":"molar mass calculations");
                console.log("stage 3 complete, decided it's: "+decidedType);
                var resps = ["Sure, let's do "+decidedType,"Alrite, "+decidedType+" it is!","Okay, "+decidedType+" sounds good."];
                finalResp = resps[Math.floor(Math.random()*resps.length)]+'\n';
                finalResp+="What is the unbalanced equation of our problem? \n Format is: <reactant 1> + <reactant 2> ... -> <product 1> + <product 2>...";
                curConversationInfo[2]=probType[k];
                assignValues(context,inputData,curConversationInfo);
                context.sendResponse(finalResp);
                return;
            }
        }
    }
    if(curConversationInfo[0]&&curConversationInfo[1]&&curConversationInfo[2]){
        if(curConversationInfo[2]=='stoich'){
            var len = inputData.length;
            if(len==0){
                inputData.push(msg);
                context.sendResponse("What information are you given? \n Format is: <amount> <unit> of <chemical>");
            }else if(len==1){
                inputData.push(msg);
                context.sendResponse("What are you trying to find? <measurement(volume, mass, or moles)> of <chemical>");
            }else{
                inputData.push(msg);
                //state1 amount chemS state2 chemE equation
                var properInput = "";
                var done = false;
                var temp = inputData[1].split(' ');
                console.log("raw input: "+inputData);
                console.log("temparr: "+temp);
                for(var i = 0;i<temp.length;i++){
                    var cur = temp[i].toLowerCase();
                    if(cur=='g'){
                        properInput+="mass ";
                        done = true;
                    }else if(cur=="l"){
                        properInput+="volume ";
                        done = true;
                    }else if(cur=='mol'){
                        properInput+="moles ";
                        done = true;
                    }
                    if(done){
                        properInput+=temp[i-1]+" "+temp[i+2]+" ";
                        break;
                    }
                }
                temp = inputData[2].split(' ');
                console.log("temparr2: "+temp);
                done = false;
                for(var i = 0;i<temp.length;i++){
                    var cur = temp[i].toLowerCase();
                    if(cur=='mass'){
                        properInput+="mass ";
                        done = true;
                    }else if(cur=="volume"){
                        properInput+="volume ";
                        done = true;
                    }else if(cur=="moles"){
                        properInput+="moles ";
                        done = true;
                    }
                    if(done){
                        properInput+=temp[i+2]+" ";
                        break;
                    }
                }
                properInput+=inputData[0];
                var req = {"messageobj":properInput};
                properInput = "Stoichiometry " + properInput;
                var dat = properInput.replace(/\s/g, "%20");
                console.log("properInput: " + properInput + " dat: " + dat);
                context.simplehttp.makeGet('http://sample-env.jbtpykbsa9.us-west-2.elasticbeanstalk.com/'+dat, req);
            }
        }
    }
}
function assignValues(context,inputData,curConversationInfo){
    context.simpledb.roomleveldata.inputData=inputData;
    context.simpledb.roomleveldata.curConversationInfo=curConversationInfo;
}
/** Functions declared below are required **/
function EventHandler(context, event) {
    if(! context.simpledb.botleveldata.numinstance)
        context.simpledb.botleveldata.numinstance = 0;
    numinstances = parseInt(context.simpledb.botleveldata.numinstance) + 1;
    context.simpledb.botleveldata.numinstance = numinstances;
    context.sendResponse("Thanks for adding me. Greet me to begin conversation!");
}

function HttpResponseHandler(context, event) {
    //context.sendResponse("Got response!");
    // if(event.geturl === "http://ip-api.com/json")
    //context.sendResponse(event.getresp);
    console.log(event);
    console.log(event.getresp)
    event.getresp = event.getresp.replace(/\\\\/g,"\\");
    /*var msg = JSON.parse(event.getresp);
    context.sendResponse(msg);*/
    var image = {"type":"image","originalUrl":event.getresp,"previewUrl":event.getresp};
    //console.log(JSON.stringify(image));
    context.sendResponse(JSON.stringify(image));//JSON.stringify(image));
}

function DbGetHandler(context, event) {
    context.sendResponse("testdbput keyword was last get by:" + event.dbval);
}

function DbPutHandler(context, event) {
    context.sendResponse("testdbput keyword was last put by:" + event.dbval);
}