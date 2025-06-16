package br.ifsp.demo.suits;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SelectPackages({"br.ifsp.demo.controller", "br.ifsp.demo.repository"})
@SuiteDisplayName("All Integration Tests")
@IncludeTags({"IntegrationTest"})
public class AllIntegrationTests {
}
