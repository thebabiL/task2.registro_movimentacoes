#!/usr/bin/env bash
set -euo pipefail

ENVIRONMENT="${1:?Uso: ./deploy/deploy.sh homolog|production IMAGE_NAME}"
IMAGE_NAME="${2:?Informe a imagem Docker. Ex.: registro_movimentacoes:build-10}"

case "$ENVIRONMENT" in
  homolog|production) ;;
  *) echo "Ambiente invalido: $ENVIRONMENT. Use homolog ou production."; exit 1 ;;
esac

: "${VM_HOST:=177.44.248.51}"
: "${VM_USER:=univates}"
: "${VM_BASE_DIR:=/home/univates/registro-movimentacoes}"
: "${JAVA_OPTS:=-Xms128m -Xmx256m}"
: "${SSH_KEY_FILE:=}"

LOCAL_COMPOSE="docker-compose.${ENVIRONMENT}.yml"
REMOTE_DIR="${VM_BASE_DIR}/${ENVIRONMENT}"
PROJECT_NAME="registro_${ENVIRONMENT}"
IMAGE_TAR="target/app-image.tar"
SSH_TARGET="${VM_USER}@${VM_HOST}"
SSH_OPTIONS=(-o StrictHostKeyChecking=accept-new)

if [ -n "$SSH_KEY_FILE" ]; then
  SSH_OPTIONS+=(-i "$SSH_KEY_FILE")
fi

if [ ! -f "$LOCAL_COMPOSE" ]; then
  echo "Arquivo nao encontrado: $LOCAL_COMPOSE"
  exit 1
fi

if [ ! -f "$IMAGE_TAR" ]; then
  echo "Arquivo nao encontrado: $IMAGE_TAR"
  exit 1
fi

echo "Criando diretorio remoto ${REMOTE_DIR}"
ssh "${SSH_OPTIONS[@]}" "${SSH_TARGET}" "mkdir -p '${REMOTE_DIR}'"

echo "Enviando compose e imagem para a VM"
scp "${SSH_OPTIONS[@]}" "$LOCAL_COMPOSE" "${SSH_TARGET}:${REMOTE_DIR}/docker-compose.yml"
scp "${SSH_OPTIONS[@]}" "$IMAGE_TAR" "${SSH_TARGET}:${REMOTE_DIR}/app-image.tar"

echo "Gerando arquivo .env remoto"
ssh "${SSH_OPTIONS[@]}" "${SSH_TARGET}" "cat > '${REMOTE_DIR}/.env'" <<EOF
IMAGE_NAME=${IMAGE_NAME}
APP_ENV=${ENVIRONMENT}
JAVA_OPTS=${JAVA_OPTS}
EOF

echo "Atualizando ambiente ${ENVIRONMENT}"
ssh "${SSH_OPTIONS[@]}" "${SSH_TARGET}" "cd '${REMOTE_DIR}' && docker compose -p '${PROJECT_NAME}' --env-file .env -f docker-compose.yml down --remove-orphans || true && docker load -i app-image.tar && docker compose -p '${PROJECT_NAME}' --env-file .env -f docker-compose.yml up -d && docker compose -p '${PROJECT_NAME}' ps"
