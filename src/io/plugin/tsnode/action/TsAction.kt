package io.plugin.tsnode.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import icons.TsIcons
import io.plugin.tsnode.execution.TsExecutor
import io.plugin.tsnode.execution.TsUtil
import javax.swing.Icon
import com.intellij.openapi.project.DumbAware

abstract class TsAction(icon: Icon = TsIcons.TypeScript): AnAction(icon), DumbAware
{
	public val LOG = Logger.getInstance(javaClass)

	protected open val _debug: Boolean = false
	protected open var _prefix: String = ""

	init
	{
		if (StringUtil.isEmpty(_prefix))
		{
			_prefix = if (_debug) "Debug" else "Run"
		}
	}

	override fun actionPerformed(event: AnActionEvent)
	{
		//LOG.info("""[actionPerformed]""")
		/*
		LOG.info("""[actionPerformed]
event.inputEvent.isAltDown: ${event.inputEvent.isAltDown.toString()}
event.inputEvent.isMetaDown: ${event.inputEvent.isMetaDown.toString()}
event.inputEvent.isConsumed: ${event.inputEvent.isConsumed.toString()}
event.inputEvent.modifiers: ${event.inputEvent.modifiers.toString()}
""")
		*/

		TsExecutor.execute(event, isDebugAction())
	}

	override fun update(event: AnActionEvent)
	{
		//LOG.info("""[update]""")

		val virtualFile = event.getData(DataKeys.VIRTUAL_FILE) as VirtualFile

		if (TsUtil.isTypeScript(virtualFile))
		{
			event.presentation.isEnabledAndVisible = true
			event.presentation.text = _getText(virtualFile)
		}
		else
		{
			event.presentation.isEnabledAndVisible = false
			//event.presentation.isVisible = false
		}
	}

	protected fun isDebugAction(): Boolean
	{
		return _debug
	}

	protected fun _getText(virtualFile: VirtualFile): String
	{
		return "${_prefix} '${virtualFile.name}'"
	}
}
