// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget

import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition

private const val ROOT_PACKAGE = "cat.mvmike.minimalcalendarwidget"

private const val DOMAIN = "$ROOT_PACKAGE.domain.."
private const val APPLICATION = "$ROOT_PACKAGE.application.."
private const val INFRASTRUCTURE_ACTIVITY = "$ROOT_PACKAGE.infrastructure.activity.."
private const val INFRASTRUCTURE_FRAGMENT = "$ROOT_PACKAGE.infrastructure.fragment.."
private const val INFRASTRUCTURE_PREFERENCE = "$ROOT_PACKAGE.infrastructure.preference.."
private const val INFRASTRUCTURE_RECEIVER = "$ROOT_PACKAGE.infrastructure.receiver.."
private const val INFRASTRUCTURE_RESOLVER = "$ROOT_PACKAGE.infrastructure.resolver.."

// https://www.archunit.org/userguide/html/000_Index.html
@AnalyzeClasses(
    packagesOf = [Widget::class]
)
internal class HexArchTest {

    @ArchTest
    val domainShouldNotDependOnApplication: ArchRule = ArchRuleDefinition
        .noClasses()
        .that()
        .resideInAPackage(DOMAIN)
        .should()
        .dependOnClassesThat()
        .resideInAPackage(APPLICATION)

    @ArchTest
    val domainShouldNotDependOnUiInfrastructure: ArchRule = ArchRuleDefinition
        .noClasses()
        .that()
        .resideInAPackage(DOMAIN)
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage(
            INFRASTRUCTURE_ACTIVITY,
            INFRASTRUCTURE_FRAGMENT,
            INFRASTRUCTURE_PREFERENCE,
            INFRASTRUCTURE_RECEIVER
        )

    @ArchTest
    val domainShouldNotDependOnAndroidUiFrameworks: ArchRule = ArchRuleDefinition
        .noClasses()
        .that()
        .resideInAPackage(DOMAIN)
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage(
            "androidx.appcompat..",
            "androidx.fragment..",
            "androidx.preference.."
        )

    @ArchTest
    val applicationShouldOnlyDependOnActivityUiInfrastructure: ArchRule = ArchRuleDefinition
        .noClasses()
        .that()
        .resideInAPackage(APPLICATION)
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage(
            INFRASTRUCTURE_FRAGMENT,
            INFRASTRUCTURE_PREFERENCE,
            INFRASTRUCTURE_RECEIVER
        )

    @ArchTest
    val resolverInfrastructureShouldNotDependOnApplication: ArchRule = ArchRuleDefinition
        .noClasses()
        .that()
        .resideInAPackage(INFRASTRUCTURE_RESOLVER)
        .should()
        .dependOnClassesThat()
        .resideInAPackage(APPLICATION)

    @ArchTest
    val resolverInfrastructureShouldNotDependOnUiInfrastructure: ArchRule = ArchRuleDefinition
        .noClasses()
        .that()
        .resideInAPackage(INFRASTRUCTURE_RESOLVER)
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage(
            INFRASTRUCTURE_ACTIVITY,
            INFRASTRUCTURE_FRAGMENT,
            INFRASTRUCTURE_PREFERENCE,
            INFRASTRUCTURE_RECEIVER
        )

    @ArchTest
    val preferenceInfrastructureShouldOnlyDependOnFrameworks: ArchRule = ArchRuleDefinition
        .classes()
        .that()
        .resideInAPackage(INFRASTRUCTURE_PREFERENCE)
        .should()
        .onlyDependOnClassesThat()
        .resideInAnyPackage(
            INFRASTRUCTURE_PREFERENCE,
            "android..",
            "androidx..",
            "java..",
            "kotlin..",
            "org.jetbrains.annotations.."
        )
}