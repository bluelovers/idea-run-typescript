package io.plugin.base.runner.inter

import com.intellij.execution.CommonProgramRunConfigurationParameters
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef

interface _NodeJsRunConfigurationParams : CommonProgramRunConfigurationParameters
{
	var interpreterPath: NodeJsInterpreterRef

	var interpreterOptions: String

	var scriptName: String
}
