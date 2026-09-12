package com.sxwl.auth.crypto;

import com.sxwl.security.key.SxwlSM2KeyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link SxwlPasswordDecryptor} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SxwlPasswordDecryptor 测试")
class SxwlPasswordDecryptorTest {

    @Mock
    private SxwlSM2KeyManager keyManager;

    private SxwlPasswordDecryptor decryptor;

    @BeforeEach
    void setUp() {
        decryptor = new SxwlPasswordDecryptor(keyManager);
    }

    @Test
    @DisplayName("decrypt 应解密密码")
    void decrypt_shouldDecryptPassword() {
        when(keyManager.decrypt("encrypted")).thenReturn("plainPassword");
        assertEquals("plainPassword", decryptor.decrypt("encrypted"));
    }

    @Test
    @DisplayName("decrypt 输入为空应返回空字符串")
    void decrypt_emptyInput_shouldReturnEmpty() {
        assertEquals("", decryptor.decrypt(null));
        assertEquals("", decryptor.decrypt(""));
        assertEquals("", decryptor.decrypt("   "));
    }

    @Test
    @DisplayName("decrypt 解密失败应传播异常")
    void decrypt_failure_shouldPropagateException() {
        when(keyManager.decrypt("bad")).thenThrow(new RuntimeException("解密失败"));
        assertThrows(RuntimeException.class, () -> decryptor.decrypt("bad"));
    }
}
