This is my first project that I publish in Maven central.

## Workflow
Release to maven consists of two steps:
1) artifact is pushed to https://oss.sonatype.org/#nexus-search;quick~phip1611
2) they push it further to maven central

Current POM must contain a snapshot versions for a release!
https://stackoverflow.com/questions/20054185/you-dont-have-a-snapshot-project-in-the-reactor-projects-list-when-using-jen
-> replacement to next release-version is automatically done during "mvn release:prepare"

Maven doesn't care about branches. Release can be done from "any" branch.


---
## Setup

1) (ONCE) I created an account on sonatype.org (they work as distributor into maven central)
   https://issues.sonatype.org/browse/OSSRH-60727
2) (PER MACHINE) I added login data in `$HOME/.m2/settings.xml`
   ```
   <settings>
       <servers>
           <server>
               <!-- publishing to maven central-->
               <!-- via: https://issues.sonatype.org/browse/OSSRH-60727-->
               <id>ossrh</id>
               <username>phip1611</username>
               <password>****</password>
           </server>
       </servers>
   </settings>
   ```
3) (ONCE; COPY KEY TO OTHER MACHINES AND ADD IT TO GPG) I created a gpg key on my Mac (don't forget to bac it up!) public ID:
   ```
   pub   rsa3072 2020-09-17 [SC]
                                 389BB8340DA168D0
         D21DE8C7C1BE873DD10DCB13389BB8340DA168D0
   uid                      Philipp Schuster (Maven Central publishing) <phip1611@gmail.com>
   sub   rsa3072 2020-09-17 [E]

   ```
4) (PER MACHINE) add gpg key to `$HOME/.m2/settings.xml`
   ```
   <profiles>
       <profile>
           <id>ossrh</id>
           <activation>
               <activeByDefault>true</activeByDefault>
           </activation>
           <properties>
                <!-- password for key: D21DE8C7C1BE873DD10DCB13389BB8340DA168D0
                     on local machine (in gpg keystore) -->
                <gpg.passphrase>****</gpg.passphrase>
           </properties>
       </profile>
   </profiles>
   ```
5) (ONCE) send pgp key to pgp.mit.edu server:
   ~~gpg --keyserver pgp.mit.edu --send-key 389BB8340DA168D0~~ (didn't worked during `mvn release:perform`)
   `gpg --keyserver keys.openpgp.org:11371 --send-key 389BB8340DA168D0`
6) (PER RELEASE) execute `sh release.sh`
