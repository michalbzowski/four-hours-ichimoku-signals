create table actual_trends (
   id             uuid DEFAULT uuid_generate_v4(),
   symbol         varchar NOT NULL,
   trend          trend NOT NULL,
   duration       integer NOT NULL,
   audit_cd       timestamp default Now()
)