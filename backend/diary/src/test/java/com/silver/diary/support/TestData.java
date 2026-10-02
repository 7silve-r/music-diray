package com.silver.diary.support;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.silver.diary.entity.User;
import com.silver.diary.service.UserService;
import org.springframework.mock.web.MockMultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import static org.mockito.Mockito.*;

public final class TestData {
    private TestData() {}
    @SuppressWarnings("unchecked")
    public static LambdaQueryChainWrapper<User> users(UserService service) {
        LambdaQueryChainWrapper<User> query = mock(LambdaQueryChainWrapper.class);
        doReturn(query).when(query).eq(any(), any());
        when(service.lambdaQuery()).thenReturn(query);
        return query;
    }
    public static User user() {
        User user = new User(); user.setId(1); user.setUsername("writer01");
        return user;
    }
    public static MockMultipartFile png() throws Exception {
        var output = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), "png", output);
        return new MockMultipartFile("file", "../../fake.html", "text/html", output.toByteArray());
    }
}
