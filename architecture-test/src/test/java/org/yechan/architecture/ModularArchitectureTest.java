package org.yechan.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ModularArchitectureTest {

    private JavaClasses classes;

    @BeforeEach
    void setUp() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("org.yechan");
    }

    @Test
    @DisplayName("도메인 모듈 간 순환 의존성 금지")
    public void domainModulesShouldBeFreeOfCycles() {
        SlicesRuleDefinition.slices()
                .matching("org.yechan.(*)..") 
                .should().beFreeOfCycles()
                .check(classes);
    }

    @Test
    @DisplayName("API 계층은 모든 계층에 접근 가능")
    public void apiLayerCanAccessAllLayers() {
        // API 계층은 모든 도메인과 인프라에 접근 가능하므로 제한 없음
        // 단, 역방향 접근은 금지
        noClasses()
                .that().resideInAPackage("org.yechan.domain..")
                .should().dependOnClassesThat().resideInAPackage("org.yechan.api..")
                .allowEmptyShould(true)
                .check(classes);
    }

    @Test
    @DisplayName("도메인 코어는 다른 도메인에 의존하지 않음")
    public void domainCoreShouldNotDependOnOtherDomains() {
        noClasses()
                .that().resideInAPackage("org.yechan.domain.core..")
                .should().dependOnClassesThat().resideInAPackage("org.yechan.domain.show..")
                .orShould().dependOnClassesThat().resideInAPackage("org.yechan.domain.user..")
                .orShould().dependOnClassesThat().resideInAPackage("org.yechan.domain.order..")
                .orShould().dependOnClassesThat().resideInAPackage("org.yechan.domain.payment..")
                .allowEmptyShould(true)
                .check(classes);
    }

    @Test
    @DisplayName("도메인 쇼는 도메인 코어만 의존")
    public void domainShowShouldOnlyDependOnDomainCore() {
        noClasses()
                .that().resideInAPackage("org.yechan.domain.show..")
                .should().dependOnClassesThat().resideInAPackage("org.yechan.domain.user..")
                .orShould().dependOnClassesThat().resideInAPackage("org.yechan.domain.order..")
                .orShould().dependOnClassesThat().resideInAPackage("org.yechan.domain.payment..")
                .allowEmptyShould(true)
                .check(classes);
    }

    @Test
    @DisplayName("도메인 유저는 도메인 코어만 의존")
    public void domainUserShouldOnlyDependOnDomainCore() {
        noClasses()
                .that().resideInAPackage("org.yechan.domain.user..")
                .should().dependOnClassesThat().resideInAPackage("org.yechan.domain.show..")
                .orShould().dependOnClassesThat().resideInAPackage("org.yechan.domain.order..")
                .orShould().dependOnClassesThat().resideInAPackage("org.yechan.domain.payment..")
                .allowEmptyShould(true)
                .check(classes);
    }

    @Test
    @DisplayName("도메인 오더는 허용된 도메인만 의존")
    public void domainOrderShouldOnlyDependOnAllowedDomains() {
        // domain-order는 domain-core, domain-show, domain-user에만 의존 가능
        noClasses()
                .that().resideInAPackage("org.yechan.domain.order..")
                .should().dependOnClassesThat().resideInAPackage("org.yechan.domain.payment..")
                .allowEmptyShould(true)
                .check(classes);
    }

    @Test
    @DisplayName("도메인 결제는 허용된 도메인만 의존")
    public void domainPaymentShouldOnlyDependOnAllowedDomains() {
        // domain-payment는 domain-core, domain-order에만 의존 가능
        noClasses()
                .that().resideInAPackage("org.yechan.domain.payment..")
                .should().dependOnClassesThat().resideInAPackage("org.yechan.domain.show..")
                .orShould().dependOnClassesThat().resideInAPackage("org.yechan.domain.user..")
                .allowEmptyShould(true)
                .check(classes);
    }

    @Test
    @DisplayName("Internal 모듈은 도메인에 의존하지 않음")
    public void internalModulesShouldNotDependOnDomains() {
        noClasses()
                .that().resideInAPackage("org.yechan.internal..")
                .should().dependOnClassesThat().resideInAPackage("org.yechan.domain..")
                .allowEmptyShould(true)
                .check(classes);
    }

    @Test
    @DisplayName("External 모듈은 Internal에 의존하지 않음")
    public void externalShouldNotDependOnInternal() {
        noClasses()
                .that().resideInAPackage("org.yechan.external..")
                .should().dependOnClassesThat().resideInAPackage("org.yechan.internal..")
                .allowEmptyShould(true)
                .check(classes);
    }

    @Test
    @DisplayName("Common 모듈은 다른 모듈에 의존하지 않음")
    public void commonShouldNotDependOnOtherModules() {
        noClasses()
                .that().resideInAPackage("org.yechan.common..")
                .should().dependOnClassesThat().resideInAPackage("org.yechan.domain..")
                .orShould().dependOnClassesThat().resideInAPackage("org.yechan.internal..")
                .orShould().dependOnClassesThat().resideInAPackage("org.yechan.external..")
                .orShould().dependOnClassesThat().resideInAPackage("org.yechan.api..")
                .allowEmptyShould(true)
                .check(classes);
    }
}
