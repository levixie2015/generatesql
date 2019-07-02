@echo off

::set PATH=.;%JAVA_HOME%\bin

:: 指定MANIFEST.MF的main方法，并且指定classPath及依赖jar文件的路径为lib/
java  -jar generatesql-1.0-SNAPSHOT.jar
pause