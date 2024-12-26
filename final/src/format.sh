#!/bin/sh

# Copyright 2024 Carolina Pereira, Diogo Costa, Humberto Gomes, Sara Lopes
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

REPO_DIR="$(realpath "$(dirname -- "$0")")"
cd "$REPO_DIR" || exit 1

assert_installed_command() {
    if ! command -v "$1" > /dev/null; then
        echo "$1 is not installed! Please install it and try again. Leaving ..." >&2
        exit 1
    fi
}

assert_installed_command clang-format
assert_installed_command git

formatted_dir="$(mktemp -d)"
diff_file="$(mktemp)"
trap 'trap - EXIT; rm -rf "$formatted_dir" "$diff_file"; exit' EXIT TERM INT HUP

SOURCE_DIRS="app/src/main/java/dss"
for source_dir in $SOURCE_DIRS; do
    find "$source_dir" -type f | while IFS= read -r file; do
        mkdir -p "$(dirname "$formatted_dir/$file")"
        clang-format "$file" | sed "\$a\\" | sed 's/\s*$//' > "$formatted_dir/$file"
    done
    git --no-pager -c color.ui=always \
        diff --no-index "$source_dir" "$formatted_dir/$source_dir" >> "$diff_file"
done

if ! [ -s "$diff_file" ]; then
    echo "Already formatted! Leaving ..." >&2
    exit 0
elif [ "$1" = "--check" ]; then
    echo "Formatting errors!" >&2
    cat "$diff_file"
    exit 1
else
    less -R "$diff_file"
fi

stdbuf -o 0 printf "Agree with these changes? [Y/n]> "
IFS= read -r yn
if echo "$yn" | grep -Eq '^[Yy]([Ee][Ss])?$'; then
    for source_dir in $SOURCE_DIRS; do
        cp -r "$formatted_dir/$source_dir" "$(dirname "$source_dir")"
    done
    exit 0
elif echo "$yn" | grep -Eq '^[Nn][Oo]?$'; then
    echo "Source code left unformatted. Leaving ..." >&2
    exit 1
else
    echo "Invalid input. Leaving ..." >&2
    exit 1
fi
