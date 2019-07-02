@echo off
::cd %~dp0
::set JAVA_HOME=C:\Program Files\Java\jdk1.7.0_45

::set PATH=.;%JAVA_HOME%\bin

::java -version
:: 不指定MANIFEST.MF的main方法，并且不指定classPath及依赖jar文件的路径
SET LIB_PATH=.
FOR %%F IN (lib\*.jar) DO call :addcp %%F
goto extlibe
:addcp
SET LIB_PATH=%LIB_PATH%;%1
goto :eof
:extlibe
SET CLASSPATH=%LIB_PATH%

echo %CLASSPATH%

java  -classpath %CLASSPATH% com.lw.sql.SqlOperate
pause