package config;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.AntPathMatcher;

@ConfigurationProperties(prefix = "security")
@Slf4j
public record SecurityConfigurationProperties(
        List<String> publicEndpoints,

        List<String> userEndpoints,

        @DefaultValue("false")
        boolean extensibleEnabled,

        @DefaultValue("false")
        boolean corsEnabled,

        @DefaultValue("false")
        boolean csrfEnabled
) {

    public SecurityConfigurationProperties {
        validateDuplicateEndpoints(publicEndpoints);
        validateDuplicateEndpoints(userEndpoints);
        validateEndpoints(publicEndpoints, userEndpoints);
        validateEndpoints(userEndpoints, publicEndpoints);
    }

    private void validateDuplicateEndpoints(final List<String> paths) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for (String path : paths) {
            if (paths.stream().filter(s -> !s.equals(path)).anyMatch(p -> antPathMatcher.match(p, path))) {
                throw new IllegalArgumentException("Duplicate endpoint found: " + path);
            }
            log.debug("Valid endpoint: {}", path);
        }
    }

    private void validateEndpoints(List<String> paths1, List<String> paths2) {
        if (paths1.stream().anyMatch(paths2::contains)) {
            throw new IllegalArgumentException("Public and User endpoints must not overlap.");
        }

        AntPathMatcher antPathMatcher = new AntPathMatcher();
        paths1.forEach(publicEndpoint -> paths2
                .stream()
                .filter(userEndpoint -> antPathMatcher.match(publicEndpoint, userEndpoint))
                .findAny()
                .ifPresent((s) -> {
                    throw new IllegalArgumentException(
                            publicEndpoint + "' conflicts with '" + s + "'");
                }));
    }
}
