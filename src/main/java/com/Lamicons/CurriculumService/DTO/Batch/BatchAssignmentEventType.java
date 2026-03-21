package com.Lamicons.CurriculumService.DTO.Batch;

/**
 * Type-safe enum for batch-instructor assignment events.
 * Jackson handles serialization/deserialization automatically.
 */
public enum BatchAssignmentEventType {
    ASSIGNED,
    UNASSIGNED,
    BATCH_DELETED
}
