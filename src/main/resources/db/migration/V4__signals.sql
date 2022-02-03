create table signals (
   symbol         varchar NOT NULL,
   name           varchar NOT NULL,
   trend          trend NOT NULL,
   should_enter   boolean NOT NULL,
   should_exit    boolean NOT NULL,
   signal_time    timestamp NOT NULL,
   audit_cd       timestamp default Now()
)