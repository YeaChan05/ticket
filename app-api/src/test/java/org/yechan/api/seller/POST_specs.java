package org.yechan.api.seller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.yechan.testdata.EmailGenerator.generateEmail;
import static org.yechan.testdata.PasswordGenerator.generatePassword;
import static org.yechan.testdata.PhoneNumberGenerator.generatePhone;
import static org.yechan.testdata.UsernameGenerator.generateUsername;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.yechan.config.IntegrationTest;
import org.yechan.dto.request.SellerRegisterRequest;
import org.yechan.fixture.TestFixture;
import org.yechan.repository.JpaSellerRepository;

@IntegrationTest
@DisplayName("POST /api/v1/seller")
public class POST_specs {
    @Test
    void 판매자_회원가입_요청은_성공적으로_처리된다(
            @Autowired TestFixture fixture
    ) {
        // Arrange
        var hostname = generateUsername();
        var email = generateEmail();
        var phone = generatePhone();
        var password = generatePassword();
        var request = new SellerRegisterRequest(
                hostname,
                email,
                password, phone
        );

        // Act
        fixture.post(
                        "/api/v1/sellers/sign-up",
                        request,
                        null
                )
                .exchange(Void.class)
                .onSuccess(
                        // Assert
                        response -> {
                            assertThat(response.getStatus()).isEqualTo("SUCCESS");
                        }
                );
    }

    @Test
    void 판매자의_정보는_요청된_내용과_동일하게_데이터베이스에_저장된다(
            @Autowired TestFixture fixture,
            @Autowired JpaSellerRepository repository
    ) {
        // Arrange
        var hostname = generateUsername();
        var email = generateEmail();
        var contact = generatePhone();
        var password = generatePassword();
        var request = new SellerRegisterRequest(
                hostname,
                email,
                password, contact
        );

        // Act
        fixture.post(
                        "/api/v1/sellers/sign-up",
                        request,
                        null
                )
                .exchange(Void.class)
                .onSuccess(
                        // Assert
                        response -> {
                            assertThat(response.getStatus()).isEqualTo("SUCCESS");
                        }
                );

        // Assert
        var seller = repository.findById(1L).orElseThrow();
        assertThat(seller.getName()).isEqualTo(hostname);
        assertThat(seller.getEmail()).isEqualTo(email);
        assertThat(seller.getContact()).isEqualTo(contact);
    }

    static SellerRegisterRequest[] blankFieldSellerRegisterRequests() {
        return new SellerRegisterRequest[]{
                new SellerRegisterRequest("", generateEmail(), generatePassword(), generatePhone()), // 이름 누락
                new SellerRegisterRequest(generateUsername(), "", generatePassword(), generatePhone()), // 이메일 누락
                new SellerRegisterRequest(generateUsername(), generateEmail(), "", generatePhone()), // 비밀번호 누락
                new SellerRegisterRequest(generateUsername(), generateEmail(), generatePassword(), ""), // 전화번호 누락
                new SellerRegisterRequest(null, generateEmail(), generatePassword(), generatePhone()), // 이름 null
                new SellerRegisterRequest(generateUsername(), null, generatePassword(), generatePhone()), // 이메일 null
                new SellerRegisterRequest(generateUsername(), generateEmail(), null, generatePhone()), // 비밀번호 null
                new SellerRegisterRequest(generateUsername(), generateEmail(), generatePassword(), null) // 전화번호 null
        };
    }

    @ParameterizedTest
    @MethodSource("org.yechan.api.seller.POST_specs#blankFieldSellerRegisterRequests")
    void 판매자_회원가입_요청_시_필수_필드가_누락된_경우_CONSTRAINT_VIOLATION이_응답된다(
            SellerRegisterRequest request,// Arrange
            @Autowired TestFixture fixture
    ) {
        // Act
        fixture.post(
                        "/api/v1/sellers/sign-up",
                        request,
                        null
                )
                .exchange(Void.class)
                .onError(
                        // Assert
                        error -> {
                            assertThat(error.getStatus()).isEqualTo("CONSTRAINT_VIOLATION");
                        }
                );
    }

    @ParameterizedTest
    @MethodSource("org.yechan.testdata.EmailGenerator#invalidEmailRequests")
    void 이메일이_RFC_5322_형식을_만족하지_않은_경우_CONSTRAINT_VIOLATION이_응답된다(
            String email, // Arrange
            @Autowired TestFixture fixture
    ) {
        // Arrange
        var request = new SellerRegisterRequest(
                generateUsername(),
                email,
                generatePassword(),
                generatePhone()
        );

        // Act
        fixture.post(
                        "/api/v1/sellers/sign-up",
                        request,
                        null
                )
                .exchange(Void.class)
                .onError(
                        // Assert
                        error -> {
                            assertThat(error.getStatus()).isEqualTo("CONSTRAINT_VIOLATION");
                        }
                );
    }

    @Test
    @DisplayName("중복된 이메일로 가입 시 에러 코드 SELLER-001을 반환한다")
    void 중복된_이메일로_가입_시_에러_코드_SELLER_001을_반환한다(
            @Autowired TestFixture fixture
    ) {
        // Arrange
        var email = generateEmail();
        var request1 = new SellerRegisterRequest(
                generateUsername(),
                email,
                generatePassword(),
                generatePhone()
        );
        var request2 = new SellerRegisterRequest(
                generateUsername(),
                email,
                generatePassword(),
                generatePhone()
        );

        fixture.post(
                        "/api/v1/sellers/sign-up",
                        request1,
                        null
                )
                .exchange(Void.class);
        // Act
        fixture.post(
                        "/api/v1/sellers/sign-up",
                        request2,
                        null
                )
                .exchange(Void.class)
                .onError(
                        // Assert
                        error -> {
                            assertThat(error.getStatus()).isEqualTo("SELLER-001");
                        }
                );
    }
}
