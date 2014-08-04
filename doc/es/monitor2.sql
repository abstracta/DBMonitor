--select * from pg_locks;
/*
vacuum;

SELECT *--relfilenode, relpages
FROM pg_class order by relname;
--WHERE relname = 'usuarios';

SELECT a.name, a.setting, a.category, a.short_desc, a.extra_desc, a.context, a.vartype, a.source, a.min_val, a.max_val
FROM pg_show_all_settings() a(name text, setting text, category text, short_desc text, extra_desc text, context text, 
vartype text, source text, min_val text, max_val text);
*/

SELECT d.oid AS datid, d.datname, pg_stat_get_backend_pid(s.backendid) AS procpid,
pg_stat_get_backend_userid(s.backendid) AS usesysid, u.usename, pg_stat_get_backend_activity(s.backendid) AS
current_query, pg_stat_get_backend_activity_start(s.backendid) AS query_start 

FROM pg_database d, (SELECT pg_stat_get_backend_idset() AS backendid) s, pg_shadow u 

WHERE ((pg_stat_get_backend_dbid(s.backendid) = d.oid) AND (pg_stat_get_backend_userid(s.backendid) = u.usesysid));
