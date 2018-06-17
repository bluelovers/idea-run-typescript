package io.plugin.tsnode.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.vfs.VirtualFile
import icons.TsIcons
import io.plugin.tsnode.execution.TsUtil
import javax.swing.Icon

public abstract class TsAction(icon: Icon = TsIcons.TypeScript): AnAction(icon)
{
	public val LOG = Logger.getInstance(javaClass)

	protected abstract val _debug: Boolean

	override fun actionPerformed(event: AnActionEvent)
	{
		LOG.info("""[actionPerformed]""")
		/*
		LOG.info("""[actionPerformed]
event.inputEvent.isAltDown: ${event.inputEvent.isAltDown.toString()}
event.inputEvent.isMetaDown: ${event.inputEvent.isMetaDown.toString()}
event.inputEvent.isConsumed: ${event.inputEvent.isConsumed.toString()}
event.inputEvent.modifiers: ${event.inputEvent.modifiers.toString()}
""")
		*/

		val project = event.project
		val virtualFile = event.getData(DataKeys.VIRTUAL_FILE)
		if (project == null || virtualFile == null) return
		TsUtil.execute(project, virtualFile, isDebugAction())
	}

	override fun update(event: AnActionEvent)
	{
		LOG.info("""[update]""")

		val virtualFile = event.getData(DataKeys.VIRTUAL_FILE) as VirtualFile

		if (TsUtil.isTypeScript(virtualFile))
		{
			event.presentation.isEnabledAndVisible = true
			event.presentation.text = _getText(virtualFile)
		}
		else
		{
			event.presentation.isVisible = false
		}
	}

	protected fun isDebugAction(): Boolean
	{
		return _debug
	}

	protected fun _getText(virtualFile: VirtualFile): String
	{
		val prefix: String

		if (isDebugAction())
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
