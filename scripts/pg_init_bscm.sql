-- 初始化 BSCM 数据库与用户（幂等：重复执行不会报错）
-- 说明：
-- - 该脚本需要以具有足够权限的用户执行（通常是 postgres）
-- - 密码在 pg_init_bscm.ps1 中通过变量替换传入

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'bscm') THEN
    EXECUTE format('CREATE ROLE bscm LOGIN PASSWORD %L', current_setting('bscm.init_password'));
  END IF;
END $$;

-- 创建数据库（若不存在）
SELECT format('CREATE DATABASE bscm_db OWNER bscm')
WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'bscm_db')
\gexec



