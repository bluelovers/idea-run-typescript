package io.plugin.tsnode.execution

import com.intellij.execution.RunManagerEx
import com.intellij.execution.configuration.ConfigurationFactoryEx
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunConfigurationModule
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configuration.RunConfigurationExtensionBase
import com.intellij.openapi.project.Project
import io.plugin.tsnode.lib.TsLog
import java.util.*

/**
 * @todo ConfigurationFactoryEx is deprecated in java
 */
class TsConfigurationFactory(configurationType: TsConfigurationType) : ConfigurationFactoryEx<TsRunConfiguration>(configurationType)
{
	val LOG = TsLog(javaClass)

	override fun onNewConfigurationCreated(configuration: TsRunConfiguration)
	{
		//LOG.info("[onNewConfigurationCreated] $configuration")

		//the last param has to be false because we do not want a fallback to the template (we're creating it right now) (avoiding a SOE)
		RunManagerEx.getInstanceEx(configuration.project)
			.setBeforeRunTasks(configuration, Collections.emptyList())
	}

	override fun createTemplateConfiguration(project: Project): RunConfiguration
	{
		val configuration = TsRunConfiguration(getModule(project), this, "")

		//LOG.info("[onNewConfigurationCreated] $configuration")

		return configuration
	}

	fun getModule(project: Project): RunConfigurationModule
	{
		return RunConfigurationModule(project)
	}
}
