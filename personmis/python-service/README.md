# Python 算法服务 - 安装运行指南

## 安装运行顺序

### 第一步：检查Python环境

确保系统已安装Python 3.8或更高版本：

```bash
python --version
```

**Windows系统：**
```powershell
python --version
```

**Linux/Mac系统：**
```bash
python3 --version
```

如果显示版本号低于3.8，请先升级Python。

---

### 第二步：进入项目目录

```bash
cd personmis/python-service
```

**Windows PowerShell：**
```powershell
cd D:\SVN\homework\personmis\python-service
```

**Linux/Mac：**
```bash
cd /path/to/personmis/python-service
```

---

### 第三步：安装依赖包

**直接使用系统Python环境安装（不使用虚拟环境）：**

```bash
pip install -r requirements.txt
```

**如果pip命令不可用，尝试：**

**Windows：**
```powershell
python -m pip install -r requirements.txt
```

**Linux/Mac：**
```bash
python3 -m pip install -r requirements.txt
```

**如果遇到权限问题，Windows系统可以尝试：**
```powershell
python -m pip install --user -r requirements.txt
```

---

### 第四步：验证依赖安装

检查关键依赖是否安装成功：

```bash
python -c "import flask; print('Flask: OK')"
python -c "import flask_cors; print('Flask-CORS: OK')"
python -c "import numpy; print('NumPy: OK')"
python -c "import pandas; print('Pandas: OK')"
python -c "import gurobipy; print('Gurobi: OK')"
```

如果所有依赖都显示"OK"，说明安装成功。

---

### 第五步：启动服务

**直接运行：**

```bash
python app.py
```

**Windows PowerShell：**
```powershell
python app.py
```

**Linux/Mac：**
```bash
python3 app.py
```

**如果5000端口被占用，可以修改端口：**

**Windows PowerShell：**
```powershell
$env:PORT=5001
python app.py
```

**Linux/Mac：**
```bash
export PORT=5001
python3 app.py
```

---

### 第六步：验证服务运行

服务启动后，会看到类似输出：

```
 * Running on http://127.0.0.1:5000
 * Press CTRL+C to quit
```

**在浏览器中访问：**
```
http://localhost:5000/api/health
```

**或使用curl命令：**
```bash
curl http://localhost:5000/api/health
```

如果返回 `{"status": "ok"}` 或类似响应，说明服务运行正常。

---

## 完整安装运行示例（Windows）

```powershell
# 1. 检查Python版本
python --version

# 2. 进入项目目录
cd D:\SVN\homework\personmis\python-service

# 3. 安装依赖
python -m pip install -r requirements.txt

# 4. 验证安装
python -c "import flask; print('Flask: OK')"

# 5. 启动服务
python app.py
```

---

## 完整安装运行示例（Linux/Mac）

```bash
# 1. 检查Python版本
python3 --version

# 2. 进入项目目录
cd /path/to/personmis/python-service

# 3. 安装依赖
python3 -m pip install -r requirements.txt

# 4. 验证安装
python3 -c "import flask; print('Flask: OK')"

# 5. 启动服务
python3 app.py
```

---

## 项目结构

```
python-service/
├── requirements.txt      # 依赖列表
├── README.md            # 说明文档
├── app.py               # Flask应用入口
└── ...                  # 其他文件
```

---

## 依赖包说明

主要依赖包：

- **Flask** (>=2.3.0)：Web框架
- **Flask-CORS** (>=4.0.0)：跨域支持
- **gurobipy** (>=11.0.0)：Gurobi优化求解器
- **numpy** (>=1.26.0)：数值计算
- **pandas** (>=2.1.0)：数据处理
- **scipy** (>=1.11.0)：科学计算
- **scikit-learn** (>=1.3.0)：机器学习
- **python-dotenv** (>=1.0.0)：环境变量管理

---

## 常见问题

### 1. pip命令不存在

**解决方案：**
```bash
# Windows
python -m pip install -r requirements.txt

# Linux/Mac
python3 -m pip install -r requirements.txt
```

### 2. 权限错误（Permission denied）

**Windows解决方案：**
```powershell
python -m pip install --user -r requirements.txt
```

**Linux/Mac解决方案：**
```bash
sudo python3 -m pip install -r requirements.txt
```

### 3. 端口被占用

**解决方案：**
修改端口号（如改为5001）：
```bash
# Windows PowerShell
$env:PORT=5001
python app.py

# Linux/Mac
export PORT=5001
python3 app.py
```

### 4. Gurobi安装问题

如果Gurobi安装失败，可以：
1. 先安装其他依赖：`pip install Flask Flask-CORS numpy pandas scipy scikit-learn python-dotenv`
2. Gurobi需要许可证，如果只是测试可以暂时注释掉相关代码

### 5. 依赖冲突

**解决方案：**
```bash
# 升级pip
python -m pip install --upgrade pip

# 重新安装依赖
pip install -r requirements.txt --force-reinstall
```

---

## 停止服务

在运行服务的终端窗口中，按 `Ctrl+C` 停止服务。

---

## 注意事项

1. **Python版本**：必须使用Python 3.8或更高版本
2. **端口占用**：默认使用5000端口，确保端口未被占用
3. **依赖安装**：首次安装可能需要几分钟时间，请耐心等待
4. **系统环境**：直接使用系统Python环境，不创建虚拟环境
5. **防火墙**：如果无法访问服务，请检查防火墙设置

---

## 快速启动检查清单

- [ ] Python 3.8+ 已安装
- [ ] 已进入 `personmis/python-service` 目录
- [ ] 已执行 `pip install -r requirements.txt`
- [ ] 依赖验证通过（所有包显示"OK"）
- [ ] 已执行 `python app.py` 启动服务
- [ ] 浏览器访问 `http://localhost:5000/api/health` 返回正常

完成以上步骤后，Python服务即可正常使用！
