package org.yechan.dto.annotation;

import static java.util.Objects.requireNonNull;
import static org.yechan.dto.annotation.AuthUser.UserType.SELLER;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.yechan.error.SellerErrorCode;
import org.yechan.error.UserErrorCode;
import org.yechan.error.exception.SellerException;
import org.yechan.error.exception.UserException;
import org.yechan.repository.SellerRepository;
import org.yechan.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        var email = getEmail();

        AuthUser authUser = parameter.getParameterAnnotation(AuthUser.class);
        if (requireNonNull(authUser).type() == SELLER) {
            return sellerRepository.findByEmail(email)
                    .orElseThrow(() -> new SellerException("판매자를 찾을 수 없습니다.", SellerErrorCode.SELLER_NOT_FOUND));
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("사용자를 찾을 수 없습니다.", UserErrorCode.USER_NOT_FOUND));
    }

    private String getEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
            authentication.getPrincipal().equals("anonymousUser")) {
            throw new UserException("인증되지 않은 사용자입니다.", UserErrorCode.USER_NOT_FOUND);
        }

        if (authentication instanceof UsernamePasswordAuthenticationToken token) {
            return token.getPrincipal().toString();
        }
        throw new UserException("<UNK> <UNK> <UNK> <UNK>.", UserErrorCode.USER_NOT_FOUND);
    }
}
