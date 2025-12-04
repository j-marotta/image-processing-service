package org.jakemarotta.imageprocessingservice.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class ImageResponse {

    private UUID imageId;
    private String url;
    private String type;
    private Long fileSize;

}
