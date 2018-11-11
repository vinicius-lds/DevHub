function switchSelection(element) {
    var tr = element.parentNode.parentNode;
    var classButton = element.className;
    console.log("toActivate(Antes) -> " + document.getElementById("toActivate").value);
    console.log("toDeactivate(Antes) -> " + document.getElementById("toDeactivate").value);
    if (classButton === "btn btn-danger btn-custom") {
        element.className = "btn btn-success btn-custom";
        element.innerHTML = "V";
        document.getElementById("toActivate").value = document.getElementById("toActivate").value.replace(tr.id + ";", "");
        document.getElementById("toDeactivate").value += tr.id + ";";
    } else {
        element.className = "btn btn-danger btn-custom";
        element.innerHTML = "X";
        document.getElementById("toDeactivate").value = document.getElementById("toDeactivate").value.replace(tr.id + ";", "");
        document.getElementById("toActivate").value += tr.id + ";";
    }
    console.log("toActivate(Depois) -> " + document.getElementById("toActivate").value);
    console.log("toDeactivate(Depois) -> " + document.getElementById("toDeactivate").value);
}

function filtrarUsuarios(element) {
    var linhas = document.getElementsByClassName("tr-usuario");
    var pesquisa = element.value.toString().toLowerCase();
    console.log(pesquisa);
    for(var i = 0; i < linhas.length; ++i) {
        var usuario = linhas[i].querySelector('#nomeUsuario').innerHTML;
        if(usuario.toString().toLowerCase().includes(pesquisa) || pesquisa == "") {
            $(linhas[i]).show();
        } else {
            $(linhas[i]).hide();
        }
    }
}

function nivelChange(element) {
    var tr = element.parentNode.parentNode.parentNode;
    var idUsuario = tr.id;
    var novoNivel = element.options[element.selectedIndex].value;
    var input = document.getElementById('niveisUsuarios');
    input.value = input.value.replace(idUsuario + "->1;", "");
    input.value = input.value.replace(idUsuario + "->2;", "");
    input.value = input.value.replace(idUsuario + "->3;", "");
    input.value += idUsuario + "->" + novoNivel + ";";
    console.log(input.value);
}