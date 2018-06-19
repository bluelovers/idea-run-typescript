package io.plugin.base.runner.inter

import com.intellij.execution.CommonProgramRunConfigurationParameters
import com.intellij.execution.configuration.AbstractRunConfiguration
import com.intellij.execution.configuration.EnvironmentVariablesComponent
import com.intellij.execution.configuration.EnvironmentVariablesData
import com.intellij.execution.configurations.*
import com.intellij.execution.util.ProgramParametersUtil
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef
import com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreter
import com.intellij.javascript.nodejs.util.NodePackage
import com.intellij.openapi.module.Module
import com.intellij.openapi.util.InvalidDataException
import com.intellij.openapi.util.WriteExternalException
import io.plugin.tsnode.execution.TsRunSettings
import io.plugin.tsnode.execution.TsUtil
import io.plugin.tsnode.lib.TsLog
import org.jdom.Element
import java.io.File

abstract class _RunConfiguration<T : TsRunSettings>(runConfigurationModule: RunConfigurationModule, factory: ConfigurationFactory, name: String, var runSettings: T) :
//RunConfigurationBase(project, factory, name),
	AbstractRunConfiguration(name, runConfigurationModule, factory),
	//_NodeJsRunConfigurationParams,
	CommonProgramRunConfigurationParameters,
	//DebuggableRunConfiguration,
	//RunConfigurationWithSettings<T>,
	RunConfiguration,
	RunConfigurationWithSuppressedDefaultDebugAction
{

	val LOG = TsLog(javaClass)

	override fun getValidModules(): Collection<Module>
	{
		TsLog(javaClass).info("[getValidModules] $allModules")

		return allModules
	}

	override fun isCompileBeforeLaunchAddedByDefault(): Boolean
	{
		TsLog(javaClass).info("[isCompileBeforeLaunchAddedByDefault]")

		return false
	}

	override fun excludeCompileBeforeLaunchOption(): Boolean
	{
		TsLog(javaClass).info("[excludeCompileBeforeLaunchOption]")

		return false
	}

	override fun suggestedName(): String?
	{
		val name = File(getScriptName()).name
		LOG.info("[suggestedName] $name")

		return name
	}

	abstract fun getInterpreterRef(): NodeJsInterpreterRef
	abstract fun setInterpreterRef(value: NodeJsInterpreterRef)

	abstract fun getScriptName(): String
	abstract fun setScriptName(value: String)

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
		TsLog(javaClass).info("[checkConfiguration] $this")

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

	fun getInterpreterOptions(): String
	{
		return runSettings.interpreterOptions
	}

	fun setInterpreterOptions(value: String)
	{
		runSettings.interpreterOptions = value
	}

	fun getEnvData(): EnvironmentVariablesData
	{
		return runSettings.envData
	}

	fun setEnvData(value: EnvironmentVariablesData)
	{
		runSettings.envData = value
	}

	override fun getEnvs(): Map<String, String>
	{
		return runSettings.envData.envs
	}

	override fun setEnvs(value: Map<String, String>)
	{
		runSettings.envData.envs
	}

	override fun isPassParentEnvs(): Boolean
	{
		return runSettings.envData.isPassParentEnvs
	}

	override fun getWorkingDirectory(): String
	{
		return runSettings.workingDirectory
	}
	override fun setWorkingDirectory(value: String?)
	{
		runSettings.workingDirectory = if (value != null) value else ""
	}

	@Throws(InvalidDataException::class)
	override fun readExternal(element: Element)
	{
		LOG.info("[readExternal:1] $element ${element.name} ${element.attributes}")

		super<AbstractRunConfiguration>.readExternal(element)

		EnvironmentVariablesComponent.readExternal(element, envs)

		configurationModule.readExternal(element)

		LOG.info("[readExternal:2] $element ${element.name} ${element.attributes}")

		LOG.info(envs.toString())
	}

	@Throws(WriteExternalException::class)
	override fun writeExternal(element: Element)
	{
		LOG.info("[writeExternal:1] $element ${element.name} ${element.attributes}")

		super<AbstractRunConfiguration>.writeExternal(element)

		EnvironmentVariablesComponent.writeExternal(element, envs)

		configurationModule.writeExternal(element)

		LOG.info("[writeExternal:2] $element ${element.name} ${element.attributes}")

		LOG.info(envs.toString())
	}

}
