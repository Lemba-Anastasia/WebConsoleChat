var ws;
var currentChatId;

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
        showGreeting(data.data.toString().trim().replace(/\n/g, '<br>'));
    };
    setConnected(true);
}

function showGreeting(message) {
    console.log(message);
    var reg = /NEWCHAT\d+/;
    if (message.match(reg) == message) {
        console.log("NEW_CHAT CREATION");
        addNewTab(message.split("NEWCHAT")[1]);
    }
    else if (message.match(/\d+::(\w+:)(\s*\w+\s*)+/) != null &&
        message.match(/\d+::(\w+:)(\s*\w+\s*)+/)[0] == message) {
        console.log("MESAGE RECEIVED");
        var userId = message.split("::")[0];
        var idOfTable = "#greetings" + userId;
        $(idOfTable).append("<tr><td> " + message.toString() + "</td></tr>");
    } else if (message.match(/server:(\s*\w+\s*)+/) != null &&
        message.match(/server:(\s*\w+\s*)+/)[0] == message) {
        alert(message);
    }
}

function addNewTab(userId) {
    $(".nav-tabs").append('<li><a data-toggle="tab" onclick="tabClick(' + userId + ')" id="' + userId + 'taba" href="#conversation' + userId + '">' + userId + '</a></li>\n');
    createNewChatWindow(userId)
}

function createNewChatWindow(userId) {
    var table = '' +
        '<table id="conversation' + userId + '" class="tab-pane fade table table-striped">' +
        '   <thead>' +
        '   </thead>' +
        '  <tbody id="greetings' + userId + '">' +
        '   </tbody>' +
        '</table>';
    $(".tab-content").append(table);
}

function tabClick(n) {
    currentChatId=n;
}

function closeChat() {
    var data = JSON.stringify({'message': "/leaveCurrentChat:"+currentChatId})
    ws.send(data);
    $('#'+ "conversation"+ currentChatId).remove();
    $('#' + currentChatId + "taba").remove();
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
        closeChat();
    });
});