
CREATE UNIQUE INDEX only_one_finished_candle ON candles (symbol, duration, begining) WHERE finished IS TRUE;