package com.proiectcolectiv.demo.dto.photo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhotoResponseDTO {
    private UUID id;
    private String url;
    private String publicId;
    private String uploaderName;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate uploadedAt;
}
