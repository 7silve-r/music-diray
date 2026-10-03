package com.silver.music.controller;

import com.silver.diary.common.Result;
import com.silver.music.service.PlaylistBindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
public class PlaylistBindingController {
    @Autowired private PlaylistBindingService playlistBindingService;

    @PutMapping("/music/admin/playlists/{id}/songs")
    public Result<Void> update(@PathVariable Long id, @RequestBody List<Long> songIds) {
        playlistBindingService.update(id, songIds); return Result.success();
    }
}
