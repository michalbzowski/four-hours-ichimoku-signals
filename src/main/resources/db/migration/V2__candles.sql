create table candles (
   id           uuid DEFAULT uuid_generate_v4(),
   reference_id uuid NOT NULL,
   symbol       varchar NOT NULL,
   duration     integer NOT NULL, --minutes
   begining timestamp,
   price_open   numeric(16,5) NOT NULL,
   price_close  numeric(16,5) NOT NULL,
   price_high   numeric(16,5) NOT NULL,
   price_low    numeric(16,5) NOT NULL,
   finished     boolean NOT NULL default FALSE,
   audit_cd     timestamp default Now(),
   PRIMARY KEY (id),
   CONSTRAINT fk_candle_parent
      FOREIGN KEY(reference_id) 
	    REFERENCES candles(id)
)