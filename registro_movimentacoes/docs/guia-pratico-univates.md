# Guia pratico CI/CD - VM Univates

Este guia esta adaptado para a sua VM real:

```text
SSH: univates@177.44.248.51
VM_HOST: 177.44.248.51
VM_USER: univates
Homologacao: http://177.44.248.51:8082
Producao: http://177.44.248.51:8080
Integracao: http://177.44.248.51:8081
Jenkins: http://177.44.248.51:8085
```

Se a universidade bloquear acesso externo as portas, use tunel SSH no Windows:

```powershell
ssh -L 8085:localhost:8085 -L 8081:localhost:8081 -L 8082:localhost:8082 -L 8080:localhost:8080 univates@177.44.248.51
```

Com o tunel aberto:

```text
Jenkins: http://localhost:8085
Integracao: http://localhost:8081
Homologacao: http://localhost:8082
Producao: http://localhost:8080
```

## Arquivos ja adaptados

Os arquivos abaixo ja estao prontos no projeto:

```text
Jenkinsfile
Dockerfile
docker-compose.integration.yml
docker-compose.homolog.yml
docker-compose.production.yml
deploy/deploy.sh
deploy/prepare-vm.sh
src/main/resources/application.properties
.env.example
jenkins/Dockerfile
jenkins/docker-compose.yml
```

Os valores reais de VM, GitHub, portas e email ja foram aplicados nos arquivos. A unica informacao que nao fica no repositorio e a chave SSH privada do Jenkins, que voce gera no Windows e cadastra no Jenkins como `vm-ssh-key`.

## PASSO 1 - Comandos no Windows

Abra PowerShell no projeto:

```powershell
cd C:\Users\barba\OneDrive\Documentos\task_2\registro_movimentacoes
```

Gere uma chave SSH para o Jenkins acessar a VM:

```powershell
ssh-keygen -t ed25519 -C "jenkins-univates" -f $env:USERPROFILE\.ssh\jenkins_univates
```

Copie a chave publica para a VM:

```powershell
type $env:USERPROFILE\.ssh\jenkins_univates.pub | ssh univates@177.44.248.51 "mkdir -p ~/.ssh && cat >> ~/.ssh/authorized_keys && chmod 700 ~/.ssh && chmod 600 ~/.ssh/authorized_keys"
```

Teste o SSH:

```powershell
ssh -i $env:USERPROFILE\.ssh\jenkins_univates univates@177.44.248.51 "hostname && whoami"
```

Copie a chave privada para cadastrar no Jenkins:

```powershell
Get-Content $env:USERPROFILE\.ssh\jenkins_univates
```

Opcional: enviar a pasta do Jenkins para a VM:

```powershell
ssh univates@177.44.248.51 "mkdir -p ~/jenkins-registro"
scp .\jenkins\Dockerfile univates@177.44.248.51:~/jenkins-registro/Dockerfile
scp .\jenkins\docker-compose.yml univates@177.44.248.51:~/jenkins-registro/docker-compose.yml
```

Enviar codigo para GitHub:

```powershell
git status
git add .
git commit -m "Configura pipeline CI/CD com Jenkins Docker e VM Univates"
git push origin main
```

## PASSO 2 - Comandos na VM

Entre na VM:

```bash
ssh univates@177.44.248.51
```

Instale Docker e Docker Compose:

```bash
sudo apt update
sudo apt install -y ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

sudo tee /etc/apt/sources.list.d/docker.sources <<EOF
Types: deb
URIs: https://download.docker.com/linux/ubuntu
Suites: $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}")
Components: stable
Architectures: $(dpkg --print-architecture)
Signed-By: /etc/apt/keyrings/docker.asc
EOF

sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```

Libere Docker para o usuario `univates`:

```bash
sudo usermod -aG docker univates
newgrp docker
docker run hello-world
docker compose version
```

Crie os diretorios dos ambientes:

```bash
sudo mkdir -p /opt/registro-movimentacoes/homolog
sudo mkdir -p /opt/registro-movimentacoes/production
sudo chown -R univates:univates /opt/registro-movimentacoes
```

Suba o Jenkins na VM:

```bash
cd ~/jenkins-registro
docker compose up -d --build
docker ps
```

Pegue a senha inicial do Jenkins:

```bash
docker exec jenkins-registro cat /var/jenkins_home/secrets/initialAdminPassword
```

Acesse:

```text
http://177.44.248.51:8085
```

Se nao abrir, use o tunel SSH mostrado no inicio e acesse:

```text
http://localhost:8085
```

## PASSO 3 - Configurar Jenkins

Na primeira tela:

```text
1. Cole a senha inicial.
2. Clique em Install suggested plugins.
3. Crie um usuario admin.
4. Finalize.
```

Instale plugins adicionais em:

```text
Manage Jenkins > Plugins > Available plugins
```

Instale:

```text
Pipeline
Git
JUnit
SSH Agent
Warnings Next Generation
```

## PASSO 4 - Credenciais Jenkins

Menu:

```text
Manage Jenkins > Credentials > System > Global credentials (unrestricted) > Add Credentials
```

Crie exatamente esta credencial obrigatoria:

| ID | Tipo | Valor |
|---|---|---|
| `vm-ssh-key` | SSH Username with private key | username `univates`, private key = conteudo de `C:\Users\barba\.ssh\jenkins_univates` |

O `Jenkinsfile` ja contem:

```text
VM_HOST=177.44.248.51
VM_USER=univates
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=barbara.leidemer@universo.univates.br
MAIL_PASSWORD=xqqmjlkllwdmmzfy
```

Onde a credencial e usada:

