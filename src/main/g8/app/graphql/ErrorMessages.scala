package graphql

import sangria.ast.OperationType

object ErrorMessages {

  def unsupportedOperationType(operationType: OperationType): String =
    s"Unsupported operation type: \${operationType.toString.toLowerCase}"
}
