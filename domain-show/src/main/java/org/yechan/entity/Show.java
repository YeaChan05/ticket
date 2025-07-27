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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "shows",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_shows_key", columnNames = "shows_key"),
                @UniqueConstraint(name = "uk_shows_title", columnNames = "title")
        }
)
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Show extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "show_id")
    private Long id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "shows_key", nullable = false, unique = true)
    @Builder.Default
    private UUID key = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @Column(name = "hall_id", nullable = false)
    private UUID hallId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, columnDefinition = "VARCHAR(20)")
    private Category category;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "ticketing_start_date")
    private LocalDateTime ticketingStartDate;

    @Column(name = "ticketing_end_date")
    private LocalDateTime ticketingEndDate;

    @Getter
    @AllArgsConstructor
    public enum Category {
        MUSICAL("뮤지컬"),
        CONCERT("콘서트"),
        PLAY("연극"),
        CLASSICAL("클래식"),
        DANCE("무용"),
        EXHIBITION("전시");
        private final String description;
    }
}
