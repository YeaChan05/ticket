package architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.library.Architectures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LayeredArchitectureTest {

    private final JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("org.yechan", "entity", "repository");

    @Test
    @DisplayName("계층 아키텍처 테스트")
    public void layeredArchitectureShouldBeRespected() {
        Architectures.LayeredArchitecture layeredArchitecture = Architectures.layeredArchitecture()
                .consideringAllDependencies()
                .optionalLayer("Controller").definedBy("org.yechan.api..")
                .optionalLayer("Service").definedBy("org.yechan.service..")
                .optionalLayer("DTO").definedBy("org.yechan.dto..")
                .optionalLayer("Repository").definedBy("..repository..")
                .optionalLayer("Entity").definedBy("..entity..")
                .optionalLayer("Config").definedBy("org.yechan.config..");

        layeredArchitecture
//                .whereLayer("Controller").mayNotBeAccessedByAnyLayer()

                .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
                .whereLayer("Entity").mayOnlyBeAccessedByLayers("Service", "Repository", "DTO")
                .whereLayer("DTO").mayOnlyBeAccessedByLayers("Controller", "Service", "Repository")
                .whereLayer("Config").mayNotBeAccessedByAnyLayer()
                .check(classes);
    }
}
