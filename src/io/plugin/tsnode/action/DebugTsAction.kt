package io.plugin.tsnode.action

import icons.TsIcons

/**
 * @todo make `TsDebugProgramRunner` can be fully replace `DebugTsAction`
 */
class DebugTsAction : TsAction(TsIcons.TypeScriptDebug)
{
	override val _debug = true
}
