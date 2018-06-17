package io.plugin.tsnode.execution

import com.intellij.execution.Executor
import com.intellij.execution.configuration.AbstractRunConfiguration
import com.intellij.execution.configurations.RunConfigurationModule
import com.intellij.execution.configurations.RunConfigurationWithSuppressedDefaultDebugAction
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreter
import com.intellij.javascript.nodejs.util.NodePackage
import com.intellij.openapi.module.Module

class TsRunConfiguration(runConfigurationModule: RunConfigurationModule, factory: TsConfigurationFactory, name: String) :
	//RunConfigurationBase(project, factory, name),
	AbstractRunConfiguration(name, runConfigurationModule, factory),
	//_NodeJsRunConfigurationParams,
	RunConfigurationWithSuppressedDefaultDebugAction
{
	var tsRunSettings = TsRunSettings()
	private var _tsPackage: NodePackage? = null

	override fun getValidModules(): Collection<Module>
	{
		return allModules
	}

	override fun isCompileBeforeLaunchAddedByDefault(): Boolean
	{
		return false
	}

	override fun excludeCompileBeforeLaunchOption(): Boolean
	{
		return false
	}

	override fun getConfigurationEditor() = TsConfigurationEditor(this, project)

	override fun getState(executor: Executor, environment: ExecutionEnvironment) = TsRunProfileState(project, this, executor, environment)

	fun selectedTsNodePackage(): NodePackage
	{
		if (_tsPackage == null)
		{
			val interpreter = NodeJsLocalInterpreter.tryCast(tsRunSettings.interpreterPath.resolve(project))
			val pkg = NodePackage.findPreferredPackage(project, "ts-node", interpreter)
			_tsPackage = pkg
			return pkg
		}
		return _tsPackage!!
	}

	fun setTypeScriptPackage(nodePackage: NodePackage)
	{
		_tsPackage = nodePackage
	}

	fun getWorkingDirectory(): String
	{
		return tsRunSettings.workingDirectory
	}

	fun setWorkingDirectory(workingDirectory: String)
	{
		tsRunSettings.workingDirectory = workingDirectory
	}

	fun getScriptName(): String
	{
		return tsRunSettings.scriptName
	}

	fun setScriptName(typescriptFile: String)
	{
		tsRunSettings.scriptName = typescriptFile
	}
}
