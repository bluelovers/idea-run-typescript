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
import com.intellij.openapi.util.JDOMExternalizerUtil
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

	var envs2: MutableMap<String, String> = mutableMapOf()

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
	abstract fun setScriptName(value: String?)

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
		return envs2
		//return runSettings.envData.envs
	}

	override fun setEnvs(value: Map<String, String>)
	{
		//runSettings.envData.envs?.clear()

		LOG.info("[setEnvs] $value ")

		//envs2 = value

		//envs2.clear()
		//envs2.putAll(value)

		envs2 = value.toMutableMap()

		//runSettings.envData.envs.putAll(value)
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

	/**
	 * 讀取設定檔 .idea/workspace.xml
	 */
	@Throws(InvalidDataException::class)
	override fun readExternal(element: Element)
	{
		LOG.info("[readExternal:1] $element ${element.name} ${element.attributes}")

		super<AbstractRunConfiguration>.readExternal(element)

		//var mapenv: MutableMap<String, String> = mutableMapOf()

		EnvironmentVariablesComponent.readExternal(element, envs2)

		//setEnvs(mapenv)

		configurationModule.readExternal(element)

		val interpreterRef_referenceName = JDOMExternalizerUtil.readField(element, "interpreterRef") ?: "";

		if (interpreterRef_referenceName != "")
		{
			// 讀取 node js bin
			runSettings.interpreterRef = NodeJsInterpreterRef.create(interpreterRef_referenceName);
		}

		setScriptName(JDOMExternalizerUtil.readField(element, "scriptName"))
		setWorkingDirectory(JDOMExternalizerUtil.readField(element, "workingDirectory"))

		runSettings.interpreterOptions = JDOMExternalizerUtil.readField(element, "interpreterOptions") ?: ""

		runSettings.tsconfigFile = JDOMExternalizerUtil.readField(element, "tsconfigFile") ?: ""

		runSettings.extraTypeScriptOptions = JDOMExternalizerUtil.readField(element, "extraTypeScriptOptions") ?: ""

		runSettings.programParameters = JDOMExternalizerUtil.readField(element, "programParameters") ?: ""

		LOG.info("[readExternal:2] $element ${element.name} ${element.attributes}")

		LOG.info(envs.toString())
	}

	/**
	 * 將設定寫入 .idea/workspace.xml
	 */
	@Throws(WriteExternalException::class)
	override fun writeExternal(element: Element)
	{
		LOG.info("[writeExternal:1] $element ${element.name} ${element.attributes}")

		super<AbstractRunConfiguration>.writeExternal(element)

		EnvironmentVariablesComponent.writeExternal(element, envs2)

		//runSettings.envData.writeExternal(element)

		JDOMExternalizerUtil.writeField(element, "interpreterRef", getInterpreterRef().referenceName)

		JDOMExternalizerUtil.writeField(element, "interpreterOptions", getInterpreterOptions())

		JDOMExternalizerUtil.writeField(element, "workingDirectory", getWorkingDirectory())

		JDOMExternalizerUtil.writeField(element, "tsconfigFile", runSettings.tsconfigFile)

		JDOMExternalizerUtil.writeField(element, "extraTypeScriptOptions", runSettings.extraTypeScriptOptions)

		JDOMExternalizerUtil.writeField(element, "scriptName", getScriptName())

		JDOMExternalizerUtil.writeField(element, "programParameters", runSettings.programParameters)

		configurationModule.writeExternal(element)

		LOG.info("""[writeExternal:2] $element ${element.name}
${element.attributes}
${element.children}
""".trimMargin())

		LOG.info(envs.toString())
	}

}
