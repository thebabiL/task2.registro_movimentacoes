# Evidencias para apresentacao

Use esta lista como roteiro de validacao do trabalho.

## Registro de mudanca e versionamento

- Print do GitHub com commits.
- Print de uma alteracao de codigo ou changelog do banco.
- Print da branch `develop` ou `main` acionando o Jenkins.

## Testes automatizados e estatisticas

- Print do stage `Testes automatizados` verde no Jenkins.
- Print do relatorio JUnit mostrando total de testes.
- Print ou arquivo de `target/site/jacoco/index.html` mostrando cobertura.
- Evidencia do script `validate-test-count.sh` exibindo pelo menos 20 testes.

## Qualidade de codigo

- Print do stage `Qualidade de codigo`.
- Arquivo arquivado `target/site/checkstyle.html`.
- Relatorio do plugin Warnings Next Generation, se instalado.

## Containers e ambientes

- Print do `docker compose -f docker-compose.integration.yml ps` no Jenkins.
- Na VM, prints dos comandos:

```bash
docker ps
docker compose -p registro_homolog ps
docker compose -p registro_production ps
```

## Banco versionado

- Print da classe `DatabaseChangelog001.java`.
- Print no MongoDB mostrando colecao de controle do Mongock.
- Demonstrar que a aplicacao sobe e executa o changelog automaticamente.

## Deploy semi-automatizado

- Print do Jenkins parado no input de homologacao.
- Print apos aprovar homologacao.
- Print do Jenkins parado no input de producao.
- Print apos aprovar producao.

## Demonstracao de atualizacao

1. Alterar um texto simples na tela ou endpoint.
2. Commitar e enviar para GitHub.
3. Mostrar Jenkins executando novamente.
4. Aprovar homologacao e acessar `http://177.44.248.51:8082`.
5. Aprovar producao e acessar `http://177.44.248.51:8080`.
6. Mostrar que integracao usa `http://177.44.248.51:8081`.
