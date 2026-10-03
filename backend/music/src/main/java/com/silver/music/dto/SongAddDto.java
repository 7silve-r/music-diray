package com.silver.music.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class SongAddDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @jakarta.validation.constraints.NotNull
    @jakarta.validation.constraints.Positive
    private Long artistId;

    @jakarta.validation.constraints.NotBlank
    @jakarta.validation.constraints.Size(max = 100)
    private String songName;

    @jakarta.validation.constraints.NotBlank
    @jakarta.validation.constraints.Size(max = 100)
    private String album;

    private String style;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @jakarta.validation.constraints.NotNull
    private LocalDate releaseTime;

}
