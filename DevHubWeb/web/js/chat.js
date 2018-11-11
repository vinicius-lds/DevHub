var path = window.location.pathname;
var contextoWeb = path.substring(0, path.indexOf('/', 1));
var endPointURL = "ws://" + window.location.host + contextoWeb + "/server";

var chatCliente = null;

var idProjetoSelecionado;

function conectar() {
    chatCliente = new WebSocket(endPointURL);
    chatCliente.onmessage = function (e) {
        console.log("mensagem nova");
        incomingMessage(JSON.parse(e.data));
    };
}

function desconectar() {
    chatCliente.close();
}

$(".messages").animate({scrollTop: $(document).height()}, "slow");
function newMessage() {
    console.log('aqui');
    message = $(".message-input input").val();
    document.getElementById("message-input").value = "";
    if ($.trim(message) == '') {
        return false;
    }
    var imagem = document.getElementById("profile-img").src;
    $('<li class="sent"><img src="' + imagem + '" alt="" /><p>' + message + '</p></li>').appendTo($('.messages ul'));
    $('.message-input input').val(null);

    $('.contact.active .preview').html('<span>Você: </span>' + message);
//    $(".messages").animate({scrollTop: $(document).height()}, "slow");
    document.getElementById("messages").scrollTop = document.getElementById("messages").scrollHeight;

    var idUsuario = document.getElementById("idUsuario").value;
    var nomeUsuario = document.getElementById("nome-usuario").innerHTML;

    chatCliente.send(JSON.stringify({"idUsuario": idUsuario, "idProjeto": idProjetoSelecionado, "imagem": imagem, "mensagem": message, "nomeUsuario" : nomeUsuario}));

}

function moveProjectUp(idProjeto, nomeUsuario, message) {
    var value = '<span>' + nomeUsuario + ': </span>' + message;
    if(idProjetoSelecionado != idProjeto) {
        value += '<span class="not-read-messages" id="not-read-messages">';
        console.log(document.getElementById(idProjeto).querySelector('#not-read-messages'));
        var qtd = document.getElementById(idProjeto).querySelector('#not-read-messages').innerHTML;
        value += parseInt(qtd) + 1;
        value += '</span>';
//        setMensagensNaoLidas(parseInt(qtd) + 1, idProjeto);
    }
    $('#' + idProjeto + ' .preview').html(value);
    $('#' + idProjeto + ' .preview').show();
    $('#' + idProjeto).prepend();
}

function setMensagensNaoLidas(quantidade, idProjeto) {
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("GET", "requisicoes_jquery/setMensagensNaoLidas.jsp?idUsuario=" + document.getElementById('idUsuario').value
            + "&idProjeto=" + idProjeto
            + "&quantidade=" + quantidade, true);
    xmlhttp.send();
}

function incomingMessage(msg) {
    var nomeUsuario = msg.nomeUsuario;
    message = msg.mensagem;
    
    moveProjectUp(msg.idProjeto, nomeUsuario, message);
    
    if (msg.idProjeto != idProjetoSelecionado) {
        return;
    }
    var imagem = msg.imagem;
    document.getElementById("message-input").value = "";
    if ($.trim(message) == '') {
        return false;
    }
    $('<li class="replies"><img src="' + imagem + '" alt="" /><p>' + message + '</p></li>').appendTo($('.messages ul'));
    $('.message-input input').val(null);

//    $('.contact.active .preview').html('<span>You: </span>' + message);
//    $(".messages").animate({scrollTop: $(document).height()}, "slow");;;
    document.getElementById("messages").scrollTop = document.getElementById("messages").scrollHeight;
    
    setMensagensNaoLidas(0, idProjeto);
    var nrm = $('#' + idProjeto + ' #not-read-messages');
    nrm.innerHTML = 0;
    nrm.hide();
}

$('.submit').click(function () {
    newMessage();
});

$(window).on('keydown', function (e) {
    if (e.which == 13) {
        newMessage();

        return false;
    }
});

$(document).ready(function () {
    $('#barra-de-pesquisa').keyup(function () {
        var valores = document.querySelectorAll('div ul li div div p');
        var query = document.getElementById('barra-de-pesquisa').value;
        var div;
        for (i = 0; i < valores.length; i += 2) {
            div = valores[i].parentElement.parentElement.parentElement;
            if (!valores[i].innerHTML.toString().toLowerCase().includes(query.toString().toLowerCase())) {
                div.style.display = 'none';
            } else {
                div.style.display = 'block';
            }
        }
    });
});
function select(element, idProjeto, nomeProjeto, imagem) {
    $('#content').show();
    idProjetoSelecionado = idProjeto;
    var valores = document.querySelectorAll('div ul li');
    for (var i = 0; i < valores.length; i++) {
        valores[i].className = 'contact';
    }
    element.className = 'contact active';
    var element = document.getElementById('foto-de-perfil');
    element.src = imagem;
    element = document.getElementById('nome-do-projeto');
    element.innerHTML = nomeProjeto;
    carregarMensagens(idProjeto);
    setMensagensNaoLidas(0, idProjeto);
    var nrm = $('#' + idProjeto + ' #not-read-messages');
    nrm.html(0);
    nrm.hide();
}

function carregarMensagens(idProjeto) {
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            var mensagens = JSON.parse(this.responseText);
            var innerHTML = "<ul>";
            var campoDeMensagens = document.getElementById("messages");
            var idUsuarioLogado = document.getElementById("idUsuario").value;
            var classeLi;
            Object.keys(mensagens).forEach(function (key) {
                var usuario = mensagens[key][0];
                var imagem = mensagens[key][1];
                var mensagem = mensagens[key][2];
                if (usuario == idUsuarioLogado) {
                    classeLi = "sent";
                } else {
                    classeLi = "replies";
                }
                innerHTML += "<li class='" + classeLi + "'>";
                innerHTML += "<img src='" + imagem + "' alt='' />";
                innerHTML += "<p>" + mensagem + "</p>";
                innerHTML += "</li>";
            });
            innerHTML += "</ul>";
            campoDeMensagens.innerHTML = innerHTML;
            document.getElementById("messages").scrollTop = document.getElementById("messages").scrollHeight;
        }
    };
    xmlhttp.open("GET", "requisicoes_jquery/getConversa.jsp?idProjeto=" + idProjeto, true);
    xmlhttp.send();
}