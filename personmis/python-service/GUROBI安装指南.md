# Gurobi安装指南

## 快速安装步骤

### 1. 下载Gurobi Optimizer

访问 https://www.gurobi.com/downloads/ 下载对应操作系统的安装包

### 2. 安装Gurobi Optimizer

运行下载的安装程序，按默认路径安装即可

### 3. 获取许可证

**学术用户（免费）：**
1. 访问 https://www.gurobi.com/academia/academic-program-and-licenses/
2. 注册账号并申请学术许可证
3. 获取许可证密钥

**安装许可证：**
```bash
grbgetkey <你的许可证密钥>
```

或手动将许可证文件保存为 `gurobi.lic`，放到：
- Windows: `C:\Users\<用户名>\gurobi.lic`
- Linux/Mac: `~/gurobi.lic`

### 4. 配置环境变量（Windows）

**方法1：系统环境变量**
1. 右键"此电脑" -> 属性 -> 高级系统设置
2. 环境变量 -> 系统变量 -> Path -> 编辑
3. 添加：`C:\gurobi<version>\win64\bin`（替换为实际版本号）

**方法2：PowerShell临时设置**
```powershell
$env:PATH += ";C:\gurobi1102\win64\bin"
```

### 5. 安装Python包

```bash
cd python-service
venv\Scripts\activate
pip install gurobipy
```

### 6. 验证安装

```bash
python -c "import gurobipy as gp; print('Gurobi版本:', gp.gurobi.version())"
```

如果显示版本号，说明安装成功。

## 常见问题

**Q: 提示找不到grbgetkey命令？**
A: 需要先配置环境变量，将Gurobi的bin目录添加到PATH

**Q: 提示许可证无效？**
A: 检查许可证文件位置和内容是否正确，确保许可证未过期

**Q: Python导入gurobipy失败？**
A: 确保Gurobi Optimizer已安装，环境变量已配置，然后重新安装gurobipy

**Q: 学术许可证申请需要什么？**
A: 需要学校邮箱和学校域名，验证通过后即可获得免费许可证

