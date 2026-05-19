#!/usr/bin/env bash
set -euo pipefail

MIN_TESTS="${1:-20}"
REPORT_DIR="target/surefire-reports"

if [ ! -d "$REPORT_DIR" ]; then
  echo "Diretorio de relatorios nao encontrado: $REPORT_DIR"
  exit 1
fi

TOTAL=$(find "$REPORT_DIR" -name 'TEST-*.xml' -print0 \
  | xargs -0 sed -n 's/.*tests="\([0-9][0-9]*\)".*/\1/p' \
  | awk '{sum += $1} END {print sum + 0}')

echo "Total de testes automatizados executados: $TOTAL"

if [ "$TOTAL" -lt "$MIN_TESTS" ]; then
  echo "Falha: minimo exigido de testes automatizados: $MIN_TESTS"
  exit 1
fi
