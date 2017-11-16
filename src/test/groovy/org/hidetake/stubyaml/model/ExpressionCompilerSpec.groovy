package org.hidetake.stubyaml.model

import spock.lang.Specification
import spock.lang.Unroll

class ExpressionCompilerSpec extends Specification {
    ExpressionCompiler expressionCompiler = new ExpressionCompiler()

    @Unroll
    def 'Template #template should produce #value on evaluated'() {
        when:
        def compiledExpression = expressionCompiler.compileTemplate(template)

        then:
        compiledExpression?.evaluate([:]) == value

        where:
        template            | value
        null                | null
        '1'                 | '1'
        'foo'               | 'foo'
        'true'              | 'true'
        '${1}'              | 1
        '${true}'           | true
        'No.${3}'           | 'No.3'
        '"""foo'            | '"""foo'
        '${"""foo"""}'      | 'foo'
    }
}
