REM set environment
set JAVA_HOME=c:\java\jdk-8
set PATH=%JAVA_HOME%\bin;%PATH%

REM compile, test and build assembly
sbt clean compile test IntegrationTest/test assembly