@echo off
:: 不指定MANIFEST.MF的main方法，指定classPath及依赖jar文件的路径为lib/
java  -cp generatesql-1.0-SNAPSHOT.jar com.lw.sql.SqlOperate start
pause