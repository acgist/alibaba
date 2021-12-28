set JAVA_OPTS_MEM=-Xms${acgist.xms} -Xmx${acgist.xmx}
set JAVA_OPTS_EXT=-Dfile.encoding=${encoding}
set JAVA_OPTS_APP=-Dspring.profiles.active=${profile}
set JAVA_OPTS=%JAVA_OPTS_MEM% %JAVA_OPTS_EXT% %JAVA_OPTS_APP% ${acgist.jvm}
java %JAVA_OPTS% -jar .\lib\${project.artifactId}-${project.version}.jar