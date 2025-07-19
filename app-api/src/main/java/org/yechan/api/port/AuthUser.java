package org.yechan.api.port;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 인증된 사용자(User 또는 Seller)를 매개변수로 주입받기 위한 어노테이션
 * 이 어노테이션이 붙은 매개변수는 현재 인증된 사용자 정보로 자동 주입됩니다.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthUser {
    /**
     * 필요한 사용자 타입을 지정합니다.
     * @return 사용자 타입
     */
    UserType type() default UserType.USER;
    
    /**
     * 사용자 타입 열거형
     */
    enum UserType {
        /**
         * 일반 사용자(Customer)
         */
        USER,
        
        /**
         * 판매자(Seller)
         */
        SELLER
    }
}
