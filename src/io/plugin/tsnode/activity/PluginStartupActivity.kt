package io.plugin.tsnode.activity

import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.project.DumbAware
import io.plugin.tsnode.lib.ActivityUtil
import io.plugin.tsnode.lib.PluginUtil
import io.plugin.tsnode.lib.TsLog
import com.intellij.notification.*
import javax.swing.event.HyperlinkEvent

/**
 * A [StartupActivity] to demonstrate how a plugin can execute some code after a project has been opened.
 */
class PluginStartupActivity : StartupActivity, DumbAware
{
	protected val LOG = TsLog(javaClass)
	val VERSION_PROPERTY = "${PluginUtil.PLUGIN_ID}.version"

	override fun runActivity(project: Project)
	{
		//LOG.info("Project startup activity")

		if (project != null)
		{
			ActivityUtil.runLater(project, 3) {
				checkUpdate(project)
			}
		}
	}

	fun checkUpdate(project: Project)
	{
		val plugin = PluginUtil.getThisPlugin()
		LOG.info("${plugin}")
		val version = plugin.version
		val properties: PropertiesComponent = PropertiesComponent.getInstance()
		val lastVersion = properties.getValue(VERSION_PROPERTY)

		if (version == lastVersion)
		{
			return
		}

		showUpdateNotification(project, plugin)
		properties.setValue(VERSION_PROPERTY, version)
	}

	fun showUpdateNotification(project: Project, plugin: IdeaPluginDescriptor)
	{
		val version = plugin.version
		val displayId = "${plugin.name} Plugin Update"
		val title = "${plugin.name} plugin updated to v$version"

		val MILESTONE_URL = plugin.url ?: "https://github.com/bluelovers/idea-run-typescript#README";

		val content = """
			The Project url click <a href="${MILESTONE_URL.format(version)}">here</a>.
			""".trimIndent();

		NotificationGroup(displayId, NotificationDisplayType.BALLOON, true)
			.createNotification(
				title, content, NotificationType.INFORMATION,
				object : NotificationListener.Adapter() {
					private val urlOpeningBehavior = NotificationListener.UrlOpeningListener(false)

					override fun hyperlinkActivated(notification: Notification, hyperlinkEvent: HyperlinkEvent) {
							urlOpeningBehavior.hyperlinkUpdate(notification, hyperlinkEvent)
					}
				}
			)
			.setImportant(true)
			.notify(project)
		;
	}

}
