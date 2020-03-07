package io.plugin.tsnode.icons

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon
//import icons.NodeJSIcons

object TsIcons
{
	val Run = AllIcons.Actions.Execute
	val Debug = AllIcons.Actions.StartDebugger

	//val Run = IconLoader.getIcon("/actions/execute.png")
	//val Debug = IconLoader.getIcon("/actions/startDebugger.png")
	//val Json = IconLoader.getIcon("/fileTypes/json.png")

	val TypeScript = load("/icons/typescript@16.png")
	val TypeScriptDebug = load("/icons/typescript-debug@16.png")

	//val Npm = JavaScriptLanguageIcons.BuildTools.Npm.Npm_16
	//val Nodejs = JavaScriptLanguageIcons.Nodejs.Nodejs
	//val Mocha = NodeJSIcons.Mocha

	private fun load(path: String): Icon
	{
		return IconLoader.getIcon(path, TsIcons::class.java)
	}
}
