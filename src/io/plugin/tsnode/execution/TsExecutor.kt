package io.plugin.tsnode.execution

import com.intellij.execution.configurations.RunConfigurationModule
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

object TsExecutor
{
	fun execute(event: AnActionEvent, debug: Boolean)
	{
		val project = event.project as Project?
		val virtualFile = event.getData(DataKeys.VIRTUAL_FILE) as VirtualFile?
		val module = event.getData(DataKeys.MODULE) as RunConfigurationModule?

		if (project == null || virtualFile == null || module == null) return
	}
}
