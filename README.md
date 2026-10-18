# 员工画像管理系统（PersonMIS）

基于工作量明细、项目任务和福利积分的员工画像管理系统，包含 Java 后端、Vue 前端、Python 算法服务、MySQL 建库及演示数据、技术总结、功能汇报和操作视频。

## 仓库内容

| 目录或文件 | 内容 |
| --- | --- |
| `personmis/` | Spring Boot 后端、MyBatis 映射、SQL、Maven Wrapper 和原有测试代码 |
| `personmis-vue/` | Vue 前端、业务页面、图表组件和静态资源 |
| `personmis/python-service/` | 员工画像计算、扩展分析、Gurobi 人员分配与算法服务说明 |
| [中期技术说明](中期总结/) | 画像算法、界面与绩效、数据可视化、组织与员工管理的实现说明 |
| [功能总结](总结报告/) | 各模块功能、代码示例、问题定位与解决过程 |
| [功能汇报PPT](人事管理系统-员工画像.pptx) | 功能展示、计算原理、优化模型及技术问题解决 |
| [操作演示视频](视频演示.mp4) | 系统操作与功能讲解，下载后可本地播放 |

本仓库保留系统功能及技术资料，已移除开发成员姓名、学号、个人职责与分工材料，并清理 Office 文档中的作者信息。业务模块中的员工、部门、岗位及项目演示数据用于运行示例。

## 数据库连接配置

后端从环境变量读取数据库连接参数。`DB_URL` 默认使用本地 `personmis` 数据库，`DB_USERNAME` 默认为 `root`，`DB_PASSWORD` 需要填写自己的数据库密码。

Windows PowerShell，在启动 Java 后端的同一窗口设置：

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "你的本机MySQL密码"
cd personmis
.\mvnw.cmd spring-boot:run
```

Linux / WSL，在项目根目录执行：

```bash
export DB_USERNAME=root
read -rsp 'MySQL password: ' DB_PASSWORD
export DB_PASSWORD
cd personmis
./mvnw spring-boot:run
```

前端默认访问 `http://localhost:8443/personmis`；Python 算法服务使用 `http://localhost:5000`。Gurobi 许可证在本机单独配置，不放入仓库。

以下保留原项目的安装、建库、启动顺序与功能说明。

## 项目简介

人事管理系统是一个基于 Spring Boot + Vue 3 + Python Flask 的全栈人事管理平台，包含员工管理、部门管理、岗位管理、工作量统计、福利积分、员工画像分析等功能。

### 技术栈

- **后端**: Spring Boot 3.5.0 + MyBatis + MySQL
- **前端**: Vue 3 + Element Plus + Vue Router + Vuex
- **算法服务**: Python Flask + Gurobi 优化求解器
- **数据库**: MySQL 5.7+

---

## 环境要求

### 必需软件

1. **Java**: JDK 21 或更高版本
2. **Node.js**: 14.x 或更高版本（推荐 16.x+）
3. **Python**: 3.8 或更高版本（推荐 3.9+）
4. **MySQL**: 5.7 或更高版本（推荐 8.0+）
5. **Maven**: 3.6+（或使用项目自带的 mvnw）
6. **Gurobi Optimizer**: 11.0+（用于优化算法）

### 开发工具（可选）

- IntelliJ IDEA / Eclipse（Java开发）
- Visual Studio Code（前端开发）
- MySQL Workbench / Navicat（数据库管理）

---

## 完整安装步骤

### 第一步：数据库配置

#### 1.1 安装 MySQL

#### 1.2 启动 MySQL 服务
```powershell
# 通过服务管理器启动 MySQL 服务
# 或在命令行执行
net start MySQL
```

#### 1.3 创建数据库

登录 MySQL：
```bash
mysql -u root -p
```

创建数据库：
```sql
CREATE DATABASE personmis CHARACTER SET utf8 COLLATE utf8_unicode_ci;
```

#### 1.4 执行 SQL 脚本

按以下顺序执行 SQL 文件（在 `personmis/sql/` 目录下）：

**1. 执行主数据库结构脚本：**
```bash
mysql -u root -p personmis < personmis/sql/personmis_new.sql
```

