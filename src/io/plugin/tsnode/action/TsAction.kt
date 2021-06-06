package io.plugin.tsnode.action

import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import icons.TsIcons
import javax.swing.Icon
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.CommonDataKeys.VIRTUAL_FILE
import com.intellij.openapi.project.Project
import com.intellij.util.PathUtil
import com.intellij.util.io.directoryContent
import com.jetbrains.nodejs.run.NodeJsRunConfiguration
import com.jetbrains.nodejs.run.NodeJsRunConfigurationType
import io.plugin.tsnode.execution.*
import io.plugin.tsnode.execution.TsUtil.allNodeJsConfiguration
import io.plugin.tsnode.execution.TsUtil.allTsConfiguration
import io.plugin.tsnode.execution.TsUtil.getExistedNodeJsConfiguration
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.Paths

abstract class TsAction(icon: Icon = TsIcons.TypeScript) : AnAction(icon), DumbAware
{
	public val LOG = Logger.getInstance(javaClass)

	protected open val _debug: Boolean = false

	override fun actionPerformed(event: AnActionEvent)
	{
		//LOG.info("""[actionPerformed]""")
		/*
		LOG.info("""[actionPerformed]
event.inputEvent.isAltDown: ${event.inputEvent.isAltDown.toString()}
event.inputEvent.isMetaDown: ${event.inputEvent.isMetaDown.toString()}
event.inputEvent.isConsumed: ${event.inputEvent.isConsumed.toString()}
event.inputEvent.modifiers: ${event.inputEvent.modifiers.toString()}
""")
		*/

		//TsExecutor.execute(event, isDebugAction())

		val project = event.getData(CommonDataKeys.PROJECT) as Project
		val virtualFile = event.getData(CommonDataKeys.VIRTUAL_FILE) as VirtualFile

		val runManager = RunManager.getInstance(event.project!!)
		val type = NodeJsRunConfigurationType.getInstance()
		val settings = runManager.createConfiguration(virtualFile.name, type)

		val nodeJsRunConf = settings.configuration as NodeJsRunConfiguration

		val existingConfig = runManager.getConfigurationsList(TsConfigurationType())
				.find { it -> (it as TsRunConfiguration).runSettings.scriptName == virtualFile.path } as TsRunConfiguration?;

		val typescriptConfig = existingConfig ?: (runManager.createConfiguration(virtualFile.name, TsConfigurationFactory(TsConfigurationType())).configuration as TsRunConfiguration)

		//LOG.info("working directory: ${nodeJsRunConf.workingDirectory}")

		nodeJsRunConf.envs = typescriptConfig.envs;

		if (nodeJsRunConf.workingDirectory.isNullOrEmpty())
			nodeJsRunConf.workingDirectory = if (!virtualFile.parent.path.isEmpty()) virtualFile.parent.path else  project.basePath

		nodeJsRunConf.inputPath = virtualFile.path

		if (nodeJsRunConf.programParameters?.contains("--require ts-node/register") != true)
		{
			nodeJsRunConf.programParameters = "--require ts-node/register " + nodeJsRunConf.programParameters.orEmpty()
		}

		runManager.setTemporaryConfiguration(settings)

		val executor =
			if (isDebugAction()) DefaultDebugExecutor.getDebugExecutorInstance() else DefaultRunExecutor.getRunExecutorInstance()

		ProgramRunnerUtil.executeConfiguration(settings, executor)
	}

	override fun update(event: AnActionEvent)
	{
		//LOG.info("""[update]""")

		val virtualFile = event.getData(VIRTUAL_FILE) as VirtualFile?

		//val conf = getExistedConfiguration(event)
		//LOG.info("[update] ${conf}")

		if (TsUtil.isTypeScript(virtualFile))
		{
			if (!event.presentation.isEnabledAndVisible)
			{
				event.presentation.isEnabledAndVisible = true
				event.presentation.text = _getText(virtualFile!!)
			}
		}
		else if (event.presentation.isEnabledAndVisible)
		{
			event.presentation.isEnabledAndVisible = false
			//event.presentation.isVisible = false
		}
	}

	protected fun isDebugAction(): Boolean
	{
		return _debug
	}

	protected fun _getText(virtualFile: VirtualFile): String
	{
		val _prefix = if (_debug) "Debug" else "Run"
		return "${_prefix} '${virtualFile.name}'"
	}

	private fun getExistedConfiguration(event: AnActionEvent): RunnerAndConfigurationSettings?
	{
		val project = event.getData(CommonDataKeys.PROJECT) as Project
		val virtualFile = event.getData(VIRTUAL_FILE) as VirtualFile

		val allSettings = allNodeJsConfiguration(project)

		return getExistedNodeJsConfiguration(virtualFile, allSettings)
	}
}
