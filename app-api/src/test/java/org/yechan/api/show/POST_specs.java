package org.yechan.api.show;

import static org.assertj.core.api.Assertions.assertThat;
import static org.yechan.testdata.CategoryGenerator.pickAnyCategory;
import static org.yechan.testdata.ShowInfoGenerator.generateDescription;
import static org.yechan.testdata.ShowInfoGenerator.generateHallId;
import static org.yechan.testdata.ShowInfoGenerator.generateSchedule;
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
import org.yechan.repository.JpaShowRepository;
import org.yechan.testdata.ShowInfoGenerator;

@IntegrationTest
@DisplayName("POST /api/v1/shows")
public class POST_specs {

    private static ShowRegisterRequest generateShowRegisterRequest(List<String> grades, int plusDay) {
        return new ShowRegisterRequest(
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
                grades.stream()
                        .map(ShowInfoGenerator::generateTicketGrade)
                        .toList()
        );
    }

    @Test
    void 정상적인_공연_정보_등록은_성공적으로_이루어져야_한다(
            @Autowired TestFixture fixture
    ) {
        // Arrange
        var grades = List.of("VIP", "RVIP");
        var request = generateShowRegisterRequest(grades, (int) (Math.random() * 30));

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
        var grades = List.of("VIP", "RVIP");
        var request = generateShowRegisterRequest(grades, (int) (Math.random() * 30));

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

    @Test
    @DisplayName("공연 정보 등록 후, 등록된 공연 정보가 데이터베이스에 저장되어야 한다")
    void 공연_정보_등록_후_등록된_공연_정보가_데이터베이스에_저장되어야_한다(
            @Autowired TestFixture fixture,
            @Autowired JpaShowRepository showRepository
    ) {
        // Arrange
        var grades = List.of("VIP", "RVIP");
        var request = generateShowRegisterRequest(grades, (int) (Math.random() * 30));

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
                            assertThat(showRepository.findAll())
                                    .first()
                                    .satisfies(
                                            show -> assertThat(show.getCategory()).isEqualTo(request.category()),
                                            show -> assertThat(show.getTitle()).isEqualTo(request.title()),
                                            show -> assertThat(show.getDescription()).isEqualTo(request.description()),
                                            show -> assertThat(show.getHallId()).isEqualTo(request.hallId())
                                    );
                        });
    }
}
