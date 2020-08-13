#!/usr/bin/env bash

set -o errexit
set -o pipefail
set -o nounset
set -o xtrace

BASE="$(cd "$(dirname "${0}")" && pwd)"
source "${BASE}"/../.env


sbt clean test
#sbt ++2.13.0 clean coverage test coverageAggregate