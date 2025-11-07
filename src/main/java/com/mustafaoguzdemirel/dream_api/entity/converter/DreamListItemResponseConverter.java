package com.mustafaoguzdemirel.dream_api.entity.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mustafaoguzdemirel.dream_api.dto.response.DreamListItemResponse;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;

@Converter
public class DreamListItemResponseConverter implements AttributeConverter<List<DreamListItemResponse>, String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<DreamListItemResponse> dreamList) {
        try {
            if (dreamList == null || dreamList.isEmpty()) {
                return "[]";
            }
            return mapper.writeValueAsString(dreamList);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert DreamListItemResponse list to JSON", e);
        }
    }

    @Override
    public List<DreamListItemResponse> convertToEntityAttribute(String json) {
        try {
            if (json == null || json.isBlank()) {
                return List.of();
            }
            return mapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON to DreamListItemResponse list", e);
        }
    }

}
