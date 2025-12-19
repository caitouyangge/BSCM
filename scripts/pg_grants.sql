-- 给 bscm 用户授予在 public schema 建表/使用序列的权限（JPA 自动建表常用）
\connect bscm_db

GRANT USAGE, CREATE ON SCHEMA public TO bscm;

ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT ALL ON TABLES TO bscm;

ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT ALL ON SEQUENCES TO bscm;



