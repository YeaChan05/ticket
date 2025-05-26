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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private Host host;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id")
    private Hall hall;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false,columnDefinition = "VARCHAR(20)")
    private Category category;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "contact_phone")
    private String contactPhone;


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
