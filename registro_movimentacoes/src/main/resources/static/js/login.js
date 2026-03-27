function logar() {
    const login = document.getElementById("login").value;
    const senha = document.getElementById("senha").value;
    const erro = document.getElementById("erro");

    erro.innerText = "";

    if (!login || !senha) 
      {
        erro.innerText = "Preencha login e senha!";
        return;
    }

    fetch("http://177.44.248.51:8081/usuarios/login", 
      {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({ login, senha })
    })
    .then(res => 
      {
        if (res.status === 401) 
          {
            throw new Error("Login inválido");
        }
        return res.json();
    })
    .then(user => 
      {
        localStorage.setItem("usuario", JSON.stringify(user));
        window.location.href = "index.html";
    })
    .catch(err => 
      {
        if (err.message === "Login invalido") 
          {
            erro.innerText = "Login ou senha invalidos!";
        } 
        else 
          {
            erro.innerText = "Erro ao conectar com o servidor!";
        }
    });
}