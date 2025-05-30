package org.yechan.entity;

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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders") // 'order'는 SQL 예약어이므로 'orders'로 테이블명 지정
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 주문한 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_schedule_id", nullable = false)
    private ShowSchedule showSchedule;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice; // 총 결제 금액

    @Column(name = "cancel_date")
    private LocalDateTime cancelDate; // 취소 일시

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(20)")
    private OrderStatus status;

    public enum OrderStatus {
        PENDING,
        COMPLETED,
        CANCELED
    }
}
