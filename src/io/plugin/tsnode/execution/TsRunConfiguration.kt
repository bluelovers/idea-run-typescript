package io.plugin.tsnode.execution

import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunConfigurationModule
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef
import com.intellij.javascript.nodejs.util.NodePackage
import io.plugin.base.runner.inter._RunConfiguration

class TsRunConfiguration(runConfigurationModule: RunConfigurationModule, factory: TsConfigurationFactory, name: String) :
	_RunConfiguration<TsRunSettings>(runConfigurationModule, factory, name, TsRunSettings())
{
	private var _tsPackage: NodePackage? = null

	override fun getConfigurationEditor() = TsConfigurationEditor(this, project)

	override fun getState(executor: Executor, environment: ExecutionEnvironment) = TsRunProfileState(project, this, executor, environment)

	fun selectedTsNodePackage(): NodePackage
	{
		if (_tsPackage == null)
		{
			val pkg = findPreferredPackage("ts-node")
			_tsPackage = pkg
			return pkg
		}
		return _tsPackage!!
	}

	fun setTypeScriptPackage(nodePackage: NodePackage)
	{
		_tsPackage = nodePackage
	}

	override fun getWorkingDirectory() = runSettings.workingDirectory

	override fun setWorkingDirectory(workingDirectory: String?)
	{
		runSettings.workingDirectory = workingDirectory!!
	}

	override fun getInterpreterRef() = runSettings.interpreterRef

	override fun setInterpreterRef(value: NodeJsInterpreterRef)
	{
		runSettings.interpreterRef = value
	}

	override fun getScriptName() = runSettings.scriptName

	override fun setScriptName(typescriptFile: String)
	{
		runSettings.scriptName = typescriptFile
	}

	override fun getProgramParameters() = runSettings.programParameters
	override fun setProgramParameters(value: String?)
	{
		runSettings.programParameters = value!!
	}
}
