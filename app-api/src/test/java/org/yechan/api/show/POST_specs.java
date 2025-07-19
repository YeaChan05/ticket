package org.yechan.api.show;

import static org.assertj.core.api.Assertions.assertThat;
import static org.yechan.testdata.CategoryGenerator.pickAnyCategory;
import static org.yechan.testdata.ShowInfoGenerator.generateDescription;
import static org.yechan.testdata.ShowInfoGenerator.generateHallId;
import static org.yechan.testdata.ShowInfoGenerator.generateSchedule;
import static org.yechan.testdata.ShowInfoGenerator.generateTicketGrade;
import static org.yechan.testdata.ShowInfoGenerator.generateTitle;
import static org.yechan.testdata.ShowInfoGenerator.generateUrl;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.yechan.config.IntegrationTest;
import org.yechan.dto.request.ShowRegisterRequest;
import org.yechan.dto.response.ShowRegisterResponse;
import org.yechan.entity.Seller;
import org.yechan.fixture.TestFixture;

@IntegrationTest
@DisplayName("POST /api/v1/shows")
public class POST_specs {

    @Test
    void 정상적인_공연_정보_등록은_성공적으로_이루어져야_한다(
            @Autowired TestFixture fixture
    ) {
        // Arrange
        var plusDay = (int) (Math.random() * 30);
        var request = new ShowRegisterRequest(
                generateTitle(),
                generateDescription(),
                pickAnyCategory(),
                generateUrl(),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                generateHallId(),
                List.of(
                        generateSchedule(plusDay),
                        generateSchedule(plusDay + 3)
                ),
                List.of(
                        generateTicketGrade("VIP"),
                        generateTicketGrade("RVIP")
                )
        );

        // Act
        var token = fixture.generateToken(Seller.class);
        fixture.post(
                        "/api/v1/shows",
                        request,
                        token
                )
                .exchange(ShowRegisterResponse.class)
                .onSuccess(
                        // Assert
                        response -> {
                            assertThat(response.getStatus()).isEqualTo("SUCCESS");
                        }
                );
    }

    @Test
    void 성공적인_공연_정보_등록_후_redirectUrl이_반환되어야_한다(
            @Autowired TestFixture fixture
    ) {
        // Arrange
        var plusDay = (int) (Math.random() * 30);
        var request = new ShowRegisterRequest(
                generateTitle(),
                generateDescription(),
                pickAnyCategory(),
                generateUrl(),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                generateHallId(),
                List.of(
                        generateSchedule(plusDay),
                        generateSchedule(plusDay + 3)
                ),
                List.of(
                        generateTicketGrade("VIP"),
                        generateTicketGrade("RVIP")
                )
        );

        // Act
        var token = fixture.generateToken(Seller.class);
        fixture.post(
                        "/api/v1/shows",
                        request,
                        token
                )
                .exchange(ShowRegisterResponse.class)
                .onSuccess(
                        // Assert
                        response -> {
                            assertThat(response.getData().redirectUrl())
                                    .isNotNull()
                                    .contains("/api/v1/shows/");
                        }
                );
    }

}
