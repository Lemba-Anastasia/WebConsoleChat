var ws;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    ws = new WebSocket('ws://localhost:8080/agent');
    ws.onmessage = function (data) {
        showGreeting(data.data.toString().replace(/\n/g, '<br>'));
    };
    setConnected(true);
}
function addNewTab(userId, userName) {
    $(".nav-tabs").append('<li><a data-toggle="tab" id="' + userId + 'navtab" href="#' + userId + 'tab">' + userName + '</a></li>\n'
    );
    createNewChatWindow(userId)
}

function createNewChatWindow(userId) {
    var a =
        ' <div id="' + userId + 'tab" class="tab-pane fade">' +
        '   <ul id="' + userId + 'Messages" class="messages">\n' +
        '    </ul>\n' +
        '    <div class="bottom_wrapper clearfix">\n' +
        '        <div class="message_input_wrapper"><input class="message_input" id="' + userId + 'Input" placeholder="Type your message here..."/></div>\n' +
        '        <div class="send_message" id="' + userId + 'Send" onclick="sendMessage(\'' + userId + '\')">\n' +
        '            <div class="text">Send</div>\n' +
        '        </div>\n' +
        '    </div>' +
        '</div>';
    $(".col-md-12").append(a);
}

function disconnect() {
    if (ws != null) {
        ws.close();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendMessage() {
    var data = JSON.stringify({'message': $("#message").val()})
    ws.send(data);
}


function showGreeting(message) {
    if(message==="NEWCHAT"){
        addNewTab("","");//+ obj.id, obj.newUserName);//TODO
    }else{
        $("#greetings").append("<tr><td> " + message + "</td></tr>");
    }
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () {
        connect();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
    $("#send").click(function () {
        sendMessage();
    });
    $("#closeCurrentChat").click(function () {
        //closeChat();
    });
});