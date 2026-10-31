// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.core.importer.Location
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import java.util.regex.Pattern

private const val ROOT_PACKAGE = "cat.mvmike.minimalcalendarwidget"

private const val DOMAIN = "$ROOT_PACKAGE.domain.."
private const val APPLICATION = "$ROOT_PACKAGE.application.."
private const val INFRASTRUCTURE_ACTIVITY = "$ROOT_PACKAGE.infrastructure.activity.."
private const val INFRASTRUCTURE_FRAGMENT = "$ROOT_PACKAGE.infrastructure.fragment.."
private const val INFRASTRUCTURE_PREFERENCE = "$ROOT_PACKAGE.infrastructure.preference.."
private const val INFRASTRUCTURE_RECEIVER = "$ROOT_PACKAGE.infrastructure.receiver.."
private const val INFRASTRUCTURE_RESOLVER = "$ROOT_PACKAGE.infrastructure.resolver.."

private val NON_ACTIVITY_UI_INFRASTRUCTURE = arrayOf(
    INFRASTRUCTURE_FRAGMENT,
    INFRASTRUCTURE_PREFERENCE,
    INFRASTRUCTURE_RECEIVER
)

private val UI_INFRASTRUCTURE = arrayOf(
    INFRASTRUCTURE_ACTIVITY,
    *NON_ACTIVITY_UI_INFRASTRUCTURE
)

// Android unit test classes are compiled to a *UnitTest directory (e.g. build/tmp/kotlin-classes/debugUnitTest)
private val UNIT_TEST_CLASSES_LOCATION = Pattern.compile(".*/[^/]*UnitTest/.*")

internal class DoNotIncludeUnitTests : ImportOption {
    override fun includes(location: Location) = !location.matches(UNIT_TEST_CLASSES_LOCATION)
}

// https://www.archunit.org/userguide/html/000_Index.html
@AnalyzeClasses(
    packagesOf = [Widget::class],
    importOptions = [DoNotIncludeUnitTests::class]
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
        .resideInAnyPackage(*UI_INFRASTRUCTURE)

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
    val applicationShouldNotDependOnNonActivityUiInfrastructure: ArchRule = ArchRuleDefinition
        .noClasses()
        .that()
        .resideInAPackage(APPLICATION)
        .should()
        .dependOnClassesThat()
        .resideInAnyPackage(*NON_ACTIVITY_UI_INFRASTRUCTURE)

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
        .resideInAnyPackage(*UI_INFRASTRUCTURE)

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