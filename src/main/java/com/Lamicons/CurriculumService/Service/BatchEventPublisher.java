package com.Lamicons.CurriculumService.Service;

import com.Lamicons.CurriculumService.DTO.Batch.BatchAssignmentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Publishes batch-assignment events to RabbitMQ.
 * Wraps publish in try-catch — never throws, never breaks the main batch operation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BatchEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.routing-key.batch-assignment}")
    private String batchAssignmentRoutingKey;

    public void publishBatchAssignmentEvent(BatchAssignmentEvent event) {
        try {
            log.info("Publishing batch assignment event: type={}, batchId={}, instructorUserId={}",
                    event.getEventType(), event.getBatchId(), event.getInstructorUserId());
            rabbitTemplate.convertAndSend(batchAssignmentRoutingKey, event);
            log.info("Batch assignment event published successfully");
        } catch (Exception e) {
            log.error("Failed to publish batch assignment event: type={}, batchId={}, instructorUserId={}, error={}",
                    event.getEventType(), event.getBatchId(), event.getInstructorUserId(), e.getMessage(), e);
            // Never throw — publishing failure must not break the main batch operation
        }
    }
}
