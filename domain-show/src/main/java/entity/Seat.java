package entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yechan.entity.BaseEntity;

@Entity
@Table(name = "seats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Seat extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="seat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_schedule_id")
    private ShowSchedule showSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private Section section;

    @Column(name = "row_name", nullable = false, length = 10)
    private String rowName; // 좌석 행 (예: A, B, C)

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber; // 좌석 번호 (예: 1, 2, 3)

    @Column(name = "name", nullable = false, length = 20)
    private String name; // 좌석 이름 (예: A-12, VIP-1)

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SeatStatus status;

    public enum SeatStatus {
        AVAILABLE,
        WAITING,
        RESERVED,
        SOLD_OUT,
        BLOCKED
    }
}
