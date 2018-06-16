package io.plugin.tsnode.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.openapi.vfs.VirtualFile
import icons.TypeScriptIcons
import io.plugin.tsnode.execution.TypeScriptUtil

class DebugAction : AnAction(TypeScriptIcons.Debug)
{
	private fun _getText(virtualFile: VirtualFile): String
	{
		return "Debug '${virtualFile.name}'"
	}

	override fun actionPerformed(event: AnActionEvent)
	{
		val project = event.project
		val virtualFile = event.getData(DataKeys.VIRTUAL_FILE)
		if (project == null || virtualFile == null) return
		TypeScriptUtil.execute(project, virtualFile, true)
	}

	/*
	override fun update(event: AnActionEvent?)
	{
		val project = event!!.project
		val virtualFile = event.getData(DataKeys.VIRTUAL_FILE)
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
