package config;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.AntPathMatcher;

@ConfigurationProperties(prefix = "security")
@Slf4j
public record SecurityConfigurationProperties(
        @DefaultValue
        List<String> publicEndpoints,
        
        @DefaultValue
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
        if (paths == null || paths.isEmpty()) {
            log.debug("No endpoints provided, skipping validation.");
            return;
        }
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for (String path : paths) {
    private void validateDuplicateEndpoints(final List<String> paths) {
        if (paths == null || paths.isEmpty()) {
            log.debug("No endpoints provided, skipping validation.");
            return;
        }
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for (int i = 0; i < paths.size(); i++) {
            String currentPath = paths.get(i);
            for (int j = i + 1; j < paths.size(); j++) {
                String otherPath = paths.get(j);
                if (antPathMatcher.match(currentPath, otherPath)
                        || antPathMatcher.match(otherPath, currentPath)) {
                    throw new IllegalArgumentException(
                            "Duplicate endpoint found: " + currentPath + " conflicts with " + otherPath);
                }
            }
            log.debug("Valid endpoint: {}", currentPath);
        }
    }
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
