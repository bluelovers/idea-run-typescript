package io.plugin.tsnode.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.vfs.VirtualFile
import icons.TypeScriptIcons
import io.plugin.tsnode.execution.TypeScriptUtil
import io.plugin.tsnode.execution.TypeScriptUtil.compatibleFiles

class RunAction : AnAction(TypeScriptIcons.TypeScript)
{
	val logger2 = Logger.getInstance(javaClass)

	private fun _getText(virtualFile: VirtualFile): String
	{
		return "Run '${virtualFile.name}'"
	}

	override fun actionPerformed(event: AnActionEvent)
	{
		//val project = event.getData(PlatformDataKeys.PROJECT) as Project
		val project = event.project
		val virtualFile = event.getData(DataKeys.VIRTUAL_FILE)

		val files = compatibleFiles(event)

		//logger2.info("[tsnode][actionPerformed]")

		//LogPlugin.logger.info("[actionPerformed]" + project.toString())
		//LogPlugin.logger.info("[actionPerformed]" + virtualFile.toString())
		if (project == null || virtualFile == null) return

		logger2.info("[tsnode][update]" + TypeScriptUtil.executable(project, virtualFile))

		TypeScriptUtil.execute(project, virtualFile, false)
	}

	/*
	override fun update(event: AnActionEvent?)
	{
		val project = event!!.project
		val virtualFile = event.getData(DataKeys.VIRTUAL_FILE)

		//logger2.info("[tsnode][update]" + project.toString())

		//LogPlugin.logger.info("[update]" + virtualFile.toString())

		if (project != null && virtualFile != null && TypeScriptUtil.executable(project, virtualFile))
		{
			event.presentation.isEnabledAndVisible = true
			event.presentation.setText(_getText(virtualFile))
		}
		else
		{
			event.presentation.isEnabledAndVisible = false
		}
	}
	*/
}
