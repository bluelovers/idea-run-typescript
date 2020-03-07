package io.plugin.tsnode.action

import io.plugin.tsnode.icons.TsIcons

class DebugTsAction : TsAction(TsIcons.TypeScriptDebug)
{
	override val _debug = true
}
