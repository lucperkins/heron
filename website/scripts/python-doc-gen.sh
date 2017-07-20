#!/bin/bash

echo $HERONPY_VERSION
HERON_ROOT_DIR=$(git rev-parse --show-toplevel)
INPUT=heronpy
TMP_DIR=$(mktemp -d)

source $HERON_ROOT_DIR/website/scripts/common.sh

pip install heronpy==${HERONPY_VERSION}

mkdir -p static/api && rm -rf static/api/python

pdoc $INPUT \
  --html \
  --html-dir $TMP_DIR

mv $TMP_DIR/heronpy static/api/python
