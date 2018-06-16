package io.plugin.tsnode.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import io.plugin.tsnode.execution.tsUtil
import io.plugin.tsnode.execution.tsUtil.compatibleFiles

class RunTsAction : tsAction()
{
	override val _debug = false

	override fun actionPerformed(event: AnActionEvent)
	{
		LOG.info("7777777")

		//val project = event.getData(PlatformDataKeys.PROJECT) as Project
		val project = event.project
		val virtualFile = event.getData(DataKeys.VIRTUAL_FILE)

		val files = compatibleFiles(event)

		//logger2.info("[tsnode][actionPerformed]")

		LOG.info("[actionPerformed]" + project.toString())
		//LogPlugin.logger.info("[actionPerformed]" + virtualFile.toString())
		if (project == null || virtualFile == null) return

		LOG.info("[tsnode][update]" + tsUtil.executable(project, virtualFile))

		tsUtil.execute(project, virtualFile, false)
	}
}
