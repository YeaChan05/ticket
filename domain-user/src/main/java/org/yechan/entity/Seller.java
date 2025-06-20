package org.yechan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Seller extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="seller_id")
    private Long id;

    @Column(name="seller_name", nullable = false)
    private String name;

    @Column(name="seller_email", nullable = false)
    private String email;

    @Column(name="seller_password", nullable = false)
    private String password;

    @Column(name="seller_contact", nullable = false)
    private String contact;
}
