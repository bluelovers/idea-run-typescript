package io.plugin.tsnode.execution

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.lang.javascript.TypeScriptFileType
import com.intellij.openapi.util.Ref
import com.intellij.openapi.util.io.FileUtil
import com.intellij.psi.PsiElement

class TsRunConfigurationProducer : RunConfigurationProducer<TsRunConfiguration>(TsConfigurationType.getInstance())
{
	override fun setupConfigurationFromContext(runConfig: TsRunConfiguration, context: ConfigurationContext, sourceElement: Ref<PsiElement>): Boolean
	{
		val location = context.location ?: return false

		val psiElement = location.psiElement
		if (!psiElement.isValid)
		{
			return false
		}

		val psiFile = psiElement.containingFile
		if (psiFile == null || psiFile !is TypeScriptFileType)
		{
			return false
		}

		val virtualFile = location.virtualFile ?: return false

		sourceElement.set(psiFile)

		runConfig.setName(virtualFile.presentableName)
		runConfig.setScriptName(virtualFile.path)

		if (virtualFile.parent != null)
		{
			runConfig.setWorkingDirectory(virtualFile.parent.path)
		}

		return true
	}

	override fun isConfigurationFromContext(runConfig: TsRunConfiguration, context: ConfigurationContext): Boolean
	{
		val location = context.location ?: return false

		val virtualFile = location.virtualFile

		return virtualFile != null && FileUtil.pathsEqual(virtualFile.path, runConfig.getScriptName())
	}
}
