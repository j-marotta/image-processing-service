package org.jakemarotta.imageprocessingservice.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JWTResponse {
    private String username;
    private String atToken;
}
