package io.plugin.tsnode.runner.inter

import com.intellij.execution.CommonProgramRunConfigurationParameters
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef

interface _NodeJsRunConfigurationParams : CommonProgramRunConfigurationParameters
{
	var interpreterRef: NodeJsInterpreterRef

	var interpreterOptions: String

	var scriptName: String
}
