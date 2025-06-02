package org.yechan.api;

import static autoparams.customization.dsl.ArgumentCustomizationDsl.freezeArgument;
import static autoparams.customization.dsl.ArgumentCustomizationDsl.set;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import autoparams.AutoParams;
import autoparams.CsvAutoSource;
import autoparams.ResolutionContext;
import autoparams.ValueAutoSource;
import autoparams.generator.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.yechan.dto.request.UserRegisterRequest;
import org.yechan.entity.User;
import org.yechan.repository.JpaUserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    private static final String CONSTRAINT_VIOLATION = "CONSTRAINT_VIOLATION";
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaUserRepository userRepository;

    ResolutionContext context = new ResolutionContext();
    Factory<UserRegisterRequest> factory = Factory.create(UserRegisterRequest.class);


    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @AutoParams
    @DisplayName("회원가입_성공_테스트")
    void 회원가입에_성공한다() throws Exception {
        // given
        UserRegisterRequest userRequest = factory.get(
                freezeArgument("name").to("홍길동"),
                freezeArgument("email").to("test01@gmail.com"),
                freezeArgument("password").to("Password1!"),
                freezeArgument("phone").to("010-1234-5678")
        );
        // when & then
        mockMvc.perform(post("/api/v1/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andDo(print())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.name").value(userRequest.name()))
                .andExpect(jsonPath("$.data.email").value(userRequest.email()));
    }

    @Test
    @AutoParams
    @DisplayName("중복된_이메일_회원가입_테스트")
    void 중복된_이메일을_사용한_회원가입은_에러를_발생시킨다() throws Exception {
        // given
        context.customize(
                set(User::getId).to(null),
                set(User::getPhone).to("010-0000-0000")
        );
        User existingUser = context.resolve();
        userRepository.save(existingUser);
        UserRegisterRequest userRequest = factory.get(
                freezeArgument("name").to("홍길동"),
                freezeArgument("email").to(existingUser.getEmail()),
                freezeArgument("password").to("Password1!"),
                freezeArgument("phone").to("010-1234-5678")
        );

        // when & then
        mockMvc.perform(post("/api/v1/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andDo(print())
                .andExpect(jsonPath("$.status").value("EMAIL-001"))
                .andExpect(jsonPath("$.message").value("이미 사용중인 이메일입니다."));
    }

    @Test
    @AutoParams
    @DisplayName("중복된_연락처_회원가입_테스트")
    void 중복된_연락처를_사용한_회원가입은_에러를_발생시킨다() throws Exception {
        // given
        context.customize(
                set(User::getId).to(null),
                set(User::getPhone).to("010-0000-0000")
        );
        User existingUser = context.resolve();
        userRepository.save(existingUser);
        UserRegisterRequest userRequest = factory.get(
                freezeArgument("name").to("홍길동"),
                freezeArgument("email").to("test01@gmail.com"),
                freezeArgument("password").to("Password1!"),
                freezeArgument("phone").to(existingUser.getPhone())
        );

        // when & then
        mockMvc.perform(post("/api/v1/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andDo(print())
                .andExpect(jsonPath("$.status").value("EMAIL-003"))
                .andExpect(jsonPath("$.message").value("이미 사용중인 연락처입니다."));
    }

    @ParameterizedTest
    @CsvAutoSource(
            {
                    "testexample.com, 이메일 주소는 '@' 기호를 포함해야 합니다.", // '@' 기호가 없음
                    "test@.com, 이메일 형식이 올바르지 않습니다.",// 도메인 부분이 없음
                    "test@com, 이메일 형식이 올바르지 않습니다.",// '.'이 없음
                    "test@-example.com, 이메일 도메인 부분이 올바르지 않습니다.", // 도메인 시작이 '-'로 시작함
                    "test@example..com, 이메일 주소에 연속된 점(.)이 포함되어 있습니다."// 도메인 부분에 '..'이 있음
            })
    @DisplayName("유효하지_않은_이메일_형식_테스트")
    void 유효하지_않은_이메일_형식으로_회원가입하면_에러를_발생시킨다(String invalidEmail,String message) throws Exception {
        // given
        UserRegisterRequest userRequest = factory.get(
                freezeArgument("name").to("홍길동"),
                freezeArgument("email").to(invalidEmail),
                freezeArgument("password").to("Password1!"),
                freezeArgument("phone").to("010-1234-5678")
        );

        // when & then
        mockMvc.perform(post("/api/v1/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andDo(print())
                .andExpect(jsonPath("$.status").value(CONSTRAINT_VIOLATION))
                .andExpect(jsonPath("$.message").value(message));
    }

    @ParameterizedTest
    @ValueAutoSource(
            strings = {
                    "password", // 대문자, 숫자, 특수문자 없음
                    "PASSWORD1!", // 소문자 없음
                    "Password!", // 숫자 없음
                    "Password1" // 특수문자 없음
            })
    @DisplayName("약한_비밀번호_강도_테스트")
    void 약한_비밀번호로_회원가입하면_에러를_발생시킨다(String invalidPassword) throws Exception {
        UserRegisterRequest userRequest = factory.get(
                freezeArgument("name").to("홍길동"),
                freezeArgument("email").to("test01@gmail.com"),
                freezeArgument("password").to(invalidPassword),
                freezeArgument("phone").to("010-1234-5678")
        );
        // when & then
        mockMvc.perform(post("/api/v1/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andDo(print())
                .andExpect(jsonPath("$.status").value(CONSTRAINT_VIOLATION))
                .andExpect(jsonPath("$.message").value("비밀번호는 최소 8자 이상, 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다."));
    }

    @ParameterizedTest
    @ValueAutoSource(
            strings = {
                    "01012345678", // 하이픈 없이
                    "010-1234-567", // 하이픈이 잘못된 위치에 있음
                    "010-12345-6789" // 하이픈이 잘못된 위치에 있음
            })
    @DisplayName("유효하지_않은_연락처_형식_테스트")
    void 유효하지_않은_연락처_형식으로_회원가입하면_에러를_발생시킨다(String invalidPhoneNumber)
            throws Exception {
        // given
        UserRegisterRequest userRequest = factory.get(
                freezeArgument("name").to("홍길동"),
                freezeArgument("email").to("test01@gmail.com"),
                freezeArgument("password").to("Password1!"),
                freezeArgument("phone").to(invalidPhoneNumber)
        );
        // when & then
        mockMvc.perform(post("/api/v1/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andDo(print())
                .andExpect(jsonPath("$.status").value(CONSTRAINT_VIOLATION))
                .andExpect(jsonPath("$.message").value("전화번호 형식은 010-xxxx-xxxx이어야 합니다."));
    }

    @Test
    @DisplayName("필수_필드_누락_테스트")
    void 필수_필드가_누락된_회원가입은_에러를_발생시킨다() throws Exception {
        // given
        // name 필드 누락
        UserRegisterRequest userRequest = factory.get(
                freezeArgument("name").to(null),
                freezeArgument("email").to("test01@gmail.com"),
                freezeArgument("password").to("Password1!"),
                freezeArgument("phone").to("010-1234-5678")
        );

        // when & then
        mockMvc.perform(post("/api/v1/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andDo(print())
                .andExpect(jsonPath("$.status").value(CONSTRAINT_VIOLATION))
                .andExpect(jsonPath("$.message").value("이름을 입력해주세요."));
    }

}
