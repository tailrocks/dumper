#!/usr/bin/env bash
ABSOLUTE_PATH=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
cd "${ABSOLUTE_PATH}" || exit

set -e

cd dumper-test

../gradlew generatePomFileForMavenJavaPublication build bintrayUpload