或在 MySQL 客户端中：
```sql
USE personmis;
SOURCE D:/SVN/homework/personmis/sql/personmis_new.sql;
```

**2. 执行测试数据脚本：**
```bash
mysql -u root -p personmis < personmis/sql/test_data_insert.sql
```

**3. 执行工作量数据脚本：**
```bash
mysql -u root -p personmis < personmis/sql/workload_data.sql
```

### 第二步：Python 环境配置

#### 2.1 安装 Python

如果尚未安装 Python：

**Windows:**
- 下载地址：https://www.python.org/downloads/
- 安装时勾选 "Add Python to PATH"

**Linux:**
```bash
sudo apt-get update
sudo apt-get install python3 python3-pip
```

**Mac:**
```bash
brew install python3
```

验证安装：
```bash
python --version
# 或
python3 --version
```

#### 2.2 安装 Gurobi Optimizer

**2.2.1 下载 Gurobi**

访问 https://www.gurobi.com/downloads/ 下载对应操作系统的安装包

**2.2.2 安装 Gurobi**

运行下载的安装程序，按默认路径安装（通常为 `C:\gurobi<version>\` 或 `/opt/gurobi<version>/`）

**2.2.3 获取许可证**

**学术用户（免费）：**
1. 访问 https://www.gurobi.com/academia/academic-program-and-licenses/
2. 注册账号并申请学术许可证
3. 获取许可证密钥

**商业用户：**
- 需要购买商业许可证

**2.2.4 安装许可证**

**Windows:**
```powershell
# 方法1：使用命令行（需要先配置环境变量）
grbgetkey <你的许可证密钥>

# 方法2：手动创建许可证文件
# 将许可证内容保存为 C:\Users\<用户名>\gurobi.lic
```

**Linux/Mac:**
```bash
grbgetkey <你的许可证密钥>
# 或手动保存为 ~/gurobi.lic
```

**2.2.5 配置环境变量**

**Windows:**
1. 右键"此电脑" -> 属性 -> 高级系统设置
2. 环境变量 -> 系统变量 -> Path -> 编辑
3. 添加：`C:\gurobi<version>\win64\bin`（替换为实际版本号，如 `C:\gurobi1102\win64\bin`）

**Linux/Mac:**
编辑 `~/.bashrc` 或 `~/.zshrc`：
```bash
export GUROBI_HOME=/opt/gurobi<version>/linux64
export PATH=$PATH:$GUROBI_HOME/bin
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$GUROBI_HOME/lib
```

然后执行：
```bash
source ~/.bashrc
```

**验证 Gurobi 安装：**
```bash
grbgetkey --help
```

#### 2.3 安装 Python 依赖

进入 Python 服务目录：
```bash
cd personmis/python-service
```

安装依赖包：
```bash
# Windows
python -m pip install -r requirements.txt

# Linux/Mac
python3 -m pip install -r requirements.txt
```

如果遇到权限问题：
```bash
# Windows
python -m pip install --user -r requirements.txt

# Linux/Mac
sudo python3 -m pip install -r requirements.txt
```

**验证关键依赖：**
```bash
python -c "import flask; print('Flask: OK')"
python -c "import flask_cors; print('Flask-CORS: OK')"
python -c "import numpy; print('NumPy: OK')"
python -c "import pandas; print('Pandas: OK')"
python -c "import gurobipy; print('Gurobi: OK')"
```

如果所有依赖都显示"OK"，说明安装成功。

---

### 第三步：Java 后端配置

#### 3.1 安装 JDK 21

如果尚未安装 JDK 21：

**Windows:**
- 下载地址：https://www.oracle.com/java/technologies/downloads/#java21
- 或使用 OpenJDK: https://adoptium.net/

**Linux:**
```bash
sudo apt-get update
sudo apt-get install openjdk-21-jdk
```

**Mac:**
```bash
brew install openjdk@21
```

**验证安装：**
```bash
java -version
javac -version
```

#### 3.2  安装 Maven

项目自带 Maven Wrapper（`mvnw` 和 `mvnw.cmd`），无需单独安装 Maven。

如果需要全局安装：
- 下载地址：https://maven.apache.org/download.cgi
- 解压后配置 `MAVEN_HOME` 环境变量


### 第四步：Vue 前端配置

#### 4.1 安装 Node.js

如果尚未安装 Node.js：

- 下载地址：https://nodejs.org/
- 推荐安装 LTS 版本（16.x 或更高）

**验证安装：**
```bash
node --version
npm --version
```

#### 4.2 安装前端依赖

进入前端项目目录：
```bash
cd personmis-vue
```

安装依赖：
```bash
npm install
```


## 运行步骤

### 启动顺序

系统需要按以下顺序启动三个服务：

1. **MySQL 数据库**（必须首先启动）
2. **Python 算法服务**（端口 5000）
3. **Java 后端服务**（端口 8443）
4. **Vue 前端服务**（端口 8080，开发模式）

---

### 1. 启动 MySQL 数据库

确保 MySQL 服务正在运行（参考第一步的数据库配置）

---

### 2. 启动 Python 算法服务

打开新的终端窗口，进入 Python 服务目录：
```bash
cd personmis/python-service
```

启动服务：
```bash
python app.py
```

**验证服务：**
- 浏览器访问：http://localhost:5000/api/health
- 或使用 curl：`curl http://localhost:5000/api/health`

