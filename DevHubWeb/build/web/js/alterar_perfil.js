function adicionarImagem() {
    $("#img-perfil").trigger('click');
}
function mudarImagem(element) {
    if ($("#img-perfil").val().endsWith(".jpg")) {
        $("#label-imagem").show();
    } else {
        $("#label-imagem").hide();
        document.getElementById("img-perfil").value = "";
    }
}
function validaSenha() {
    var isInvalid = $('#senha').val() !== $('#confirmar-senha').val() || $('#senha').val().length < 5;
    if ($('#senha').val() !== $('#confirmar-senha').val()) {
        document.getElementById("confirmar-senha").className = "form-control is-invalid";
    } else if ($('#senha').val().length < 5) {
        document.getElementById("senha").className = "form-control is-invalid";
        document.getElementById("confirmar-senha").className = "form-control is-invalid";
    } else {
        document.getElementById("senha").className = "form-control";
        document.getElementById("confirmar-senha").className = "form-control";
    }
    return !isInvalid;
}
function validaForm() {
    if (validaNomeCompleto()
            & validaNomeDeUsuario()
            & validaPergunta1()
            & validaPergunta2()
            & validaResposta1()
            & validaResposta2()
            & validaSenha()
            & validaEmail()) {
        $("#form-alteracao").submit();
    }
}
function validaNomeDeUsuario() {
    var isInvalid = $('#nome-de-usuario').val() === "" || $('#nome-de-usuario').val().includes(" ");
    if (isInvalid) {
        document.getElementById("nome-de-usuario").className = "form-control is-invalid";
    } else {
        document.getElementById("nome-de-usuario").className = "form-control";
    }
    return !isInvalid;
}
function validaNomeCompleto() {
    var isInvalid = $('#nome-completo').val() === "";
    if ($('#nome-completo').val() === "") {
        document.getElementById("nome-completo").className = "form-control is-invalid";
    } else {
        document.getElementById("nome-completo").className = "form-control";
    }
    return !isInvalid;
}
function validaPergunta1() {
    var isInvalid = $('#pergunta1').val() === null;
    if ($('#pergunta1').val() === null) {
        document.getElementById("pergunta1").className = "form-control is-invalid";
    } else {
        document.getElementById("pergunta1").className = "form-control";
    }
    return !isInvalid;
}
function validaResposta1() {
    var isInvalid = $('#resposta1').val() === "";
    if ($('#resposta1').val() === "") {
        document.getElementById("resposta1").className = "form-control is-invalid";
    } else {
        document.getElementById("resposta1").className = "form-control";
    }
    return !isInvalid;
}
function validaPergunta2() {
    var isInvalid = $('#pergunta2').val() === null;
    if ($('#pergunta2').val() === null) {
        document.getElementById("pergunta2").className = "form-control is-invalid";
    } else {
        document.getElementById("pergunta2").className = "form-control";
    }
    return !isInvalid;
}
function validaResposta2() {
    var isInvalid = $('#resposta2').val() === "";
    if ($('#resposta2').val() === "") {
        document.getElementById("resposta2").className = "form-control is-invalid";
    } else {
        document.getElementById("resposta2").className = "form-control";
    }
    return !isInvalid;
}
function validaEmail() {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    var isInvalid = $('#email').val() === "" || !re.test(String($('#email').val()).toLowerCase());
    if ($('#email').val() === "" || !re.test(String($('#email').val()).toLowerCase())) {
        document.getElementById("email").className = "form-control is-invalid";
    } else {
        document.getElementById("email").className = "form-control";
    }
    return !isInvalid;
}