package io.plugin.tsnode.execution

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.LocatableConfigurationBase
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreter
import com.intellij.javascript.nodejs.util.NodePackage
import com.intellij.openapi.project.Project

class TypeScriptRunConfiguration(project: Project, factory: ConfigurationFactory, name: String) : LocatableConfigurationBase(project, factory, name)
{
	var typescriptRunSettings = TypeScriptRunSettings()
	private var _typescriptPackage: NodePackage? = null

	override fun getConfigurationEditor() = TypeScriptConfigurationEditor(project)

	override fun getState(executor: Executor, environment: ExecutionEnvironment) = TypeScriptRunProfileState(project, this, executor, environment)

	fun selectedTsNodePackage(): NodePackage
	{
		if (_typescriptPackage == null)
		{
			val interpreter = NodeJsLocalInterpreter.tryCast(typescriptRunSettings.nodeJs.resolve(project))
			val pkg = NodePackage.findPreferredPackage(project, "ts-node", interpreter)
			_typescriptPackage = pkg
			return pkg
		}
		return _typescriptPackage!!
	}

	fun setTypeScriptPackage(nodePackage: NodePackage)
	{
		_typescriptPackage = nodePackage
	}
}

