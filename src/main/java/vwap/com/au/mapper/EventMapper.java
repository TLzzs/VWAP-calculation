package vwap.com.au.mapper;

import com.vwap.event.VwapDataUpdateEvent;
import vwap.com.au.dto.DataPoint;

import java.time.LocalDateTime;

public class EventMapper {
    public static DataPoint mapToDto(VwapDataUpdateEvent vwapDataUpdateEvent) {

        return DataPoint.builder()
                .currencyPair(vwapDataUpdateEvent.getCurrencyPair().toString())
                .price(vwapDataUpdateEvent.getPrice())
                .volume(vwapDataUpdateEvent.getVolume())
                .timestamp(LocalDateTime.parse(vwapDataUpdateEvent.getTimestamp()))
                .build();
    }
}
