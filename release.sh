mvn clean

# https://issues.sonatype.org/browse/OSSRH-66257
export MAVEN_OPTS="--add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.text=ALL-UNNAMED --add-opens=java.desktop/java.awt.font=ALL-UNNAMED"

mvn release:prepare

mvn release:perform

# git push --tags
# git push origin main
