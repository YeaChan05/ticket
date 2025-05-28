package config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

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
        validateEndpoints(publicEndpoints, userEndpoints);
    }

    private static void validateEndpoints(List<String> publicEndpoints, List<String> userEndpoints) {
        Set<String> publicSet = new HashSet<>(publicEndpoints);
        Set<String> userSet = new HashSet<>(userEndpoints);
        Set<String> conflicts = new HashSet<>(publicSet);
        conflicts.retainAll(userSet);

        if (!conflicts.isEmpty()) {
            throw new IllegalArgumentException(
                    "Endpoint conflicts detected between publicEndpoints and userEndpoints: " + conflicts
            );
        }

        log.info("Security endpoints validation passed. Public: {}, User: {}",
                publicEndpoints.size(), userEndpoints.size());
    }
}
