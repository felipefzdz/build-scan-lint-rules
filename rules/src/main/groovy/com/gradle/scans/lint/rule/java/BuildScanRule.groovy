package com.gradle.scans.lint.rule.java

import com.netflix.nebula.lint.rule.GradleLintRule
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.MethodCallExpression

class BuildScanRule extends GradleLintRule {
    String description = 'build-scan plugin should be applied'

    @Override
    void visitApplyPlugin(MethodCallExpression call, String plugin) {
        bookmark(plugin, call)
        bookmark('lastApplyPlugin', call)
    }

    @Override
    protected void visitClassComplete(ClassNode node) {
        if (!bookmark('com.gradle.build-scan')) {
            def violation = addBuildLintViolation("""build-scan plugin is not applied. Go to: https://scans.gradle.com/get-started""")
            if(bookmark('lastApplyPlugin'))
                violation.insertAfter(lastApplyPlugin, "apply plugin: 'build-scan'")
            else violation.insertAfter(buildFile, 0, "apply plugin: 'build-scan'")
        }
    }
}
