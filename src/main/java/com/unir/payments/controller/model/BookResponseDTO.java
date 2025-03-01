package com.unir.payments.controller.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookResponseDTO {
    private Long bookId;
    private String title;
    private String author;

    @JsonProperty("publication_Date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private LocalDateTime publicationDate;

    private String category;
    private String isbn;
    private Integer rating;
    private Boolean visible;
}
