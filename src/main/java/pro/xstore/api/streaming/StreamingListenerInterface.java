package pro.xstore.api.streaming;

import pro.xstore.api.message.records.SBalanceRecord;
import pro.xstore.api.message.records.SCandleRecord;
import pro.xstore.api.message.records.SKeepAliveRecord;
import pro.xstore.api.message.records.SNewsRecord;
import pro.xstore.api.message.records.SProfitRecord;
import pro.xstore.api.message.records.STickRecord;
import pro.xstore.api.message.records.STradeRecord;
import pro.xstore.api.message.records.STradeStatusRecord;

public interface StreamingListenerInterface {
    void receiveTradeRecord(STradeRecord tradeRecord);
    void receiveTickRecord(STickRecord tickRecord);
    void receiveBalanceRecord(SBalanceRecord balanceRecord);
    void receiveNewsRecord(SNewsRecord newsRecord);
    void receiveTradeStatusRecord(STradeStatusRecord tradeStatusRecord);
    void receiveProfitRecord(SProfitRecord profitRecord);
    void receiveKeepAliveRecord(SKeepAliveRecord keepAliveRecord);
    void receiveCandleRecord(SCandleRecord candleRecord);
}