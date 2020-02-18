package io.plugin.tsnode.execution

import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.nodejs.run.NodeJsRunConfiguration
//import com.jetbrains.nodejs.run.NodeJsRunConfigurationState
import com.jetbrains.nodejs.run.NodeJsRunConfigurationType
import io.plugin.tsnode.lib.TsLog

object TsExecutor
{
	val LOG = TsLog(javaClass)

	fun execute(event: AnActionEvent, debug: Boolean)
	{
		val bool = executable(event, debug)

		//LOG.info("[execute] $bool, debug=$debug")

		if (bool)
		{
			run(event, debug)
		}
	}

	fun executable(event: AnActionEvent, debug: Boolean): Boolean
	{
		val project = event.project
		val virtualFile = event.getData(DataKeys.VIRTUAL_FILE)
		val module = event.getData(DataKeys.MODULE) as Module?

		//LOG.info("[execute:executable] debug=$debug")
		//LOG.info("project=$project")
		//LOG.info("virtualFile=$virtualFile")
		//LOG.info("module=$module")

		if (project == null || virtualFile == null || module == null) return false

		return true
	}

	class run(val event: AnActionEvent, val debug: Boolean)
	{
		var runConf: RunnerAndConfigurationSettings? = null

		init
		{
			//LOG.info("[execute:run] debug=$debug")

			val project = event.project as Project
			val virtualFile = event.getData(DataKeys.VIRTUAL_FILE) as VirtualFile
			val module = event.getData(DataKeys.MODULE) as Module

			//val runManager = RunManagerEx.getInstanceEx(project)
			val runManager = RunManager.getInstance(project)

			val settings = runManager.createConfiguration(virtualFile.name,
				NodeJsRunConfigurationType.getInstance().javaClass)

			val configuration = settings.configuration

			val getOptions = NodeJsRunConfiguration::class.java
				.getDeclaredMethod("getOptions")
			getOptions!!.isAccessible = true

//			val state = getOptions
//				.invoke(configuration) as NodeJsRunConfigurationState
//
//			state.workingDir = virtualFile.parent.path
//			state.pathToJsFile = virtualFile.path

			runManager.addConfiguration(settings)

			this.runConf = settings
		}
	}
}
