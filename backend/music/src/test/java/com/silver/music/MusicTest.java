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
}
