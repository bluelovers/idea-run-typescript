package io.plugin.tsnode.action

import icons.TsIcons

@Deprecated("do not use this")
class DebugTsAction : TsAction(TsIcons.TypeScriptDebug)
{
	override val _debug = true
}
