package io.plugin.tsnode.execution

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import io.plugin.tsnode.lib.TsLog

object TsExecutor
{
	val LOG = TsLog(javaClass)

	fun execute(event: AnActionEvent, debug: Boolean)
	{
		val bool = executable(event, debug)

		LOG.info("[execute] $bool, debug=$debug")

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

		LOG.info("[execute:executable] debug=$debug")
		LOG.info("project=$project")
		LOG.info("virtualFile=$virtualFile")
		LOG.info("module=$module")

		if (project == null || virtualFile == null || module == null) return false

		return true
	}

	fun run(event: AnActionEvent, debug: Boolean)
	{
		LOG.info("[execute:run] debug=$debug")

		val project = event.project as Project
		val virtualFile = event.getData(DataKeys.VIRTUAL_FILE) as VirtualFile
		val module = event.getData(DataKeys.MODULE) as Module
	}
}