```text
vm-ssh-key: Jenkinsfile, sshagent(credentials: ['vm-ssh-key'])
```

## PASSO 5 - Criar pipeline

No Jenkins:

```text
New Item > Pipeline
Nome: registro-movimentacoes
OK
```

Em Pipeline:

```text
Definition: Pipeline script from SCM
SCM: Git
Repository URL: https://github.com/thebabiL/task2.registro_movimentacoes.git
Credentials: - none -
Branch Specifier: */main
Script Path: Jenkinsfile
```

Clique em `Save`.

## PASSO 6 - Executar primeira vez

No Jenkins:

```text
Build Now
```

Ordem dos stages:

```text
Registro de mudanca: mostra ultimo commit.
Preparar Maven: da permissao aos scripts.
Testes automatizados: roda 21 testes e valida minimo 20.
Qualidade de codigo: roda Checkstyle.
Build Maven: gera JAR.
Build Docker: gera imagem e target/app-image.tar.
Deploy Integracao: sobe app + Mongo no Jenkins, porta 8081.
Deploy Homologacao: pede aprovacao manual e sobe VM porta 8082.
Deploy Producao: pede aprovacao manual e sobe VM porta 8080.
```

## PASSO 7 - Validar ambientes

Na VM:

```bash
docker ps
docker compose -p registro_homolog -f /opt/registro-movimentacoes/homolog/docker-compose.yml ps
docker compose -p registro_production -f /opt/registro-movimentacoes/production/docker-compose.yml ps
```

URLs finais:

```text
Integracao: http://177.44.248.51:8081
Homologacao: http://177.44.248.51:8082
Producao: http://177.44.248.51:8080
```

Via curl na VM:

```bash
curl -I http://localhost:8081
curl -I http://localhost:8082
curl -I http://localhost:8080
```

## PASSO 8 - Validar MongoDB e Mongock

Homologacao:

```bash
docker exec -it registro-mongo-homolog mongosh
```

Dentro do Mongo:

```javascript
show dbs
use controle_financeiro_homolog
show collections
db.mongockChangeLog.find()
```

Producao:

```bash
docker exec -it registro-mongo-prod mongosh
```

Dentro do Mongo:

```javascript
show dbs
use controle_financeiro_prod
show collections
db.mongockChangeLog.find()
```

## PASSO 9 - Exemplos de .env

Homologacao gerado pelo deploy em:

```text
/opt/registro-movimentacoes/homolog/.env
```

Exemplo:

```env
IMAGE_NAME=registro_movimentacoes:15
APP_ENV=homolog
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=barbara.leidemer@universo.univates.br
MAIL_PASSWORD=xqqmjlkllwdmmzfy
JAVA_OPTS=-Xms128m -Xmx256m
```

Producao gerado pelo deploy em:

```text
/opt/registro-movimentacoes/production/.env
```

Exemplo:

```env
IMAGE_NAME=registro_movimentacoes:15
APP_ENV=production
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=barbara.leidemer@universo.univates.br
MAIL_PASSWORD=xqqmjlkllwdmmzfy
JAVA_OPTS=-Xms128m -Xmx256m
```

## PASSO 10 - Demonstrar atualizacao

No Windows:

```powershell
cd C:\Users\barba\OneDrive\Documentos\task_2\registro_movimentacoes
```

Altere um texto simples no HTML ou alguma mensagem da aplicacao.

Depois:

```powershell
git status
git add .
git commit -m "Demonstra atualizacao via pipeline"
git push origin main
```

No Jenkins:

```text
1. Build Now
2. Mostre testes passando.
3. Aprove homologacao.
4. Acesse http://177.44.248.51:8082.
5. Aprove producao.
6. Acesse http://177.44.248.51:8080.
```

## Evidencias para apresentacao

Tire prints de:

```text
GitHub com commits
Jenkins com pipeline verde
Stage Testes automatizados com 21 testes
Relatorio JUnit
Relatorio Jacoco em target/site/jacoco
Relatorio Checkstyle
Input manual de homologacao
Input manual de producao
docker ps na VM
Aplicacao em http://177.44.248.51:8081
Aplicacao em http://177.44.248.51:8082
Aplicacao em http://177.44.248.51:8080
MongoDB com bancos separados
MongoDB com mongockChangeLog
```

## Erros comuns

Docker permission denied:

```bash
sudo usermod -aG docker univates
newgrp docker
docker ps
```

Jenkins nao acessa Docker:

```bash
docker exec -it jenkins-registro bash
docker version
docker compose version
```

Se falhar, confira se o compose do Jenkins tem:

```yaml
volumes:
  - /var/run/docker.sock:/var/run/docker.sock
user: root
```

SSH denied:

```powershell
ssh -i $env:USERPROFILE\.ssh\jenkins_univates univates@177.44.248.51
```

No Jenkins, confira:

```text
Credential ID: vm-ssh-key
Username: univates
Private Key: chave privada, nao a .pub
```

Porta ocupada:

```bash
sudo lsof -i :8080
sudo lsof -i :8081
sudo lsof -i :8082
docker ps
```

Mongo nao conecta:

```bash
docker logs registro-mongo-homolog
docker logs registro-app-homolog
docker logs registro-mongo-prod
docker logs registro-app-prod
```

Pipeline falha no GitHub:

```text
Repositorio publico: nao precisa credencial.
Confira se a URL esta exatamente: https://github.com/thebabiL/task2.registro_movimentacoes.git
```

Pipeline falha no Checkstyle:

```text
Abra o artefato target/site/checkstyle.html no Jenkins.
```

Pipeline falha nos testes:

```text
Abra os relatorios JUnit target/surefire-reports/*.xml no Jenkins.
```
