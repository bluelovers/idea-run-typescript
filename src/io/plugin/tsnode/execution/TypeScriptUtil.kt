package io.plugin.tsnode.execution

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

object TypeScriptUtil
{
	public val TypeScriptFileType = "com.intellij.lang.javascript.TypeScriptFileType"

	fun executable(project: Project, virtualFile: VirtualFile): Boolean
	{
		return TypeScriptFileType == virtualFile.fileType.name
	}

	fun execute(project: Project, virtualFile: VirtualFile, debug: Boolean)
	{
		/*
		val runManager = RunManager.getInstance(project)
		val configuration = NodeJsExecution.getConfiguration(project, virtualFile)
		if (configuration != null)
		{
			val configurationsList = runManager.getConfigurationsList(NodeJsRunConfigurationType.getInstance())
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
		*/
	}
}