应该返回：`{"status": "ok"}` 或类似响应

**如果端口被占用，可以修改端口：**
```bash
# Windows PowerShell
$env:PORT=5001
python app.py

# Linux/Mac
export PORT=5001
python3 app.py
```

---

### 3. 启动 Java 后端服务

打开新的终端窗口，进入后端项目目录：
```bash
cd personmis
```
**使用 IDE 启动：**
- 在 IntelliJ IDEA 或 Eclipse 中打开项目
- 找到 `PersonmisApplication.java`
- 右键运行 `main` 方法

**验证服务：**
- 浏览器访问：http://localhost:8443/personmis/
- 应该能看到后端接口响应

---

### 4. 启动 Vue 前端服务

打开新的终端窗口，进入前端项目目录：
```bash
cd personmis-vue
```

启动开发服务器：
```bash
npm run serve
```

**访问应用：**
- 浏览器访问：http://localhost:8080
- 默认登录账号：`admin` / `admin`

---

## 完整启动示例（Windows PowerShell）

```powershell
# 终端1：启动 Python 服务
cd D:\SVN\homework\personmis\python-service
python app.py

# 终端2：启动 Java 后端
cd D:\SVN\homework\personmis
.\mvnw.cmd spring-boot:run

# 终端3：启动 Vue 前端
cd D:\SVN\homework\personmis-vue
npm run serve
```

---

## 完整启动示例（Linux/Mac）

```bash
# 终端1：启动 Python 服务
cd /path/to/personmis/python-service
python3 app.py

# 终端2：启动 Java 后端
cd /path/to/personmis
./mvnw spring-boot:run

# 终端3：启动 Vue 前端
cd /path/to/personmis-vue
npm run serve
```

---

## 项目结构

```
homework/
├── personmis/                    # Java 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/             # Java 源代码
│   │   │   └── resources/        # 配置文件
│   │   └── test/                 # 测试代码
│   ├── sql/                      # SQL 脚本
│   │   ├── personmis_new.sql     # 主数据库结构
│   │   ├── test_data_insert.sql  # 测试数据
│   │   └── workload_data.sql     # 工作量数据
│   ├── python-service/           # Python 算法服务
│   │   ├── app.py                # Flask 应用入口
│   │   ├── requirements.txt      # Python 依赖
│   │   └── GUROBI安装指南.md     # Gurobi 安装指南
│   ├── pom.xml                   # Maven 配置
│   └── mvnw                      # Maven Wrapper
│
└── personmis-vue/                # Vue 前端项目
    ├── src/
    │   ├── components/           # Vue 组件
    │   ├── views/                # 页面视图
    │   ├── router/               # 路由配置
    │   └── store/                # Vuex 状态管理
    ├── package.json              # Node.js 依赖
    └── vue.config.js             # Vue 配置
```

---

## 端口说明

| 服务 | 端口 | 说明 |
|------|------|------|
| MySQL | 3306 | 数据库服务 |
| Python Flask | 5000 | 算法服务（可配置） |
| Spring Boot | 8443 | 后端 API 服务 |
| Vue Dev Server | 8080 | 前端开发服务器 |

---
