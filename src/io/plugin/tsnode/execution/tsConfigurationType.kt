package io.plugin.tsnode.execution

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import icons.tsIcons

class tsConfigurationType : ConfigurationTypeBase("tsRunnerRun", "TypeScript", "TypeScript", tsIcons.TypeScript), DumbAware
{
	init
	{
		addFactory(object : ConfigurationFactory(this)
		{
			override fun createTemplateConfiguration(project: Project) = tsRunConfiguration(project, this, "TypeScript")
			override fun isConfigurationSingletonByDefault() = true
			override fun canConfigurationBeSingleton() = false
		})
	}

	companion object
	{
		fun getInstance(): tsConfigurationType
		{
			return Holder.INSTANCE
		}
	}

	private object Holder
	{
		val INSTANCE = ConfigurationTypeUtil.findConfigurationType<tsConfigurationType>(tsConfigurationType::class.java)
	}
}
