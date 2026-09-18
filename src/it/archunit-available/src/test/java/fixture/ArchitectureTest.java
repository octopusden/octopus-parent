package fixture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Proves the parent's dependencyManagement entry works end to end: archunit-junit5 is declared with no
 * version, resolves, and a real rule runs. The two rules are the pair the issue proposes as a starting
 * set for any consumer - layer direction and no package cycles.
 */
class ArchitectureTest {

    private final JavaClasses classes = new ClassFileImporter().importPackages("fixture");

    @Test
    void the_api_does_not_depend_on_the_implementation() {
        noClasses().that().resideInAPackage("..api..")
                .should().dependOnClassesThat().resideInAPackage("..internal..")
                .check(classes);
    }

    @Test
    void packages_are_free_of_cycles() {
        slices().matching("fixture.(*)..").should().beFreeOfCycles().check(classes);
    }
}
