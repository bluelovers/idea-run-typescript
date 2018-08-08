package io.plugin.tsnode.execution

import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunConfigurationModule
import com.intellij.execution.configurations.RuntimeConfigurationException
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef
import com.intellij.javascript.nodejs.util.NodePackage
import com.intellij.openapi.util.JDOMExternalizerUtil
import com.intellij.openapi.util.text.StringUtil
import io.plugin.base.runner.inter._RunConfiguration
import org.jdom.Element
import java.io.File

class TsRunConfiguration(runConfigurationModule: RunConfigurationModule, factory: TsConfigurationFactory, name: String) :
	_RunConfiguration<TsRunSettings>(runConfigurationModule, factory, name, TsRunSettings())
{
	private var _tsPackage: NodePackage? = null

	override fun getConfigurationEditor() = TsConfigurationEditor(this, project)

	override fun getState(executor: Executor, environment: ExecutionEnvironment) = TsRunProfileState(project, this, executor, environment)

	@Throws(RuntimeConfigurationException::class)
	override fun checkConfiguration()
	{
		super.checkConfiguration()

		if (!StringUtil.isEmptyOrSpaces(runSettings.workingDirectory))
		{
			if (!File(runSettings.workingDirectory).exists())
			{
				throw RuntimeConfigurationException("Working directory not exists")
			}
		}
	}

	fun selectedTsNodePackage(name: String = "ts-node"): NodePackage
	{
		if (_tsPackage == null)
		{
			val pkg = findPreferredPackage(name)
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
		super.readExternal(element)

		val tsnodePackage_referenceName = JDOMExternalizerUtil.readField(element, "tsnodePackage") ?: "";

		if (tsnodePackage_referenceName != "")
		{
			// @todo 讀取 ts-node
			//selectedTsNodePackage(tsnodePackage_referenceName)
		}
	}

	override fun writeExternal(element: Element)
	{
		super.writeExternal(element)

		JDOMExternalizerUtil.writeField(element, "tsnodePackage", selectedTsNodePackage()!!.presentablePath ?: "")

		//LOG.info("[findBinFile] ${selectedTsNodePackage().findBinFile()}")
	}

	/*
	override fun createDebugProcess(p0: InetSocketAddress, p1: XDebugSession, p2: ExecutionResult?, p3: ExecutionEnvironment): XDebugProcess
	{
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
	*/
}
