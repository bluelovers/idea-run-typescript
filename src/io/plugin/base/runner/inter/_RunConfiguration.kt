package io.plugin.base.runner.inter

import com.intellij.execution.CommonProgramRunConfigurationParameters
import com.intellij.execution.configuration.AbstractRunConfiguration
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfigurationModule
import com.intellij.execution.configurations.RunConfigurationWithSuppressedDefaultDebugAction
import com.intellij.openapi.module.Module
import java.io.File

abstract class _RunConfiguration<T>(val runConfigurationModule: RunConfigurationModule, factory: ConfigurationFactory, name: String, var runSettings: T) :
	//RunConfigurationBase(project, factory, name),
	AbstractRunConfiguration(name, runConfigurationModule, factory),
	//_NodeJsRunConfigurationParams,
	CommonProgramRunConfigurationParameters,
	RunConfigurationWithSuppressedDefaultDebugAction
{

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

	override fun suggestedName(): String?
	{
		return File(getScriptName()!!).name
	}

	abstract fun getScriptName(): String
	abstract fun setScriptName(file: String)

	override abstract fun getWorkingDirectory(): String
	override abstract fun setWorkingDirectory(workingDirectory: String?)

}
