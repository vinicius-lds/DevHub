function validaNomeDeUsuario() {
    var isInvalid = $('#nome-de-usuario').val() === "" || $('#nome-de-usuario').val().includes(" ");
    if(isInvalid) {
        document.getElementById("nome-de-usuario").className = "form-control is-invalid";
    } else {
        document.getElementById("nome-de-usuario").className = "form-control";
    }
    return !isInvalid;
}

function validaSenha() {
    var isInvalid = $('#senha').val() !== $('#confirmar-senha').val() || $('#senha').val().length < 5;
    if ($('#senha').val() !== $('#confirmar-senha').val()) {
        document.getElementById("confirmar-senha").className = "form-control is-invalid";
    } else if($('#senha').val().length < 5) {
        document.getElementById("senha").className = "form-control is-invalid";
        document.getElementById("confirmar-senha").className = "form-control is-invalid";
    } else {
        document.getElementById("senha").className = "form-control";
        document.getElementById("confirmar-senha").className = "form-control";
    }
    return !isInvalid;
}

function validaForm() {
    if (validaNomeDeUsuario() & validaSenha()) {
        $("#form-cadastro").submit();
    }
}