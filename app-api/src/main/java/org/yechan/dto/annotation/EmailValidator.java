package org.yechan.dto.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<Email, String> {
    private static final String REGEXP = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String LOCAL_PART_PATTERN = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+$";
    private static final String DOMAIN_PATTERN = "^[a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(\\.[a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
    private static final int MAX_EMAIL_LENGTH = 254;
    private static final int MAX_LOCAL_PART_LENGTH = 64;

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        // 빈 값은 @NotNull이나 @NotEmpty로 검증
        if (value == null || value.isEmpty()) {
            return true;
        }

        // 컨텍스트 초기화 - 기본 메시지 비활성화
        context.disableDefaultConstraintViolation();

        if (!value.contains("@")) {
            addConstraintViolation(context, "이메일 주소는 '@' 기호를 포함해야 합니다.");
            return false;
        }
        if (value.length() > MAX_EMAIL_LENGTH) {
            addConstraintViolation(context, "이메일 주소는 254자를 초과할 수 없습니다.");
            return false;
        }
        if (value.contains("..")) {
            addConstraintViolation(context, "이메일 주소에 연속된 점(.)이 포함되어 있습니다.");
            return false;
        }
        if (!value.matches(REGEXP)) {
            addConstraintViolation(context, "이메일 형식이 올바르지 않습니다.");
            return false;
        }

        String[] parts = value.split("@");
        String localPart = parts[0];
        String domain = parts[1];

        // 로컬파트 길이 검증
        if (localPart.length() > MAX_LOCAL_PART_LENGTH) {
            addConstraintViolation(context, "이메일 아이디 부분은 64자를 초과할 수 없습니다.");
            return false;
        }

        if (!localPart.matches(LOCAL_PART_PATTERN)) {
            addConstraintViolation(context, "이메일 아이디 부분에 허용되지 않는 문자가 포함되어 있습니다.");
            return false;
        }

        if (!domain.matches(DOMAIN_PATTERN)) {
            addConstraintViolation(context, "이메일 도메인 부분이 올바르지 않습니다.");
            return false;
        }

        String[] domainParts = domain.split("\\.");
        String tld = domainParts[domainParts.length - 1];
        if (tld.length() < 2) {
            addConstraintViolation(context, "이메일 최상위 도메인이 유효하지 않습니다.");
            return false;
        }

        return true;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
