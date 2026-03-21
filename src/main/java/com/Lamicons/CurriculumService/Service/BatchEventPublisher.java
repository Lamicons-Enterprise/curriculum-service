package com.Lamicons.CurriculumService.Service;

import com.Lamicons.CurriculumService.DTO.Batch.BatchAssignmentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Publishes batch-assignment events to Kafka.
 * Uses batchId as the message key — events for the same batch land in the same
 * partition, guaranteeing order.
 * Wraps publish in try-catch — never throws, never breaks the main batch operation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BatchEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.batch-assignment}")
    private String batchAssignmentTopic;

    public void publishBatchAssignmentEvent(BatchAssignmentEvent event) {
        try {
            log.info("Publishing batch assignment event: type={}, batchId={}, instructorUserId={}",
                    event.getEventType(), event.getBatchId(), event.getInstructorUserId());

            String key = event.getBatchId().toString();
            kafkaTemplate.send(batchAssignmentTopic, key, event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish batch assignment event: type={}, batchId={}, error={}",
                                    event.getEventType(), event.getBatchId(), ex.getMessage(), ex);
                        } else {
                            log.info("Batch assignment event published: type={}, offset={}",
                                    event.getEventType(), result.getRecordMetadata().offset());
                        }
                    });
        } catch (Exception e) {
            log.error("Failed to publish batch assignment event: type={}, batchId={}, error={}",
                    event.getEventType(), event.getBatchId(), e.getMessage(), e);
            // Never throw — publishing failure must not break the main batch operation
        }
    }
}
