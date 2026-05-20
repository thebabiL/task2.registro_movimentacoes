#!/usr/bin/env bash
set -euo pipefail

: "${VM_HOST:=177.44.248.51}"
: "${VM_USER:=univates}"
: "${VM_BASE_DIR:=/opt/registro-movimentacoes}"
: "${SSH_KEY_FILE:=}"

SSH_OPTIONS=(-o StrictHostKeyChecking=accept-new)

if [ -n "$SSH_KEY_FILE" ]; then
  SSH_OPTIONS+=(-i "$SSH_KEY_FILE")
fi

ssh "${SSH_OPTIONS[@]}" "${VM_USER}@${VM_HOST}" "mkdir -p '${VM_BASE_DIR}/homolog' '${VM_BASE_DIR}/production' && docker --version && docker compose version && docker ps"
