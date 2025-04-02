package vwap.com.au.consumer;


import com.vwap.event.VwapDataUpdateEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import vwap.com.au.dto.DataPoint;
import vwap.com.au.mapper.EventMapper;
import vwap.com.au.service.VwapCalculator;

import java.util.function.Function;

@Service
public class VwapEventConsumer {

    private final VwapCalculator vwapCalculator;

    public VwapEventConsumer(VwapCalculator vwapCalculator) {
        this.vwapCalculator = vwapCalculator;
    }

    @KafkaListener(
            topics = "${spring.kafka.consumer.topics}",
            groupId = "${spring.kafka.consumer.group-id}",
            autoStartup = "true",
            concurrency = "${spring.kafka.consumer.concurrency}"
    )
    public void kafkaSubscriber(ConsumerRecord<String, VwapDataUpdateEvent> record, Acknowledgment ack) {
        doReceive(record);
        ack.acknowledge();
    }

    private void doReceive(ConsumerRecord<String, VwapDataUpdateEvent> record) {
        this.logEvent()
                .andThen(getEvent())
                .andThen(mapToDto())
                .andThen(vwapCalculator.addPriceUpdate())
                .apply(record);

    }

    private Function<ConsumerRecord<String, VwapDataUpdateEvent>,VwapDataUpdateEvent> getEvent() {
        return ConsumerRecord::value;
    }

    private Function<VwapDataUpdateEvent, DataPoint> mapToDto() {
        return EventMapper::mapToDto;
    }

    private Function<ConsumerRecord<String, VwapDataUpdateEvent>,
            ConsumerRecord<String, VwapDataUpdateEvent>> logEvent() {
        return event -> {
            System.out.println("Received Event ID: " + event.value().getId());
            return event;
        };
    }

}
