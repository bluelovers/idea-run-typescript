package io.plugin.tsnode.execution

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import icons.TypeScriptIcons

class TypeScriptConfigurationType : ConfigurationTypeBase("TypeScriptRunner", "TypeScript", "TypeScript", TypeScriptIcons.TypeScript), DumbAware
{
	init
	{
		addFactory(object : ConfigurationFactory(this)
		{
			override fun createTemplateConfiguration(project: Project) = TypeScriptRunConfiguration(project, this, "TypeScript")
			override fun isConfigurationSingletonByDefault() = true
			override fun canConfigurationBeSingleton() = false
		})
	}

	companion object
	{
		fun getInstance(): TypeScriptConfigurationType
		{
			return Holder.INSTANCE
		}
	}

	private object Holder
	{
		val INSTANCE = ConfigurationTypeUtil.findConfigurationType<TypeScriptConfigurationType>(TypeScriptConfigurationType::class.java)
	}
}
