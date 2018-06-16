package io.plugin.tsnode.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import icons.TypeScriptIcons
import io.plugin.tsnode.execution.tsUtil

class DebugAction : Action(TypeScriptIcons.Debug)
{
	override val _debug = true

	override fun actionPerformed(event: AnActionEvent)
	{
		LOG.info("888")

		val project = event.project
		val virtualFile = event.getData(DataKeys.VIRTUAL_FILE)
		if (project == null || virtualFile == null) return
		tsUtil.execute(project, virtualFile, true)
	}

	/*
	override fun update(event: AnActionEvent?)
	{
		val project = event!!.project
		val virtualFile = event.getData(DataKeys.VIRTUAL_FILE)
		if (project != null && virtualFile != null && tsUtil.executable(project, virtualFile))
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
