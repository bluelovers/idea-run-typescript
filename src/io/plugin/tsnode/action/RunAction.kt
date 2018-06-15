package io.plugin.tsnode.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.diagnostic.Logger
import icons.TypeScriptIcons
import io.plugin.tsnode.log.LogPlugin

class RunAction : AnAction(TypeScriptIcons.TypeScript)
{
	val logger2 = Logger.getInstance(javaClass)

	override fun actionPerformed(event: AnActionEvent)
	{
		val project = event.getData(PlatformDataKeys.PROJECT)
		//val project = event.project
		val virtualFile = event.getData(DataKeys.VIRTUAL_FILE)

		logger2.info("[tsnode][actionPerformed]")

		LogPlugin.logger.info("[actionPerformed]" + project.toString())
		LogPlugin.logger.info("[actionPerformed]" + virtualFile.toString())
	}

	/*
	override fun update(event: AnActionEvent?)
	{
		val project = event!!.getData(PlatformDataKeys.PROJECT)
		val virtualFile = event.getData(DataKeys.VIRTUAL_FILE)

		logger2.info("[tsnode][update]" + project.toString())

		LogPlugin.logger.info("[update]" + virtualFile.toString())

		if (project != null && virtualFile != null && NodeJsExecution.executable(project, virtualFile))
		{
			event.presentation.isEnabledAndVisible = true
			event.presentation.setText("Run '" + virtualFile.name + "'")

			//event.presentation.icon = TypeScriptIcons.TypeScript
		}
		else
		{
			event.presentation.isEnabledAndVisible = false
		}
	}
	*/

}
