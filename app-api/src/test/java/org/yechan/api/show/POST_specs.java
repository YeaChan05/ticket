package org.yechan.api.show;

import static org.assertj.core.api.Assertions.assertThat;
import static org.yechan.testdata.CategoryGenerator.pickAnyCategory;
import static org.yechan.testdata.ShowInfoGenerator.generateDescription;
import static org.yechan.testdata.ShowInfoGenerator.generateHallKey;
import static org.yechan.testdata.ShowInfoGenerator.generateSchedule;
import static org.yechan.testdata.ShowInfoGenerator.generateTitle;
import static org.yechan.testdata.ShowInfoGenerator.generateUrl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.yechan.config.IntegrationTest;
import org.yechan.dto.request.ShowRegisterRequest;
import org.yechan.dto.response.ShowRegisterResponse;
import org.yechan.entity.Hall;
import org.yechan.entity.Seller;
import org.yechan.fixture.TestFixture;
import org.yechan.repository.JpaHallRepository;
import org.yechan.repository.JpaShowRepository;
import org.yechan.testdata.ShowInfoGenerator;

@IntegrationTest
@DisplayName("POST /api/v1/shows")
public class POST_specs {

    private static Hall saveHall(JpaHallRepository hallRepository, UUID request, int capacity) {
        return hallRepository.save(
                Hall.builder()
                        .name("Test Hall")
                        .address("123 Main St")
                        .hallKey(request)
                        .contactPhone("010-1234-5678")
                        .capacity(capacity)
                        .build()
        );
    }

    private static ShowRegisterRequest generateShowRegisterRequest(List<String> grades, int plusDay, String title,
                                                                   int eventDuration, int ticketCount, UUID hallKey) {
        return new ShowRegisterRequest(
                title,
                generateDescription(),
                pickAnyCategory(),
                generateUrl(),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(eventDuration),
                hallKey,
                ticketCount,
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
            @Autowired TestFixture fixture,
            @Autowired JpaHallRepository hallRepository
    ) {
        // Arrange
        var grades = List.of("VIP", "RVIP");
        var request = generateShowRegisterRequest(grades, (int) (Math.random() * 30), generateTitle(), 1, 100,
                generateHallKey());
        saveHall(hallRepository, request.hallId(), 100);

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
            @Autowired TestFixture fixture,
            @Autowired JpaHallRepository hallRepository
    ) {
        // Arrange
        var grades = List.of("VIP", "RVIP");
        var hallKey = generateHallKey();
        var request = generateShowRegisterRequest(grades, (int) (Math.random() * 30), generateTitle(), 1, 100,
                hallKey);
        saveHall(hallRepository, hallKey, 100);

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
    @DisplayName("공연 제목이 중복된 경우 SHOW-001 오류가 발생해야 한다")
    void 공연_제목이_중복된_경우_SHOW_001_오류가_발생해야_한다(
            @Autowired TestFixture fixture,
            @Autowired JpaHallRepository hallRepository
    ) {
        // Arrange
        var grades = List.of("VIP", "RVIP");
        var registeredTitle = generateTitle();
        var hallKey = generateHallKey();
        var request = generateShowRegisterRequest(grades, (int) (Math.random() * 30), registeredTitle, 1, 100, hallKey);
        saveHall(hallRepository, hallKey, 100);
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
                        generateShowRegisterRequest(grades, (int) (Math.random() * 30), registeredTitle, 1, 100,
                                generateHallKey()),
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
            @Autowired JpaShowRepository showRepository,
            @Autowired JpaHallRepository hallRepository
    ) {
        // Arrange
        var grades = List.of("VIP", "RVIP");
        var request = generateShowRegisterRequest(grades, (int) (Math.random() * 30), generateTitle(), 1, 100,
                generateHallKey());
        saveHall(hallRepository, request.hallId(), 100);

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
    @DisplayName("티켓팅 날짜 및 시간이 유효하지 않은 경우 SHOW-002 오류가 발생해야 한다")
    void 티켓팅_날짜_및_시간이_유효하지_않은_경우_SHOW_002_오류가_발생해야_한다(
            @Autowired TestFixture fixture,
            @Autowired JpaHallRepository hallRepository
    ) {
        // Arrange
        var grades = List.of("VIP", "RVIP");
        var request = generateShowRegisterRequest(grades, 2, generateTitle(), -1, 100, generateHallKey());
        var token = fixture.generateToken(Seller.class);
        saveHall(hallRepository, request.hallId(), 100);
        // Act
        fixture.post(
                        "/api/v1/shows",
                        request,
                        token
                )
                .exchange(ShowRegisterResponse.class)
                .onError(
                        // Assert
                        errorResponse -> assertThat(errorResponse.getStatus()).isEqualTo("SHOW-002")
                );
    }

    @Test
    @DisplayName("티켓 총 수량이 공연장 수용 가능 인원을 초과하는 경우 SHOW-003 오류가 발생해야 한다")
    void 티켓_총_수량이_공연장_수용_가능_인원을_초과하는_경우_SHOW_003_오류가_발생해야_한다(
            @Autowired TestFixture fixture,
            @Autowired JpaHallRepository hallRepository
    ) {
        // Arrange
        var grades = List.of("VIP", "RVIP");
        var ticketCount = 100;
        var hallCapacity = 50;
        var hallKey = UUID.randomUUID();
        var request = generateShowRegisterRequest(grades, 2, generateTitle(), 1, ticketCount, hallKey);
        saveHall(hallRepository, hallKey, hallCapacity);
        var token = fixture.generateToken(Seller.class);

        // Act
        fixture.post(
                        "/api/v1/shows",
                        request,
                        token
                )
                .exchange(ShowRegisterResponse.class)
                .onError(
                        // Assert
                        errorResponse -> assertThat(errorResponse.getStatus()).isEqualTo("SHOW-003")
                );
    }

    @Test
    @DisplayName("존재하지 않는 공연 장소를 입력한 경우 SHOW-004 오류가 발생해야 한다")
    void 존재하지_않는_공연_장소를_입력한_경우_SHOW_004_오류가_발생해야_한다(
            @Autowired TestFixture fixture,
            @Autowired JpaHallRepository hallRepository
    ) {
        // Arrange
        var grades = List.of("VIP", "RVIP");
        var hallKey = UUID.randomUUID();
        var invalidHallKey = UUID.randomUUID();
        var request = generateShowRegisterRequest(grades, 2, generateTitle(), 1, 100, hallKey);
        saveHall(hallRepository, hallKey, 100);

        var token = fixture.generateToken(Seller.class);
        // Act
        fixture.post(
                        "/api/v1/shows",
                        generateShowRegisterRequest(grades, 2, generateTitle(), 1, 100, invalidHallKey),
                        token
                )
                .exchange(ShowRegisterResponse.class)
                .onError(
                        // Assert
                        errorResponse -> assertThat(errorResponse.getStatus()).isEqualTo("SHOW-004")
                );
    }
}
