package io.plugin.tsnode.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.vfs.VirtualFile
import icons.TypeScriptIcons
import io.plugin.tsnode.execution.tsUtil
import javax.swing.Icon

public abstract class tsAction(icon: Icon = TypeScriptIcons.TypeScript): AnAction(icon)
{
	public val LOG = Logger.getInstance(javaClass)

	protected abstract val _debug: Boolean

	override fun update(event: AnActionEvent)
	{
		val virtualFile = event.getData(DataKeys.VIRTUAL_FILE) as VirtualFile

		if (tsUtil.isTypeScript(virtualFile))
		{
			event.presentation.isEnabledAndVisible = true
			event.presentation.text = _getText(virtualFile)
		}
		else
		{
			event.presentation.isVisible = false
		}
	}

	protected fun _getText(virtualFile: VirtualFile): String
	{
		val prefix: String

		if (_debug)
		{
			prefix = "Debug"
		}
		else
		{
			prefix = "Run"
		}

		return "$prefix '${virtualFile.name}'"
	}
}
