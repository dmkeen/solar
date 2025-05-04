package org.keen.solar;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

public class ApplicationModulesTest {

    @Test
    public void validateApplicationModules() {
        ApplicationModules modules = ApplicationModules.of(SolarApplication.class);
        modules.forEach(System.out::println);
        modules.verify();
    }

}
