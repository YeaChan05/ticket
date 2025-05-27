package architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PackageDependencyTest {

    private final JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("org.yechan", "entity", "repository");

    @Test
    @DisplayName("컨트롤러 클래스는 api 패키지에 있어야 함")
    public void controllerClassesShouldBeInControllerPackage() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Controller")
                .should().resideInAPackage("..api..")
                .allowEmptyShould(true)
                .because("컨트롤러는 API 패키지에 위치해야 합니다");

        rule.check(classes);
    }

    @Test
    @DisplayName("리포지토리 클래스는 리포지토리 패키지에 있어야 함")
    public void repositoryClassesShouldBeInRepositoryPackage() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Repository")
                .should().resideInAPackage("..repository..")
                .allowEmptyShould(true)
                .because("리포지토리는 리포지토리 패키지에 위치해야 합니다");

        rule.check(classes);
    }

    @Test
    @DisplayName("엔티티 클래스는 엔티티 패키지에 있어야 함")
    public void entityClassesShouldBeInEntityPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameNotEndingWith("DTO")
                .and().haveSimpleNameNotEndingWith("Request")
                .and().haveSimpleNameNotEndingWith("Response")
                .and().areAnnotatedWith("jakarta.persistence.Entity")
                .should().resideInAPackage("..entity..")
                .allowEmptyShould(true)
                .because("엔티티는 엔티티 패키지에 위치해야 합니다");

        rule.check(classes);
    }

    @Test
    @DisplayName("도메인 패키지 간 의존성 검사")
    public void domainPackagesShouldNotDependOnEachOther() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..entity..")
                .should().dependOnClassesThat().resideInAPackage("..repository..")
                .allowEmptyShould(true)
                .because("엔티티는 리포지토리에 의존하면 안됩니다");

        rule.check(classes);
    }
}
