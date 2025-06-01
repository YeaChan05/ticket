package org.yechan.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.yechan.entity.User;
import org.yechan.repository.JpaUserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaUserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입_성공_테스트")
    void 회원가입에_성공한다() throws Exception {
        // given
        Map<String, String> userRequest = new HashMap<>();
        userRequest.put("name", "홍길동");
        userRequest.put("email", "test@example.com");
        userRequest.put("password", "Password1!");
        userRequest.put("phone", "010-1234-5678");

        // when & then
        mockMvc.perform(post("/api/v1/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andDo(print())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.name").value("홍길동"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }

    @Test
    @DisplayName("중복된_이메일_회원가입_테스트")
    void 중복된_이메일을_사용한_회원가입은_에러를_발생시킨다() throws Exception {
        // given
        User existingUser = User.builder()
                .name("기존사용자")
                .email("existing@example.com")
                .password("Password1!")
                .phone("010-9876-5432")
                .build();
        userRepository.save(existingUser);

        Map<String, String> userRequest = new HashMap<>();
        userRequest.put("name", "신규사용자");
        userRequest.put("email", "existing@example.com");
        userRequest.put("password", "Password1!");
        userRequest.put("phone", "010-1234-5678");

        // when & then
        mockMvc.perform(post("/api/v1/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andDo(print())
                .andExpect(jsonPath("$.status").value("EMAIL-001"))
                .andExpect(jsonPath("$.message").value("이미 사용중인 이메일입니다."));
    }

    @Test
    @DisplayName("중복된_연락처_회원가입_테스트")
    void 중복된_연락처를_사용한_회원가입은_에러를_발생시킨다() throws Exception {
        // given
        User existingUser = User.builder()
                .name("기존사용자")
                .email("existing@example.com")
                .password("Password1!")
                .phone("010-9876-5432")
                .build();
        userRepository.save(existingUser);

        Map<String, String> userRequest = new HashMap<>();
        userRequest.put("name", "신규사용자");
        userRequest.put("email", "new@example.com");
        userRequest.put("password", "Password1!");
        userRequest.put("phone", "010-9876-5432");

        // when & then
        mockMvc.perform(post("/api/v1/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andDo(print())
                .andExpect(jsonPath("$.status").value("EMAIL-003"))
                .andExpect(jsonPath("$.message").value("이미 사용중인 연락처입니다."));
    }

    @Test
    @DisplayName("유효하지_않은_이메일_형식_테스트")
    void 유효하지_않은_이메일_형식으로_회원가입하면_에러를_발생시킨다() throws Exception {
        // given
        Map<String, String> userRequest = new HashMap<>();
        userRequest.put("name", "홍길동");
        userRequest.put("email", "invalid-email");
        userRequest.put("password", "Password1!");
        userRequest.put("phone", "010-1234-5678");

        // when & then
        mockMvc.perform(post("/api/v1/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andDo(print())
                .andExpect(jsonPath("$.status").value("CONSTRAINT_VIOLATION"))
                .andExpect(jsonPath("$.message").value("이메일 주소는 '@' 기호를 포함해야 합니다."));
    }

    @Test
    @DisplayName("약한_비밀번호_강도_테스트")
    void 약한_비밀번호로_회원가입하면_에러를_발생시킨다() throws Exception {
        // given
        Map<String, String> userRequest = new HashMap<>();
        userRequest.put("name", "홍길동");
        userRequest.put("email", "test@example.com");
        userRequest.put("password", "weakpwd");
        userRequest.put("phone", "010-1234-5678");

        // when & then
        mockMvc.perform(post("/api/v1/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andDo(print())
                .andExpect(jsonPath("$.status").value("WEAK_PASSWORD"))
                .andExpect(jsonPath("$.message").value("비밀번호는 최소 8자 이상이어야 하며, 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다."));
    }

    @Test
    @DisplayName("유효하지_않은_연락처_형식_테스트")
    void 유효하지_않은_연락처_형식으로_회원가입하면_에러를_발생시킨다() throws Exception {
        // given
        Map<String, String> userRequest = new HashMap<>();
        userRequest.put("name", "홍길동");
        userRequest.put("email", "test@example.com");
        userRequest.put("password", "Password1!");
        userRequest.put("phone", "01012345678");

        // when & then
        mockMvc.perform(post("/api/v1/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andDo(print())
                .andExpect(jsonPath("$.status").value("INVALID_PHONE_FORMAT"))
                .andExpect(jsonPath("$.message").value("유효하지 않은 연락처 형식입니다. (010-XXXX-XXXX)"));
    }

    @Test
    @DisplayName("필수_필드_누락_테스트")
    void 필수_필드가_누락된_회원가입은_에러를_발생시킨다() throws Exception {
        // given
        // name 필드 누락
        Map<String, String> userRequest = new HashMap<>();
        userRequest.put("email", "test@example.com");
        userRequest.put("password", "Password1!");
        userRequest.put("phone", "010-1234-5678");

        // when & then
        mockMvc.perform(post("/api/v1/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andDo(print())
                .andExpect(jsonPath("$.status").value("CONSTRAINT_VIOLATION"))
                .andExpect(jsonPath("$.message").value("이름을 입력해주세요."));
    }

}
