#!/usr/bin/env bash

set -euo pipefail

# See what happens
set -x

mvn clean

# Actually the profile should be invoked automatically, but apparently, I
# faced situations where the artifacts didn't get signed. So, better be
# explicit here.
mvn -Prelease-sign-artifacts release:prepare
mvn release:perform

git push --tags
git push origin main
