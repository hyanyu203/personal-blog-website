# 将 docker/.env.mail 中的 SMTP 变量合并进 docker/.env.prod（两者均在 gitignore）
$ErrorActionPreference = "Stop"
$DockerDir = Split-Path $PSScriptRoot -Parent
$MailEnv = Join-Path $DockerDir ".env.mail"
$ProdEnv = Join-Path $DockerDir ".env.prod"

if (-not (Test-Path $MailEnv)) {
    Write-Error "缺少 $MailEnv"
}

if (-not (Test-Path $ProdEnv)) {
    Copy-Item (Join-Path $DockerDir ".env.prod.example") $ProdEnv
    Write-Host "已从 .env.prod.example 创建 .env.prod，请补全 JWT_SECRET 等必填项"
}

$mailLines = Get-Content $MailEnv | Where-Object {
    $_ -match '^\s*(MAIL_|REGISTRATION_ENABLED=)'
}

$prodContent = Get-Content $ProdEnv -Raw
foreach ($line in $mailLines) {
    $key = ($line -split '=', 2)[0].Trim()
    if ($prodContent -match "(?m)^$key=.*") {
        $prodContent = $prodContent -replace "(?m)^$key=.*", $line.TrimEnd()
    } else {
        $prodContent = $prodContent.TrimEnd() + "`n" + $line
    }
}

Set-Content -Path $ProdEnv -Value $prodContent.TrimEnd() -NoNewline
Write-Host "已合并 SMTP 配置到 $ProdEnv"
Write-Host "启动: docker compose -f docker-compose.prod.yml --env-file .env.prod up -d --build"
