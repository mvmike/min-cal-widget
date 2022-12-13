// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget

import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition

// https://www.archunit.org/userguide/html/000_Index.html
@AnalyzeClasses(
    packagesOf = [MonthWidget::class]
)
internal class HexArchTest {

    @ArchTest
    val domainShouldNotDependOnApplication = ArchRuleDefinition
        .noClasses()
        .that().resideInAnyPackage("cat.mvmike.minimalcalendarwidget.domain..")
        .should().dependOnClassesThat().resideInAPackage("cat.mvmike.minimalcalendarwidget.application..")!!

    @ArchTest
    val domainShouldNotDependOnActivityOrReceiverInfrastructure = ArchRuleDefinition
        .noClasses()
        .that().resideInAnyPackage("cat.mvmike.minimalcalendarwidget.domain..")
        .should().dependOnClassesThat().resideInAnyPackage(
            "cat.mvmike.minimalcalendarwidget.infrastructure.activity..",
            "cat.mvmike.minimalcalendarwidget.infrastructure.receiver.."
        )!!

    @ArchTest
    val applicationShouldNotDependOnReceiverInfrastructure = ArchRuleDefinition
        .noClasses()
        .that().resideInAnyPackage("cat.mvmike.minimalcalendarwidget.application..")
        .should().dependOnClassesThat().resideInAnyPackage(
            "cat.mvmike.minimalcalendarwidget.infrastructure.receiver.."
        )!!
}
