set JAVA_HOME=
set PATH=%PATH$;%JAVA_HOME%/bin;

java -Dconfig.location=D:\Javaworksapce\scalp\src\main\resources\application.properties -Dnfo-symbol-location=D:\Javaworksapce\scalp\src\main\resources\NFO-symbol.csv -jar stock-scalping-0.0.1-SNAPSHOT.jar
