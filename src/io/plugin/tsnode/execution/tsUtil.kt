package io.plugin.tsnode.execution

import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import io.plugin.tsnode.log.LogPlugin
import java.util.*

object tsUtil
{
	public val TypeScriptFileType = "com.intellij.lang.javascript.TypeScriptFileType"

	val logger2 = Logger.getInstance(javaClass)

	private val configurations = HashMap<String, RunnerAndConfigurationSettingsImpl>()

	fun isTypeScript(virtualFile: VirtualFile): Boolean
	{
		return TypeScriptFileType == virtualFile.fileType.javaClass.name
	}

	fun compatibleFiles(event: AnActionEvent): List<VirtualFile>
	{
		val files = event.getData(DataKeys.VIRTUAL_FILE_ARRAY).orEmpty()

		logger2.debug("[tsnode][compatibleFiles]" + files.size)

		return files
			.filter { it -> tsUtil.isTypeScript(it) }
	}

	fun executable(project: Project, virtualFile: VirtualFile): Boolean
	{
		LogPlugin.logger.debug("virtualFile.fileType.name=" + virtualFile.fileType.name)
		LogPlugin.logger.debug(virtualFile.fileType.defaultExtension)
		LogPlugin.logger.debug(virtualFile.fileType.javaClass.name)

		return TypeScriptFileType == virtualFile.fileType.name
			//&& getConfiguration(project, virtualFile) != null
	}

	private fun getConfiguration(project: Project, virtualFile: VirtualFile): RunnerAndConfigurationSettingsImpl?
	{
		val tsPath = virtualFile.canonicalPath
		val configuration: RunnerAndConfigurationSettingsImpl? = configurations[tsPath]

		if (configuration == null)
		{
			if (tsPath == null) return null

			if (virtualFile.exists())
			{
				val runManager = RunManager.getInstance(project)

				//runManager.addConfiguration(configuration)
			}

		}

		return configuration
	}

	fun execute(project: Project, virtualFile: VirtualFile, debug: Boolean)
	{
		val runManager = RunManager.getInstance(project)
		val configuration = getConfiguration(project, virtualFile)

		logger2.debug("[tsnode][execute]" + configuration)

		if (configuration != null)
		{
			val configurationsList = runManager.getConfigurationsList(tsConfigurationType.getInstance())
			if (!configurationsList.contains(configuration.configuration))
			{
				runManager.addConfiguration(configuration)
			}
			if (runManager.selectedConfiguration !== configuration)
			{
				runManager.selectedConfiguration = configuration
			}
			ProgramRunnerUtil.executeConfiguration(configuration,
				if (debug) DefaultDebugExecutor.getDebugExecutorInstance() else DefaultRunExecutor.getRunExecutorInstance())
		}
	}
}
