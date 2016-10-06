package com.gradle.scans.lint.rule.java

import com.netflix.nebula.lint.rule.GradleLintRule
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.MethodCallExpression

class BuildScanRule extends GradleLintRule {
    String description = 'build-scan plugin should be applied'

    @Override
    void visitApplyPlugin(MethodCallExpression call, String plugin) {
        bookmark(plugin, call)
        if (!bookmark('firstApplyPlugin')) {
            bookmark('firstApplyPlugin', call)
        }
    }

    @Override
    protected void visitClassComplete(ClassNode node) {
        if (!bookmark('com.gradle.build-scan')) {
            def violation = addBuildLintViolation("""build-scan plugin is not applied. Go to: https://scans.gradle.com/get-started""")
            def firstApplyPlugin = bookmark('firstApplyPlugin')
            if(firstApplyPlugin)
                violation.insertBefore(firstApplyPlugin, "apply plugin: 'com.gradle.build-scan'")
            else violation.insertAfter(buildFile, 0, "apply plugin: 'com.gradle.build-scan'")
        }
    }
}
