package com.gradle.scans.lint.rule.java

import com.netflix.nebula.lint.rule.test.AbstractRuleSpec
import spock.lang.Unroll

class BuildScanRuleSpec extends AbstractRuleSpec {

    @Unroll
    def 'only adding plugin #plugin #violationText build-scan rule'() {
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

    @Unroll
    def 'missing build-scan plugin is added when being the script empty is #emptyScript'() {
        setup:
        project.buildFile << "$originalPlugin"

        def rule = new BuildScanRule()
        rule.project = project

        when:
        def actualPlugins = correct(rule)
                .readLines()
                .findAll{ !it.allWhitespace}
                .collect { it.trim() }

        then:
        actualPlugins[0] == expectedFirstPlugin
        emptyScript ? true : actualPlugins[1] == expectedSecondPlugin

        where:
        emptyScript | originalPlugin         | expectedFirstPlugin                     | expectedSecondPlugin
        true        | /''/                   | /apply plugin: 'com.gradle.build-scan'/ | ''
        false       | /apply plugin: 'java'/ | /apply plugin: 'com.gradle.build-scan'/ | /apply plugin: 'java'/
    }
}
