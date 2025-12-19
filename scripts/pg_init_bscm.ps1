$ErrorActionPreference = "Stop"

$pgBin  = "D:\Program Files\PostgreSQL\17\bin"
$pgData = "D:\Program Files\PostgreSQL\17\data"
$pgCtl  = Join-Path $pgBin "pg_ctl.exe"
$psql   = Join-Path $pgBin "psql.exe"
$hba    = Join-Path $pgData "pg_hba.conf"

# 你可以改成自己想要的密码（随后也会写进 Spring 的环境变量默认值）
$BSCM_PASSWORD = "bscm123456"

function Assert-Exists([string]$path, [string]$name) {
  if (-not (Test-Path -LiteralPath $path)) {
    throw "$name 不存在：$path"
  }
}

Assert-Exists $pgCtl  "pg_ctl.exe"
Assert-Exists $psql   "psql.exe"
Assert-Exists $hba    "pg_hba.conf"

$backup = $hba + ".bak." + (Get-Date -Format "yyyyMMddHHmmss")

Write-Host "[1/5] 确保 PostgreSQL 已启动（非 Service 模式）..."
& $pgCtl status -D $pgData | Out-Null
if ($LASTEXITCODE -ne 0) {
  & $pgCtl start -D $pgData -w -l (Join-Path $pgData "pg_ctl.log") | Out-Null
}

Write-Host "[2/5] 备份并临时开启 trust（仅本机 postgres）..."
Copy-Item -LiteralPath $hba -Destination $backup -Force

$content = [System.IO.File]::ReadAllText($hba)
$needTrust = ($content -notmatch "host\s+all\s+postgres\s+127\.0\.0\.1/32\s+trust")

if ($needTrust) {
  $prefix = @"
# TEMP: for local setup (remove after initializing users)
local   all             postgres                                trust
host    all             postgres        127.0.0.1/32            trust
host    all             postgres        ::1/128                 trust

"@
  [System.IO.File]::WriteAllText($hba, $prefix + $content, [System.Text.UTF8Encoding]::new($false))
}

& $pgCtl reload -D $pgData | Out-Null

try {
  Write-Host "[3/5] 创建用户 bscm 与数据库 bscm_db（幂等）..."
  # 通过 GUC 把密码传给 SQL（避免在 SQL 文件里写死）
  & $psql -h 127.0.0.1 -U postgres -d postgres -v "ON_ERROR_STOP=1" `
    -c ("SET bscm.init_password = '" + $BSCM_PASSWORD.Replace("'", "''") + "';") `
    -f (Join-Path $PSScriptRoot "pg_init_bscm.sql") | Out-Null

  Write-Host "[4/5] 授权 schema 权限..."
  & $psql -h 127.0.0.1 -U postgres -d postgres -v "ON_ERROR_STOP=1" `
    -f (Join-Path $PSScriptRoot "pg_grants.sql") | Out-Null
}
finally {
  Write-Host "[5/5] 恢复 pg_hba.conf 并 reload..."
  Copy-Item -LiteralPath $backup -Destination $hba -Force
  & $pgCtl reload -D $pgData | Out-Null
}

Write-Host ""
Write-Host "完成：已创建用户 bscm / 数据库 bscm_db"
Write-Host ("bscm 密码（用于本地开发）：{0}" -f $BSCM_PASSWORD)
Write-Host ("pg_hba.conf 备份：{0}" -f $backup)



