const API = "http://177.44.248.51:8081/lancamentos";
let editandoId = null;

// 1. Atualizado: Adicionado o parâmetro tipoFiltro
function carregar(dataInicioFiltro = "", dataFimFiltro = "", situacaoFiltro = "", tipoFiltro = "") {
    let url = API;
    const params = new URLSearchParams();

    if (dataInicioFiltro) params.append("dataInicio", dataInicioFiltro);
    if (dataFimFiltro) params.append("dataFim", dataFimFiltro);
    if (situacaoFiltro) params.append("situacao", situacaoFiltro);
    if (tipoFiltro) params.append("tipo", tipoFiltro); // Inclui o novo filtro na requisição

    if (params.toString()) {
        url += "?" + params.toString();
    }

    fetch(url)
        .then(res => res.json())
        .then(data => {
            const tabela = document.getElementById("tabela");
            tabela.innerHTML = "";

            data.forEach(l => {
                let dataFormatada = "";
                let dataOriginal = "";

                if (l.dataLancamento) {
                    if (Array.isArray(l.dataLancamento)) {
                        const ano = l.dataLancamento[0];
                        const mes = String(l.dataLancamento[1]).padStart(2, '0');
                        const dia = String(l.dataLancamento[2]).padStart(2, '0');
                        dataFormatada = `${dia}/${mes}/${ano}`;
                        dataOriginal = `${ano}-${mes}-${dia}`;
                    } 
                    else if (typeof l.dataLancamento === 'string') {
                        dataOriginal = l.dataLancamento.split('T')[0]; 
                        const partes = dataOriginal.split('-');
                        dataFormatada = `${partes[2]}/${partes[1]}/${partes[0]}`;
                    }
                }

                tabela.innerHTML += `
                    <tr>
                        <td>${l.descricao}</td>
                        <td>${dataFormatada}</td>
                        <td>R$ ${l.valor}</td>
                        <td>${l.tipo}</td>
                        <td>${l.situacao}</td>
                        <td class="acoes-cell">
                            <button class="outline" onclick="editar('${l.id}', '${l.descricao}', ${l.valor}, '${l.tipo}', '${l.situacao}', '${dataOriginal}')">Editar</button>
                            <button class="danger" onclick="deletar('${l.id}')">Excluir</button>
                        </td>
                    </tr>
                `;
            });
        })
        .catch(err => console.error("Erro ao carregar a lista:", err));
}

// 2. Atualizado: Capturando o valor do novo filtro
function filtrar() {
    const dataInicio = document.getElementById("filtroDataInicio").value;
    const dataFim = document.getElementById("filtroDataFim").value;
    const situacao = document.getElementById("filtroSituacao").value;
    const tipo = document.getElementById("filtroTipo").value; // NOVO
    
    if ((dataInicio && !dataFim) || (!dataInicio && dataFim)) {
        alert("Para filtrar por data, preencha tanto o Início quanto o Fim.");
        return;
    }

    carregar(dataInicio, dataFim, situacao, tipo); // Passando o 4º parâmetro
}

// 3. Atualizado: Limpando o novo filtro
function limparFiltros() {
    document.getElementById("filtroDataInicio").value = "";
    document.getElementById("filtroDataFim").value = "";
    document.getElementById("filtroSituacao").value = "";
    document.getElementById("filtroTipo").value = ""; // NOVO
    carregar(); 
}

// 4. Atualizado: Incluindo o tipo na exportação do PDF
function exportarPDF() {
    let url = `${API}/exportar-pdf`;
    const params = new URLSearchParams();

    const dataInicio = document.getElementById("filtroDataInicio").value;
    const dataFim = document.getElementById("filtroDataFim").value;
    const situacao = document.getElementById("filtroSituacao").value;
    const tipo = document.getElementById("filtroTipo").value; // NOVO

    if (dataInicio) params.append("dataInicio", dataInicio);
    if (dataFim) params.append("dataFim", dataFim);
    if (situacao) params.append("situacao", situacao);
    if (tipo) params.append("tipo", tipo); // NOVO

    if (params.toString()) {
        url += "?" + params.toString();
    }

    window.location.href = url;
}

// =========================================================
// 5. NOVA FUNÇÃO: DELETAR (Resolve o erro do console)
// =========================================================
function deletar(id) {
    if (confirm("Tem certeza que deseja excluir esta movimentação?")) {
        fetch(`${API}/${id}`, {
            method: 'DELETE'
        })
        .then(res => {
            if (res.ok) {
                carregar(); // Atualiza a tabela se deu certo
            } else {
                alert("Erro ao excluir. Verifique se a movimentação ainda existe.");
            }
        })
        .catch(err => console.error("Erro ao deletar:", err));
    }
}

function salvar() {
    const descricao = document.getElementById("descricao").value;
    const valor = document.getElementById("valor").value;
    const tipo = document.getElementById("tipo").value;
    const situacao = document.getElementById("situacao").value;
    const dataInput = document.getElementById("dataLancamento").value;
    const emailDestino = document.getElementById("emailDestino").value;

    if (!descricao || !valor || !emailDestino) {
        alert("Preencha todos os campos obrigatórios (Descrição, Valor e E-mail)!");
        return;
    }

    const dataLancamento = dataInput ? dataInput : new Date().toISOString().split("T")[0];

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
            dataLancamento,
            emailDestino
        })
    })
    .then(() => {
        limpar();
        carregar();
    });
}

function editar(id, descricao, valor, tipo, situacao, dataLancamento) {
    document.getElementById("descricao").value = descricao;
    document.getElementById("valor").value = valor;
    document.getElementById("tipo").value = tipo;
    document.getElementById("situacao").value = situacao;
    document.getElementById("dataLancamento").value = dataLancamento || ""; 

    editandoId = id;
}

function limpar() {
    document.getElementById("descricao").value = "";
    document.getElementById("valor").value = "";
    document.getElementById("tipo").value = "RECEITA"; 
    document.getElementById("situacao").value = "PENDENTE";
    document.getElementById("dataLancamento").value = "";
    editandoId = null;
}

function logout() {
    localStorage.removeItem("usuario");
    window.location.href = "login.html";
}

carregar();