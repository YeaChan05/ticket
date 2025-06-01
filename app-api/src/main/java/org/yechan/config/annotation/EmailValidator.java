package org.yechan.config.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import org.springframework.stereotype.Component;
import org.yechan.error.CustomViolationException;

@Component
public class EmailValidator implements ConstraintValidator<Email, String> {
    private static final String REGEXP = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String LOCAL_PART_PATTERN = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+$";
    private static final String DOMAIN_PATTERN = "^[a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(\\.[a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
    private static final int MAX_EMAIL_LENGTH = 254;
    private static final int MAX_LOCAL_PART_LENGTH = 64;
    private Class<? extends CustomViolationException> exception;

    @Override
    public void initialize(final Email constraintAnnotation) {
        this.exception = constraintAnnotation.exception();
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        // 빈 값은 @NotNull이나 @NotEmpty로 검증
        if (value == null || value.isEmpty()) {
            return true;
        }

        // 컨텍스트 초기화 - 기본 메시지 비활성화
        context.disableDefaultConstraintViolation();

        String message = null;
        if (!value.matches(REGEXP)) {
            message = "이메일 형식이 올바르지 않습니다.";
        }
        if (value.length() > MAX_EMAIL_LENGTH) {
            message = "이메일 주소는 254자를 초과할 수 없습니다.";
        }

        String[] parts = value.split("@");
        String localPart = parts[0];
        String domain = parts[1];

        // 로컬파트 길이 검증
        if (localPart.length() > MAX_LOCAL_PART_LENGTH) {
            message = "이메일 아이디 부분은 64자를 초과할 수 없습니다.";
        }

        if (!localPart.matches(LOCAL_PART_PATTERN)) {
            message = "이메일 아이디 부분에 허용되지 않는 문자가 포함되어 있습니다.";
        }

        if (!domain.matches(DOMAIN_PATTERN)) {
            message = "이메일 도메인 부분이 올바르지 않습니다.";
        }

        if (value.contains("..")) {
            message = "이메일 주소에 연속된 점(.)이 포함되어 있습니다.";
        }

        String[] domainParts = domain.split("\\.");
        String tld = domainParts[domainParts.length - 1];
        if (tld.length() < 2) {
            message = "이메일 최상위 도메인이 유효하지 않습니다.";
        }

        if (message != null) {
            try {
                throw exception.getConstructor(String.class).newInstance(message);
            } catch (InstantiationException | IllegalAccessException |
                     InvocationTargetException | NoSuchMethodException e) {
                //fuck you
            }
        }

        return true;
    }
}
