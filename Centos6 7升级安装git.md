#Centos6/7升级安装git

###第一步：卸载旧的git版本

	yum remove git 

### 第二步：下载git

	wget --no-check-certificate https://www.kernel.org/pub/software/scm/git/git-2.8.4.tar.gz
###第三步：解压
	tar -zxf git-2.8.4.tar.gz
###第四步：安装依赖包
	yum install curl-devel expat-devel gettext-devel openssl-devel zlib-devel
	yum install  gcc perl-ExtUtils-MakeMaker
###第五步：编译安装
	## 切换到git目录
	cd git-2.8.4
	## 创建要安装的目录
	mkdir -p  /usr/local/git
	## 编译安装
	make prefix=/usr/local/git all
	make prefix=/usr/local/git install
###第六步：添加环境变量
	## 添加环境变量
	vim /etc/profile
	## 添加以下配置
	export PATH=$PATH:/usr/local/git/bin
###第七步：使配置生效
	## 使新加的环境变量生效
	source /etc/profile
	## 验证是否配置成功
	git --version