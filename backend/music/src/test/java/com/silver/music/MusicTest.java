package com.silver.music;

import com.silver.diary.utils.JwtUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MusicTest {
    @Autowired WebApplicationContext context;
    @Autowired JdbcTemplate jdbc;
    @Autowired JwtUtil jwt;
    MockMvc mvc;

    @BeforeEach void setup() {
        jdbc.update("INSERT INTO `user` (id, username, role) VALUES (1, 'writer01', 'USER'), (2, 'ADMIN', 'ADMIN')");
        jdbc.update("INSERT INTO tb_artist (id, name) VALUES (1, '歌手')");
        jdbc.update("INSERT INTO tb_song (id, name, artist_id, album, release_time) VALUES (1, '歌曲', 1, '专辑', CURRENT_DATE)");
        jdbc.update("INSERT INTO tb_playlist (id, title) VALUES (1, '歌单')");
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test void guestSong() throws Exception {
        mvc.perform(get("/music/public/song/getSongDetail/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.songName").value("歌曲"));
    }
    @Test void sharedUser() throws Exception {
        String token = jwt.generateToken("writer01");
        mvc.perform(get("/my/userinfo").header("Authorization", token)).andExpect(status().isOk());
        mvc.perform(post("/music/favorite/collectSong").param("songId", "1").header("Authorization", token))
                .andExpect(status().isOk());
    }
    @Test void guestWrite() throws Exception {
        mvc.perform(post("/music/favorite/collectSong").param("songId", "1")).andExpect(status().isUnauthorized());
    }
    @Test void noAdmin() throws Exception {
        mvc.perform(delete("/music/admin/deleteSong/1").header("Authorization", jwt.generateToken("writer01")))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value(403));
    }
    @Test void adminUser() throws Exception {
        mvc.perform(post("/music/favorite/collectSong").param("songId", "1").header("Authorization", jwt.generateToken("ADMIN")))
                .andExpect(status().isOk());
    }
    @Test void adminDelete() throws Exception {
        mvc.perform(delete("/music/admin/deleteSong/1").header("Authorization", jwt.generateToken("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test void repeatFavorite() throws Exception {
        for (int i = 0; i < 2; i++) mvc.perform(post("/music/favorite/collectSong").param("songId", "1")
                .header("Authorization", jwt.generateToken("writer01"))).andExpect(status().isOk());
        org.junit.jupiter.api.Assertions.assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM tb_user_favorite", Integer.class));
    }
    @Test void commentLikes() throws Exception {
        jdbc.update("INSERT INTO tb_comment (id, user_id, song_id, content, create_time, type, like_count) VALUES (10, 1, 1, 'hi', CURRENT_TIMESTAMP, 0, 0)");
        for (int i = 0; i < 2; i++) mvc.perform(patch("/music/comment/likeComment/10")
                .header("Authorization", jwt.generateToken("ADMIN"))).andExpect(status().isOk());
        org.junit.jupiter.api.Assertions.assertEquals(1, jdbc.queryForObject("SELECT like_count FROM tb_comment WHERE id = 10", Integer.class));
        for (int i = 0; i < 2; i++) mvc.perform(patch("/music/comment/cancelLikeComment/10")
                .header("Authorization", jwt.generateToken("ADMIN"))).andExpect(status().isOk());
        org.junit.jupiter.api.Assertions.assertEquals(0, jdbc.queryForObject("SELECT like_count FROM tb_comment WHERE id = 10", Integer.class));
    }
    @Test void foreignComment() throws Exception {
        jdbc.update("INSERT INTO tb_comment (id, user_id, song_id, content, create_time, type, like_count) VALUES (10, 2, 1, 'hi', CURRENT_TIMESTAMP, 0, 0)");
        mvc.perform(delete("/music/comment/deleteComment/10").header("Authorization", jwt.generateToken("writer01")))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/music/comment/deleteComment/10").header("Authorization", jwt.generateToken("ADMIN")))
                .andExpect(status().isOk());
    }
    @Test void missing() throws Exception {
        mvc.perform(get("/music/public/song/getSongDetail/999")).andExpect(status().isNotFound());
        mvc.perform(get("/music/public/playlist/getPlaylistDetail/999")).andExpect(status().isNotFound());
        mvc.perform(post("/music/favorite/collectSong").param("songId", "999")
                .header("Authorization", jwt.generateToken("writer01"))).andExpect(status().isNotFound());
    }
    @Test void badComment() throws Exception {
        mvc.perform(post("/music/comment/addSongComment").header("Authorization", jwt.generateToken("writer01"))
                .contentType("application/json").content("{\"songId\":1,\"content\":\" \"}"))
                .andExpect(status().isBadRequest());
    }
    @Test void favoritePage() throws Exception {
        jdbc.update("INSERT INTO tb_user_favorite (user_id, song_id, type, create_time) VALUES (1,1,0,CURRENT_TIMESTAMP), (2,1,0,CURRENT_TIMESTAMP)");
        mvc.perform(post("/music/favorite/getFavoriteSongs").header("Authorization", jwt.generateToken("writer01"))
                .contentType("application/json").content("{\"pageNum\":1,\"pageSize\":10}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.total").value(1));
    }
}
