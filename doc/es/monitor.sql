
delete from usuarios where id=3;
delete from usuarios where id=4;
delete from usuarios where id=5;
delete from usuarios where id=6;

delete from usuarios where id=7;
delete from usuarios where id=8;
delete from usuarios where id=9;
delete from usuarios where id=10;

delete from usuarios where id=11;
delete from usuarios where id=12;
delete from usuarios where id=13;
delete from usuarios where id=14;
/*
insert into usuarios values (3,'carlos','carl','pp@p.com',TRUE);
insert into usuarios values (4,'carlos','carl','pp@p.com',TRUE);
insert into usuarios values (5,'carlos','carl','pp@p.com',TRUE);
insert into usuarios values (6,'carlos','carl','pp@p.com',TRUE);

insert into usuarios values (7,'carlos','carl','pp@p.com',TRUE);
insert into usuarios values (8,'carlos','carl','pp@p.com',TRUE);
insert into usuarios values (9,'carlos','carl','pp@p.com',TRUE);
insert into usuarios values (10,'carlos','carl','pp@p.com',TRUE);

insert into usuarios values (11,'carlos','carl','pp@p.com',TRUE);
insert into usuarios values (12,'carlos','carl','pp@p.com',TRUE);
insert into usuarios values (13,'carlos','carl','pp@p.com',TRUE);
insert into usuarios values (14,'carlos','carl','pp@p.com',TRUE);
*/
-- select * from pg_stat_all_tables order by relid;

--select sum(n_tup_ins) as Tuplas_Inserted, sum(n_tup_upd) as Tuplas_Updated, sum(n_tup_del) as Tuplas_Deleted from pg_stat_all_tables;

--select * from pg_stat_user_tables where relname = 'usuarios';
-- select * from pg_locks a, pg_class b where a.relation=b.relfilenode;

--select * from pg_class;


--select * from usuarios;
/*
select 
	pg_stat_get_db_blocks_fetched(s.oid) as bloques_traidos_desde_disco, 
	pg_stat_get_db_blocks_hit(s.oid) as bloques_traidos_desde_cache
from 
	(SELECT datid AS oid from pg_stat_database where datname='pisg01') AS s;

*/
--select * from pg_stat_database a, pg_stat_activity b where a.datid=b.datid;

/*select * from pg_stat_database;
select * from pg_stat_activity;*/

/*
SELECT 
	pg_stat_get_db_blocks_fetched(S.BD) AS bloqueos_fetch, 
	pg_stat_get_db_blocks_hit(S.BD) AS bloqueos_hit
FROM 
	(SELECT datid as BD from pg_stat_activity 
WHERE 
	datname = 'pisg03') AS S;
*/