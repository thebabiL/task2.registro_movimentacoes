const API = "http://177.44.248.51:8081/lancamentos";

let editandoId = null;

// Atualizado para receber parâmetros de filtro opcionais
function carregar(dataFiltro = "", situacaoFiltro = "") {
    let url = API;
    const params = new URLSearchParams();

    if (dataFiltro) params.append("data", dataFiltro);
    if (situacaoFiltro) params.append("situacao", situacaoFiltro);

    if (params.toString()) {
        url += "?" + params.toString();
    }

    fetch(url)
        .then(res => res.json())
        .then(data => {
            const tabela = document.getElementById("tabela");
            tabela.innerHTML = "";

            data.forEach(l => {
                tabela.innerHTML += `
                    <tr>
                        <td>${l.descricao}</td>
                        <td>${l.valor}</td>
                        <td>${l.tipo}</td>
                        <td>${l.situacao}</td>
                        <td>
                            <button onclick="editar('${l.id}', '${l.descricao}', ${l.valor}, '${l.tipo}', '${l.situacao}')">Editar</button>
                            <button onclick="deletar('${l.id}')">Excluir</button>
                        </td>
                    </tr>
                `;
            });
        });
}

// Funções de Filtro e Exportação
function filtrar() {
    const data = document.getElementById("filtroData").value;
    const situacao = document.getElementById("filtroSituacao").value;
    carregar(data, situacao);
}

function limparFiltros() {
    document.getElementById("filtroData").value = "";
    document.getElementById("filtroSituacao").value = "";
    carregar(); // Carrega tudo novamente sem filtros
}

function exportarPDF() {
    window.location.href = `${API}/exportar-pdf`;
}

function salvar() {
    const descricao = document.getElementById("descricao").value;
    const valor = document.getElementById("valor").value;
    const tipo = document.getElementById("tipo").value;
    const situacao = document.getElementById("situacao").value;

    if (!descricao || !valor) {
        alert("Preencha todos os campos!");
        return;
    }

    const metodo = editandoId ? "PUT" : "POST";
    const url = editandoId ? `${API}/${editandoId}` : API;

    fetch(url, {
        method: metodo,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            descricao,
            valor: parseFloat(valor),
            tipo,
            situacao,
            dataLancamento: new Date().toISOString().split("T")[0]
        })
    })
    .then(() => {
        limpar();
        carregar();
    });
}

function editar(id, descricao, valor, tipo, situacao) {
    document.getElementById("descricao").value = descricao;
    document.getElementById("valor").value = valor;
    document.getElementById("tipo").value = tipo;
    document.getElementById("situacao").value = situacao;

    editandoId = id;
}

function deletar(id) {
    if (!confirm("Deseja excluir?")) return;

    fetch(`${API}/${id}`, {
        method: "DELETE"
    }).then(() => carregar());
}

function limpar() {
    document.getElementById("descricao").value = "";
    document.getElementById("valor").value = "";
    document.getElementById("tipo").value = "CREDITO";
    document.getElementById("situacao").value = "PENDENTE";
    editandoId = null;
}

function logout() {
    localStorage.removeItem("usuario");
    window.location.href = "login.html";
}

// Inicia carregando todos os dados
carregar();