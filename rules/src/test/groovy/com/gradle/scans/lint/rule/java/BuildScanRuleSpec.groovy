package com.gradle.scans.lint.rule.java

import com.netflix.nebula.lint.rule.test.AbstractRuleSpec
import spock.lang.Unroll

class BuildScanRuleSpec extends AbstractRuleSpec {

    // 'only (old-style) adding plugin #plugin #violationText build-scan rule'
    // 'only (new-style) adding plugin #plugin #violationText build-scan rule'
    // 'build-scan plugin should be added to new style block when both blocks are present'
    // 'build-scan plugin should be added to new style block when no block is present and gradle version is 2.1+'
    // 'build-scan plugin should be added to old style block when no block is present and gradle version is 2.0'
    // 'empty buildscript should be added when adding old style plugins'
    // 'buildscript without the required dependency should be updated'
    // 'buildscript with the required dependency should be untouched'
    // What to do with repositories section? Probably nothing, as we can't really infer if they're using a mirror
    // 'commented build scan section should be added.'

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
