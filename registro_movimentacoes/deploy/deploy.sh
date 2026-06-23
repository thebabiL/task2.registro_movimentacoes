#!/usr/bin/env bash
set -euo pipefail

ENVIRONMENT="${1:?Uso: ./deploy/deploy.sh homolog|production IMAGE_NAME}"
IMAGE_NAME="${2:?Informe a imagem Docker. Ex.: registro_movimentacoes:build-10}"

case "$ENVIRONMENT" in
  homolog|production) ;;
  *) echo "Ambiente invalido: $ENVIRONMENT. Use homolog ou production."; exit 1 ;;
esac

: "${JAVA_OPTS:=-Xms128m -Xmx256m}"

LOCAL_COMPOSE="docker-compose.${ENVIRONMENT}.yml"
PROJECT_NAME="registro_${ENVIRONMENT}"

if [ ! -f "$LOCAL_COMPOSE" ]; then
  echo "Arquivo nao encontrado: $LOCAL_COMPOSE"
  exit 1
fi

if ! docker image inspect "$IMAGE_NAME" >/dev/null 2>&1; then
  echo "Imagem Docker nao encontrada no daemon local: $IMAGE_NAME"
  echo "O stage Build Docker precisa criar a imagem antes do deploy."
  exit 1
fi

echo "Atualizando ambiente ${ENVIRONMENT} com a imagem ${IMAGE_NAME}"
IMAGE_NAME="$IMAGE_NAME" JAVA_OPTS="$JAVA_OPTS" docker compose -p "$PROJECT_NAME" -f "$LOCAL_COMPOSE" down --remove-orphans || true
IMAGE_NAME="$IMAGE_NAME" JAVA_OPTS="$JAVA_OPTS" docker compose -p "$PROJECT_NAME" -f "$LOCAL_COMPOSE" up -d
IMAGE_NAME="$IMAGE_NAME" JAVA_OPTS="$JAVA_OPTS" docker compose -p "$PROJECT_NAME" -f "$LOCAL_COMPOSE" ps
