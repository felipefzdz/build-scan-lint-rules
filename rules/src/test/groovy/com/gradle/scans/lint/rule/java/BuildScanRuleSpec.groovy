package com.gradle.scans.lint.rule.java

import com.netflix.nebula.lint.rule.test.AbstractRuleSpec
import spock.lang.Unroll

class BuildScanRuleSpec extends AbstractRuleSpec {

    @Unroll
    def 'only adding plugin #plugin #violationText build scan rule'() {
        setup:
        project.buildFile << """
           apply plugin: 'nebula.lint'
           apply plugin: $plugin
        """

        def rule = new BuildScanRule()
        rule.project = project

        when:
        def results = runRulesAgainst(rule)

        then:
        results.violates(BuildScanRule) == violates

        where:
        plugin | violates
        /'com.gradle.build-scan'/     | false
        /'nebula.lint'/     | true

        violationText = violates ? 'violates' : 'does not violate'
    }
}
