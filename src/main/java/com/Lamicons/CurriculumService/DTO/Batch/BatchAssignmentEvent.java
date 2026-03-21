package com.Lamicons.CurriculumService.DTO.Batch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event published to Kafka when a batch-instructor assignment changes.
 *
 * eventType values:
 *   ASSIGNED       — new instructor set on a batch (create or update)
 *   UNASSIGNED     — instructor removed or replaced (old instructor gets this)
 *   BATCH_DELETED  — entire batch deleted; auth-ams removes all local rows for this batch
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BatchAssignmentEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("batchId")
    private UUID batchId;

    @JsonProperty("instructorUserId")
    private UUID instructorUserId;

    @JsonProperty("eventType")
    private BatchAssignmentEventType eventType;

    @JsonProperty("timestamp")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
