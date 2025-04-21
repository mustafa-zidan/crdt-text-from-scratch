#!/bin/bash

INPUT_FILE=${1:-docs-structure.txt}
CURRENT_DIR=""

while IFS= read -r line; do
  # Count indentation
  indent=$(echo "$line" | sed -E 's/[^ ].*//')
  depth=$(( ${#indent} / 2 ))
  name=$(echo "$line" | sed 's/^ *//')

  # Skip empty lines
  if [ -z "$name" ]; then continue; fi

  # Build path stack
  while [ ${#stack[@]} -gt $depth ]; do
    unset 'stack[${#stack[@]}-1]'
  done

  stack[$depth]=$name

  full_path=$(IFS="/"; echo "${stack[*]}")

  if [[ "$name" == */ ]]; then
    mkdir -p "$full_path"
  else
    touch "$full_path"
  fi
done < "$INPUT_FILE"