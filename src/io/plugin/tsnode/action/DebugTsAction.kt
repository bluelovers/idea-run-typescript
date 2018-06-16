package io.plugin.tsnode.action

import icons.TypeScriptIcons

class DebugTsAction : tsAction(TypeScriptIcons.TypeScriptDebug)
{
	override val _debug = true
}
