package io.plugin.tsnode.execution

import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.openapi.project.DumbAware
import io.plugin.tsnode.lib.TsData

class TsConfigurationType : ConfigurationTypeBase(TsData.RunnerId, TsData.name, TsData.description, TsData.icon), DumbAware
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
			return ConfigurationTypeUtil.findConfigurationType(TsConfigurationType::class.java)
			//return Holder.INSTANCE
		}
	}
}
