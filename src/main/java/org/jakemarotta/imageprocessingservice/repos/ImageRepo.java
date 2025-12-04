package org.jakemarotta.imageprocessingservice.repos;

import org.jakemarotta.imageprocessingservice.entities.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageRepo extends JpaRepository<ImageEntity, UUID> {
}
