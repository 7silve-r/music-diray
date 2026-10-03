package com.silver.music.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class PlaylistUpdateDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @jakarta.validation.constraints.NotNull
    @jakarta.validation.constraints.Positive
    private Long playlistId;

    @jakarta.validation.constraints.NotBlank
    @jakarta.validation.constraints.Size(max = 100)
    private String title;

    private String introduction;

    private String style;

}
