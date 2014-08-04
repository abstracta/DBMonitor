--select * from pg_stat_database;
--select * from pg_stat_user_tables;

select sum(blks_read) as Bloques_Leidos_de_Disco, sum(blks_hit) as Bloques_Leidos_de_Memoria from pg_stat_database;
select sum(seq_scan) as Busquedas_Secuenciales, sum(idx_scan) as Busquedas_por_Indice from pg_stat_user_tables;
select sum(seq_tup_read) as Tuplas_Leidas_Secuencialmente, sum(idx_tup_fetch) as Tuplas_Leidas_Por_Indice from pg_stat_user_tables;

