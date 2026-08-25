package com.marcomoretta.dungeondesk.domain.dto;

/**
 * Dto to expose the map image
 *
 * @param content     The actual raw bytes content of the image
 * @param contentType The file type
 */
public record MapImageDto(
        byte[] content,
        String contentType
) {
}
