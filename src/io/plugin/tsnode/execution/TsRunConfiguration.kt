package io.plugin.tsnode.execution

import com.intellij.execution.ExecutionException
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.RunManager
import com.intellij.execution.configurations.RunConfigurationModule
import com.intellij.execution.configurations.RuntimeConfigurationException
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.javascript.nodejs.debug.NodeDebugRunConfiguration
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreter
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef
import com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreter
import com.intellij.javascript.nodejs.util.NodePackage
import com.intellij.javascript.nodejs.util.NodePackageField
import com.intellij.javascript.nodejs.util.NodePackageRef
import com.intellij.javascript.nodejs.util.NodePackageRefResolver
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.JDOMExternalizerUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.profiler.model.Transformation
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugSession
import com.jetbrains.nodeJs.NodeJsDebugProcess
import com.jetbrains.nodejs.run.NodeJsRunConfiguration
import com.jetbrains.nodejs.run.NodeJsRunConfigurationType
import io.plugin.base.runner.inter._RunConfiguration
import org.jdom.Element
import org.jetbrains.debugger.DebuggableRunConfiguration
import java.io.File
import java.net.InetSocketAddress

class TsRunConfiguration(runConfigurationModule: RunConfigurationModule, factory: TsConfigurationFactory, name: String) :
	_RunConfiguration<TsRunSettings>(runConfigurationModule, factory, name, TsRunSettings()), NodeDebugRunConfiguration
{
	private var _tsPackage: NodePackage? = null

	override fun getConfigurationEditor() = TsConfigurationEditor(this, project)

	override fun getState(executor: Executor, environment: ExecutionEnvironment) = TsRunProfileState(project, this, executor, environment)

	@Throws(RuntimeConfigurationException::class)
	override fun checkConfiguration()
	{
		super<_RunConfiguration>.checkConfiguration()

		if (!StringUtil.isEmptyOrSpaces(runSettings.workingDirectory))
		{
			if (!File(runSettings.workingDirectory).exists())
			{
				throw RuntimeConfigurationException("Working directory not exists")
			}
		}
	}

	fun selectedTsNodePackage(name: String = "ts-node"): NodePackage?
	{
		if (_tsPackage == null)
		{
			val pkg = findPreferredPackage(listOf(name, "ts-node", "esm-ts-node"))

			_tsPackage = pkg

			return pkg
		}
		return _tsPackage
	}

	fun setTypeScriptPackage(nodePackage: NodePackage)
	{
		_tsPackage = nodePackage
		runSettings.tsnodePackage = nodePackage
	}

	override fun getWorkingDirectory() = runSettings.workingDirectory

	override fun setWorkingDirectory(workingDirectory: String?)
	{
		runSettings.workingDirectory = workingDirectory ?: ""
	}

	override fun getInterpreterRef() = runSettings.interpreterRef

	override fun setInterpreterRef(value: NodeJsInterpreterRef)
	{
		runSettings.interpreterRef = value
	}

	override fun getScriptName() = runSettings.scriptName

	override fun setScriptName(typescriptFile: String?)
	{
		runSettings.scriptName = typescriptFile ?: ""
	}

	override fun getProgramParameters() = runSettings.programParameters
	override fun setProgramParameters(value: String?)
	{
		runSettings.programParameters = value!!
	}

	override fun readExternal(element: Element)
	{
		super<_RunConfiguration>.readExternal(element)

		val tsnodePackage_referenceName = JDOMExternalizerUtil.readField(element, "tsnodePackage") ?: "";

		//LOG.info("[readExternal] ${tsnodePackage_referenceName}")

		if (tsnodePackage_referenceName != "")
		{
			val pkgRef = NodePackageRef.create(tsnodePackage_referenceName);

			//LOG.info("[readExternal:3] ${pkgRef}")
			//LOG.info("[readExternal:4] ${pkgRef.constantPackage}")

			if (pkgRef?.constantPackage?.isValid == true)
			{
				setTypeScriptPackage(pkgRef.constantPackage!!)
			}
		}
	}

	override fun writeExternal(element: Element)
	{
		super<_RunConfiguration>.writeExternal(element)

		if (_tsPackage != null && _tsPackage?.isValid() == true)
		{
			JDOMExternalizerUtil.writeField(element, "tsnodePackage", _tsPackage!!.presentablePath ?: "")

			//configurationModule.writeExternal(element)
		}

		//LOG.info("[findBinFile] ${selectedTsNodePackage().findBinFile()}")
	}

	override fun getInterpreter(): NodeJsInterpreter?
	{
		return this.getInterpreterRef().resolve(project)
	}
}
