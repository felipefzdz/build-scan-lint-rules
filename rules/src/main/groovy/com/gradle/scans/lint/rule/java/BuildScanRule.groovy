package com.gradle.scans.lint.rule.java

import com.netflix.nebula.lint.rule.GradleLintRule
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.MethodCallExpression

class BuildScanRule extends GradleLintRule {
    String description = 'build-scan plugin should be applied'

    @Override
    void visitApplyPlugin(MethodCallExpression call, String plugin) {
        bookmark(plugin, call)
    }

    @Override
    protected void visitClassComplete(ClassNode node) {
        if (!bookmark('com.gradle.build-scan')) {
            addBuildLintViolation("""build-scan plugin is not applied. Go to: https://scans.gradle.com/get-started""")
        }
    }
}
