# SCM供应链管理系统 - 打包脚本
# 使用方法: .\build.ps1

param(
    [switch]$SkipClean,
    [switch]$SkipBackend
)

$ErrorActionPreference = "Stop"

$PROJECT_ROOT = $PSScriptRoot
$BACKEND_DIR = Join-Path $PROJECT_ROOT "backend"
$FRONTEND_DIR = Join-Path $PROJECT_ROOT "frontend"
$STATIC_DIR = Join-Path $BACKEND_DIR "src\main\resources\static"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "SCM供应链管理系统 - 打包脚本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. 清理后端target目录
if (-not $SkipClean) {
    Write-Host "[1/7] 清理后端target目录..." -ForegroundColor Yellow
    if (Test-Path "$BACKEND_DIR\target") {
        Remove-Item -Recurse -Force "$BACKEND_DIR\target"
        Write-Host "      清理完成" -ForegroundColor Green
    } else {
        Write-Host "      目录不存在，跳过" -ForegroundColor Gray
    }
    Write-Host ""

    # 2. 清理前端dist目录
    Write-Host "[2/7] 清理前端dist目录..." -ForegroundColor Yellow
    if (Test-Path "$FRONTEND_DIR\dist") {
        Remove-Item -Recurse -Force "$FRONTEND_DIR\dist"
        Write-Host "      清理完成" -ForegroundColor Green
    } else {
        Write-Host "      目录不存在，跳过" -ForegroundColor Gray
    }
    Write-Host ""
} else {
    Write-Host "[1/7] 跳过清理..." -ForegroundColor Gray
    Write-Host "[2/7] 跳过清理..." -ForegroundColor Gray
    Write-Host ""
}

# 3. 安装前端依赖
Write-Host "[3/7] 安装前端依赖..." -ForegroundColor Yellow
Push-Location $FRONTEND_DIR
try {
    npm install
    if ($LASTEXITCODE -ne 0) {
        throw "npm install 失败"
    }
    Write-Host "      安装完成" -ForegroundColor Green
} finally {
    Pop-Location
}
Write-Host ""

# 4. 构建前端
Write-Host "[4/7] 构建前端..." -ForegroundColor Yellow
Push-Location $FRONTEND_DIR
try {
    npm run build
    if ($LASTEXITCODE -ne 0) {
        throw "前端构建失败"
    }
    Write-Host "      构建完成" -ForegroundColor Green
} finally {
    Pop-Location
}
Write-Host ""

# 5. 复制前端dist到后端static目录
Write-Host "[5/7] 复制前端dist到后端static目录..." -ForegroundColor Yellow
if (Test-Path $STATIC_DIR) {
    Remove-Item -Recurse -Force $STATIC_DIR
}
New-Item -ItemType Directory -Path $STATIC_DIR -Force | Out-Null
Copy-Item -Path "$FRONTEND_DIR\dist\*" -Destination $STATIC_DIR -Recurse
Write-Host "      复制完成" -ForegroundColor Green
Write-Host ""

# 6. 构建后端jar包
if (-not $SkipBackend) {
    Write-Host "[6/7] 构建后端jar包..." -ForegroundColor Yellow
    Push-Location $BACKEND_DIR
    try {
        mvn clean package -DskipTests
        if ($LASTEXITCODE -ne 0) {
            throw "后端构建失败"
        }
        Write-Host "      构建完成" -ForegroundColor Green
    } finally {
        Pop-Location
    }
    Write-Host ""
} else {
    Write-Host "[6/7] 跳过后端构建..." -ForegroundColor Gray
    Write-Host ""
}

# 7. 打包完成
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "打包成功!" -ForegroundColor Green
Write-Host ""
Write-Host "后端jar位置: $BACKEND_DIR\target\scm-backend-1.0.0.jar" -ForegroundColor White
Write-Host ""
Write-Host "启动命令: java -jar `"$BACKEND_DIR\target\scm-backend-1.0.0.jar`"" -ForegroundColor White
Write-Host "访问地址: http://localhost:8180" -ForegroundColor White
Write-Host "登录账号: admin / 123456" -ForegroundColor White
Write-Host "========================================" -ForegroundColor Cyan
