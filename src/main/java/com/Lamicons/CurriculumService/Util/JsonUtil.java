package com.Lamicons.CurriculumService.Util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Utility class to handle JSON operations, particularly for PostgreSQL JSONB fields.
 */
@Slf4j
@Component
public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Creates an empty JSON array.
     * 
     * @return Empty JSON array as JsonNode
     */
    public static JsonNode createEmptyJsonArray() {
        return objectMapper.createArrayNode();
    }
    
    /**
     * Creates an empty JSON object.
     * 
     * @return Empty JSON object as JsonNode
     */
    public static JsonNode createEmptyJsonObject() {
        return objectMapper.createObjectNode();
    }
    
    /**
     * Safely converts a value to a JSON node.
     * 
     * @param value Value to convert (String, Map, List, etc.)
     * @return JsonNode or empty JSON array if parsing fails
     */
    public static JsonNode toJsonNode(Object value) {
        try {
            if (value == null) {
                return createEmptyJsonArray();
            }
            
            if (value instanceof JsonNode) {
                return (JsonNode) value;
            }
            
            if (value instanceof String) {
                String strValue = (String) value;
                if (strValue.isEmpty()) {
                    return createEmptyJsonArray();
                }
                return objectMapper.readTree(strValue);
            }
            
            // Convert any other object to JsonNode
            return objectMapper.valueToTree(value);
        } catch (Exception e) {
            log.error("Error converting to JsonNode: {}", e.getMessage());
            return createEmptyJsonArray(); // Return empty array as fallback
        }
    }
}