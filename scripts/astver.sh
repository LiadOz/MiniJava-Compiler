#!/bin/bash
# use xmllint to verify ast
# xmllint is part of libxml2-utils

full_path=$(realpath $0)
dir_path=$(dirname $full_path)
schema_file=$dir_path/../schema/ast.xsd
xmllint --schema $schema_file $1 -noout
