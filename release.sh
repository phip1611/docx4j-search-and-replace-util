#!/usr/bin/env bash

set -euo pipefail

# See what happens
set -x

mvn clean

# https://issues.sonatype.org/browse/OSSRH-66257
export MAVEN_OPTS="--add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.text=ALL-UNNAMED --add-opens=java.desktop/java.awt.font=ALL-UNNAMED"

mvn release:prepare

mvn verify gpg:sign

mvn release:perform

rm release.properties
rm pom.xml.releaseBackup

git push --tags
git push origin main
