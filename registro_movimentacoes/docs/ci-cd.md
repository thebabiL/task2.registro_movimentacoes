# CI/CD simples para Gerencia de Configuracao de Software

## Arquitetura

- GitHub: versionamento do codigo, Dockerfile, Jenkinsfile, compose, scripts e changelogs do banco.
- Jenkins: ambiente de integracao, execucao de testes, estatisticas JUnit/Jacoco, Checkstyle, build Docker e aprovacao manual.
- Docker Compose: sobe aplicacao Spring Boot e MongoDB por ambiente.
- VM Linux via SSH: hospeda homologacao e producao.
- Mongock: versionamento simples do MongoDB por classes `DatabaseChangelogXXX`.

## Estrutura adicionada

```text
Jenkinsfile
Dockerfile
docker-compose.integration.yml
docker-compose.homolog.yml
docker-compose.production.yml
config/checkstyle.xml
scripts/validate-test-count.sh
deploy/prepare-vm.sh
deploy/deploy.sh
docs/evidencias.md
src/main/java/task2/registro_movimentacoes/changelogs/DatabaseChangelog001.java
```

## Separacao dos ambientes

| Ambiente | Onde roda | Porta | Banco Mongo | Deploy |
|---|---|---:|---|---|
| Integracao | Jenkins | 8081 | `controle_financeiro_int` | automatico |
| Homologacao | VM Linux | 8082 | `controle_financeiro_homolog` | manual no Jenkins |
| Producao | VM Linux | 8080 | `controle_financeiro_prod` | manual no Jenkins |

Cada ambiente tem container, volume e banco separados.

## Configuracao minima do Jenkins

Plugins sugeridos:

- Pipeline
- Git
- JUnit
- SSH Agent
- Warnings Next Generation
- Docker instalado no agente Jenkins

Credenciais no Jenkins:

- `vm-ssh-key`: chave privada SSH para acessar a VM `univates@177.44.248.51`.
- O repositorio GitHub e publico: nao precisa token para clonar.
- `VM_HOST`, `VM_USER`, email e senha de email ja estao definidos no `Jenkinsfile`.

Pipeline:

1. Criar job do tipo Multibranch Pipeline ou Pipeline from SCM.
2. Apontar para o repositorio GitHub.
3. Usar o script path `registro_movimentacoes/Jenkinsfile`.
4. Garantir que o agente Jenkins tenha Java 21, Maven ou `mvnw`, Docker e Docker Compose.

## Preparacao da VM Linux

Na VM, instalar Docker e liberar o univates para Docker:

```bash
sudo apt update
sudo apt install -y docker.io docker-compose-plugin
sudo usermod -aG docker $USER
newgrp docker
```

No Jenkins, testar SSH:

```bash
export VM_HOST=177.44.248.51
export VM_USER=univates
./deploy/prepare-vm.sh
```

## Fluxo de entrega

1. Fazer mudanca no codigo.
2. Registrar a mudanca em commit Git com mensagem clara.
3. Enviar para GitHub.
4. Jenkins executa testes, conta minimo de 20 testes, gera estatisticas e executa Checkstyle.
5. Jenkins cria o jar e a imagem Docker.
6. Jenkins sobe integracao automaticamente.
7. Jenkins pede aprovacao manual para homologacao.
8. Jenkins pede aprovacao manual para producao.

## Versionamento do MongoDB

Usar Mongock. Para cada alteracao estrutural ou carga controlada no banco, criar nova classe:

```text
src/main/java/task2/registro_movimentacoes/changelogs/DatabaseChangelog002.java
```

Padrao:

```java
@ChangeUnit(id = "002-descricao-curta", order = "002", author = "seu-nome")
public class DatabaseChangelog002 {
    @Execution
    public void execution(MongoTemplate mongoTemplate) {
        // alteracao do banco
    }

    @RollbackExecution
    public void rollbackExecution(MongoTemplate mongoTemplate) {
        // reversao simples, quando aplicavel
    }
}
```

O Mongock registra no proprio Mongo quais changelogs ja foram executados.
