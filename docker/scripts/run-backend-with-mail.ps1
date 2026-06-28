# 加载 docker/.env.mail 并启动单体后端（dev + 网易 SMTP）
$ErrorActionPreference = "Stop"
$Root = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
$MailEnv = Join-Path $Root "docker\.env.mail"

if (-not (Test-Path $MailEnv)) {
    Write-Error "缺少 $MailEnv，请复制 docker\.env.mail.example 为 .env.mail 并填写邮箱与授权码"
}

Get-Content $MailEnv | ForEach-Object {
    $line = $_.Trim()
    if ($line -eq "" -or $line.StartsWith("#")) { return }
    if ($line -match '^([^=]+)=(.*)$') {
        $name = $matches[1].Trim()
        $value = $matches[2].Trim()
        Set-Item -Path "Env:$name" -Value $value
    }
}

if ($env:MAIL_USER -match "请替换") {
    Write-Error "请在 docker\.env.mail 中把 MAIL_USER 改成你的 163 邮箱地址"
}

Write-Host "MAIL_HOST=$env:MAIL_HOST MAIL_USER=$env:MAIL_USER REGISTRATION=$env:REGISTRATION_ENABLED"

Push-Location (Join-Path $Root "backend\monolith")
try {
    if (-not (Test-Path "target\jiangou-1.0.0-SNAPSHOT.jar")) {
        Write-Host "Building backend..."
        .\mvnw.cmd -DskipTests package -q
    }
    java -jar target\jiangou-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev
} finally {
    Pop-Location
}
