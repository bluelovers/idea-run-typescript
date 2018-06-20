package io.plugin.tsnode.execution

/**
 * @author wibotwi
 */
interface TsRunConfigurationParams
{

	var interpreterOptions: String

	var workingDirectory: String

	val isPassParentEnvs: Boolean

	var envs: Map<String, String>

	var scriptName: String

	var programParameters: String

}
