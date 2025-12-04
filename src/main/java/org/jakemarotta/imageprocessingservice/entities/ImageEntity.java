package org.jakemarotta.imageprocessingservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ImageEntity {

    @Id
    private long id;

    private String originalFilename;

    private String storagePath;

    private String contentType;

    private long size;

    private LocalDateTime createdAt;
}
