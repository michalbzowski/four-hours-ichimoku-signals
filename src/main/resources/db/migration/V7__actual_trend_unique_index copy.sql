
CREATE UNIQUE INDEX only_one_trend_per_symbol_and_duration ON actual_trends (symbol, duration);
ALTER TABLE actual_trends 
ADD CONSTRAINT sym_dur_u_constraint 
UNIQUE USING INDEX only_one_trend_per_symbol_and_duration;