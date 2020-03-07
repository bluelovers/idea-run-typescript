package io.plugin.tsnode.lib

import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId

object PluginUtil
{
	val LOG = TsLog(javaClass)

	const val PLUGIN_ID = "io.plugin.tsnode"

	//val descriptor: IdeaPluginDescriptor = getThisPlugin()
	//val version: String by lazy { descriptor.version }

	fun getThisPlugin(): IdeaPluginDescriptor
	{
		return getPlugin(PluginId.getId(PLUGIN_ID))!!
	}

	/**
	 * make sure support 183 , 193
	 *
	 * https://github.com/JetBrains/intellij-community/blob/30edfba7a578f9856180a13a3ee3bd695f5b5e0b/platform/core-impl/src/com/intellij/ide/plugins/PluginManagerCore.java#L1901
	 */
	fun getPlugin(id: PluginId): IdeaPluginDescriptor?
	{
		if (id != null)
		{
			val list = PluginManagerCore.getPlugins()
			return list.find { plugin ->
				val bool = plugin.getPluginId().equals(id)
				//LOG.info("${bool} ${plugin.getPluginId()}")
				bool
			}
		}
		return null
	}

	fun toVersionParts(ver: String?): IntArray? {
		if (ver == null)
		{
			return null
		}
		val versionParts = ver.split('.').take(2)
		return when (versionParts.size) {
			1 -> intArrayOf(versionParts[0].toInt(), 0)
			2 -> intArrayOf(versionParts[0].toInt(), versionParts[1].toInt())
			else -> throw IllegalStateException("Invalid version number: $this")
		}
	}

}
