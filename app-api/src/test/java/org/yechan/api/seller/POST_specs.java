package org.yechan.api.seller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.yechan.testdata.EmailGenerator.generateEmail;
import static org.yechan.testdata.PasswordGenerator.generatePassword;
import static org.yechan.testdata.PhoneNumberGenerator.generatePhone;
import static org.yechan.testdata.UsernameGenerator.generateUsername;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
}
