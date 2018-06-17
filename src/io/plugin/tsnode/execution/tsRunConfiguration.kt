package io.plugin.tsnode.execution

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.LocatableConfigurationBase
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreter
import com.intellij.javascript.nodejs.util.NodePackage
import com.intellij.openapi.project.Project

class tsRunConfiguration(project: Project, factory: ConfigurationFactory, name: String) : LocatableConfigurationBase(project, factory, name)
{
	var tsRunSettings = tsRunSettings()
	private var _tsPackage: NodePackage? = null

	override fun getConfigurationEditor() = tsConfigurationEditor(project)

	override fun getState(executor: Executor, environment: ExecutionEnvironment) = tsRunProfileState(project, this, executor, environment)

	fun selectedTsNodePackage(): NodePackage
	{
		if (_tsPackage == null)
		{
			val interpreter = NodeJsLocalInterpreter.tryCast(tsRunSettings.nodeJs.resolve(project))
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
		return tsRunSettings.workingDir
	}

	fun setWorkingDirectory(workingDirectory: String)
	{
		tsRunSettings.workingDir = workingDirectory
	}

	fun getScriptName(): String
	{
		return tsRunSettings.typescriptFile
	}

	fun setScriptName(typescriptFile: String)
	{
		tsRunSettings.typescriptFile = typescriptFile
	}
}
