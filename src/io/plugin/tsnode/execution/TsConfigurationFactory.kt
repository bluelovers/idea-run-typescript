package io.plugin.tsnode.execution

import com.intellij.execution.RunManagerEx
import com.intellij.execution.configuration.ConfigurationFactoryEx
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project
import java.util.*

class TsConfigurationFactory(configurationType: TsConfigurationType) : ConfigurationFactoryEx< TsRunConfiguration>(configurationType)
{

	override fun onNewConfigurationCreated(configuration: TsRunConfiguration)
	{
		//the last param has to be false because we do not want a fallback to the template (we're creating it right now) (avoiding a SOE)
		RunManagerEx.getInstanceEx(configuration.project)
			.setBeforeRunTasks(configuration, Collections.emptyList(), false)
	}

	override fun createTemplateConfiguration(project: Project): RunConfiguration
	{
		val configuration = TsRunConfiguration(project, this, "")

		return configuration
	}
}
