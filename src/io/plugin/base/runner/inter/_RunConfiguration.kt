package io.plugin.base.runner.inter

import com.intellij.execution.CommonProgramRunConfigurationParameters
import com.intellij.execution.configuration.AbstractRunConfiguration
import com.intellij.execution.configurations.*
import com.intellij.execution.util.ProgramParametersUtil
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef
import com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreter
import com.intellij.javascript.nodejs.util.NodePackage
import com.intellij.openapi.module.Module
import io.plugin.tsnode.execution.TsUtil
import java.io.File

abstract class _RunConfiguration<T>(runConfigurationModule: RunConfigurationModule, factory: ConfigurationFactory, name: String, var runSettings: T) :
	//RunConfigurationBase(project, factory, name),
	AbstractRunConfiguration(name, runConfigurationModule, factory),
	//_NodeJsRunConfigurationParams,
	CommonProgramRunConfigurationParameters,
	//DebuggableRunConfiguration,
	RunConfiguration,
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
		return File(getScriptName()).name
	}

	abstract fun getInterpreterRef(): NodeJsInterpreterRef
	abstract fun setInterpreterRef(value: NodeJsInterpreterRef)

	abstract fun getScriptName(): String
	abstract fun setScriptName(value: String)

	override abstract fun getWorkingDirectory(): String
	override abstract fun setWorkingDirectory(value: String?)

	fun getInterpreter(): NodeJsLocalInterpreter?
	{
		return NodeJsLocalInterpreter
			.tryCast(getInterpreterRef().resolve(project))
	}

	fun findPreferredPackage(name: String): NodePackage
	{
		val interpreter = getInterpreter()
		val pkg = NodePackage.findPreferredPackage(project, name, interpreter)
		return pkg
	}

	fun getInterpreterSystemDependentPath(): String
	{
		return getInterpreterRef()
			.resolveAsLocal(project)
			.interpreterSystemDependentPath
	}

	@Throws(RuntimeConfigurationException::class)
	override fun checkConfiguration()
	{
		super<AbstractRunConfiguration>.checkConfiguration()

		val module = configurationModule.module
		if (module != null)
		{
			//a missing module will cause a NPE in the check method
			ProgramParametersUtil.checkWorkingDirectoryExist(this, project, module)
		}

		val interpreterPath = getInterpreterSystemDependentPath()
		TsUtil.expectFile(interpreterPath, true, "interpreter")
		TsUtil.expectFile(getScriptName(), true, "script")
	}

}
