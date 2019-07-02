@echo off
set current_path=%~dp0
:: 指定MANIFEST.MF的main方法，并且把依赖全部打在一个jar文件里generateSql.jar
java -jar %current_path%\generateSql.jar
pause
