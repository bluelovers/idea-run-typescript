package io.plugin.tsnode.execution

import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.openapi.project.DumbAware
import io.plugin.tsnode.lib.TsData
import io.plugin.tsnode.lib.TsLog

class TsConfigurationType : ConfigurationTypeBase(TsData.RunnerId, TsData.name, TsData.description, TsData.icon)
	, DumbAware
	, ConfigurationType
{

	init
	{
		addFactory(TsConfigurationFactory(this))
	}

	companion object
	{
		val NAME: String = TsData.name

		fun getInstance(): TsConfigurationType
		{
			TsLog(javaClass).info("[getInstance]")

			return ConfigurationTypeUtil.findConfigurationType(TsConfigurationType::class.java)
			//return Holder.INSTANCE
		}
	}
}
