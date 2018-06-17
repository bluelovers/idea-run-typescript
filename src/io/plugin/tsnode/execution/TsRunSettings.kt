package io.plugin.tsnode.execution

import com.intellij.execution.configuration.EnvironmentVariablesData
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef

data class TsRunSettings(
	var nodeJs: NodeJsInterpreterRef = NodeJsInterpreterRef.createProjectRef(),
	var nodeOptions: String = "",
	var workingDir: String = "",
	var envData: EnvironmentVariablesData = EnvironmentVariablesData.DEFAULT,

	var typescriptFile: String = "",
	var typescriptFileOptions: String = "",

	var extraTypeScriptOptions: String = "",
	var typescriptConfigFile: String = ""
)
