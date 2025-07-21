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

    @Test
    void 정상적인_공연_정보_등록은_성공적으로_이루어져야_한다(
            @Autowired TestFixture fixture
    ) {
        // Arrange
        var grades = List.of("VIP", "RVIP");
        var request = generateShowRegisterRequest(grades, (int) (Math.random() * 30), generateTitle());

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
        var request = generateShowRegisterRequest(grades, (int) (Math.random() * 30), generateTitle());

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

    private static ShowRegisterRequest generateShowRegisterRequest(List<String> grades, int plusDay, String title) {
        return new ShowRegisterRequest(
                title,
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
    @DisplayName("공연 제목이 중복된 경우 SHOW-001 오류가 발생해야 한다")
    void 공연_제목이_중복된_경우_SHOW_001_오류가_발생해야_한다(
            @Autowired TestFixture fixture
    ) {
        // Arrange
        var grades = List.of("VIP", "RVIP");
        var registeredTitle = generateTitle();
        var request = generateShowRegisterRequest(grades, (int) (Math.random() * 30), registeredTitle);
        var token = fixture.generateToken(Seller.class);

        // Act
        fixture.post(
                        "/api/v1/shows",
                        request,
                        token
                )
                .exchange(ShowRegisterResponse.class);

        fixture.post(
                        "/api/v1/shows",
                        generateShowRegisterRequest(grades, (int) (Math.random() * 30), registeredTitle),
                        token
                )
                .exchange(ShowRegisterResponse.class)
                .onError(
                        // Assert
                        errorResponse -> assertThat(errorResponse.getStatus()).isEqualTo("SHOW-001")
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
        var request = generateShowRegisterRequest(grades, (int) (Math.random() * 30), generateTitle());

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
                            assertThat(showRepository.findAll().stream()
                                    .filter(show -> show
                                            .getTitle().equals(request.title())).findAny().get())
                                    .satisfies(
                                            show -> assertThat(show.getCategory()).isEqualTo(request.category()),
                                            show -> assertThat(show.getTitle()).isEqualTo(request.title()),
                                            show -> assertThat(show.getDescription()).isEqualTo(request.description()),
                                            show -> assertThat(show.getHallId()).isEqualTo(request.hallId())
                                    );
                        });
    }

    @Test
    @DisplayName("중복된 key로 등록 시도 시 SHOW-002 오류가 발생해야 한다")
    void 중복된_key로_등록_시도_시_SHOW_002_오류가_발생해야_한다(
            @Autowired TestFixture fixture,
            @Autowired JpaShowRepository showRepository
    ) {
        // Arrange
        var grades = List.of("VIP", "RVIP");
        var firstTitle = generateTitle();
        var secondTitle = generateTitle();
        var firstRequest = generateShowRegisterRequest(grades, (int) (Math.random() * 30), firstTitle);
        var secondRequest = generateShowRegisterRequest(grades, (int) (Math.random() * 30), secondTitle);
        var token = fixture.generateToken(Seller.class);

        // Act
        fixture.post(
                "/api/v1/shows",
                firstRequest,
                token
        ).exchange(ShowRegisterResponse.class);

        var firstResponse = showRepository.findAll().stream()
                .filter(show -> show.getTitle().equals(firstTitle))
                .findFirst()
                .orElseThrow();
        fixture.post(
                "/api/v1/shows",
                secondRequest,
                token
        ).exchange(ShowRegisterResponse.class);

        var secondResponse = showRepository.findAll().stream()
                .filter(show -> show.getTitle().equals(secondTitle))
                .findFirst()
                .orElseThrow();

        // Assert
        assertThat(firstResponse.getKey()).isNotEqualTo(secondResponse.getKey());
    }
}
