@echo off
chcp 65001 >nul
echo ========================================
echo SCM供应链管理系统 - 打包脚本
echo ========================================
echo.

set PROJECT_ROOT=%~dp0
set BACKEND_DIR=%PROJECT_ROOT%backend
set FRONTEND_DIR=%PROJECT_ROOT%frontend
set STATIC_DIR=%BACKEND_DIR%\src\main\resources\static

echo [1/7] 清理后端target目录...
if exist "%BACKEND_DIR%\target" (
    rmdir /s /q "%BACKEND_DIR%\target"
    echo       清理完成
) else (
    echo       目录不存在，跳过
)
echo.

echo [2/7] 清理前端dist目录...
if exist "%FRONTEND_DIR%\dist" (
    rmdir /s /q "%FRONTEND_DIR%\dist"
    echo       清理完成
) else (
    echo       目录不存在，跳过
)
echo.

echo [3/7] 安装前端依赖...
cd /d "%FRONTEND_DIR%"
call npm install
if %errorlevel% neq 0 (
    echo       [错误] npm install 失败
    pause
    exit /b 1
)
echo       安装完成
echo.

echo [4/7] 构建前端...
call npm run build
if %errorlevel% neq 0 (
    echo       [错误] 前端构建失败
    pause
    exit /b 1
)
echo       构建完成
echo.

echo [5/7] 复制前端dist到后端static目录...
if exist "%STATIC_DIR%" (
    rmdir /s /q "%STATIC_DIR%"
)
xcopy /e /i /y "%FRONTEND_DIR%\dist" "%STATIC_DIR%\"
echo       复制完成
echo.

echo [6/7] 构建后端jar包...
cd /d "%BACKEND_DIR%"
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo       [错误] 后端构建失败
    pause
    exit /b 1
)
echo       构建完成
echo.

echo [7/7] 打包完成！
echo.
echo ========================================
echo 打包成功！
echo.
echo 后端jar位置: %BACKEND_DIR%\target\scm-backend-1.0.0.jar
echo.
echo 启动命令: java -jar "%BACKEND_DIR%\target\scm-backend-1.0.0.jar"
echo 访问地址: http://localhost:8080
echo ========================================
echo.
pause
