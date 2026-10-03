package com.silver.music.upload;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import com.silver.diary.exception.BusinessException;
import static org.junit.jupiter.api.Assertions.*;
class UploadValidatorTest {
    @Test void case1() {
        var error=assertThrows(BusinessException.class, () -> UploadValidator.validate(
            new MockMultipartFile("file","image.png","image/png","<script>bad</script>".getBytes()),false));
        assertEquals(415,error.getCode());
    }
    @Test void case2() throws Exception {
        var bytes=new java.io.ByteArrayOutputStream();
        javax.imageio.ImageIO.write(new java.awt.image.BufferedImage(2,2,1),"png",bytes);
        var result=UploadValidator.validate(new MockMultipartFile("file","../../script.exe","text/plain",bytes.toByteArray()),false);
        assertEquals(".png",result.extension()); assertEquals("image/png",result.type());
    }
    @Test void case3() {
        assertEquals(400,assertThrows(BusinessException.class, () -> UploadValidator.validate(new MockMultipartFile("file",new byte[0]),false)).getCode());
        assertEquals(413,assertThrows(BusinessException.class, () -> UploadValidator.validate(new MockMultipartFile("file",new byte[5*1024*1024+1]),false)).getCode());
    }
    @Test void case4() throws Exception {
        var bytes=new java.io.ByteArrayOutputStream();
        javax.imageio.ImageIO.write(new java.awt.image.BufferedImage(4097,1,1),"png",bytes);
        assertEquals(400,assertThrows(BusinessException.class, () -> UploadValidator.validate(new MockMultipartFile("file",bytes.toByteArray()),false)).getCode());
    }
    @Test void rejectsFakeAudio() {
        assertEquals(415,assertThrows(BusinessException.class, () -> UploadValidator.validate(new MockMultipartFile("file","song.mp3","audio/mpeg","not audio".getBytes()),true)).getCode());
    }
}
