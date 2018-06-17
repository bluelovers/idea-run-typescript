package io.plugin.tsnode.execution

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.openapi.util.Ref
import com.intellij.openapi.util.io.FileUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiUtil
import com.intellij.psi.util.PsiUtilCore


class tsRunConfigurationProducer : RunConfigurationProducer<tsRunConfiguration>(tsConfigurationType.getInstance())
{
	override fun setupConfigurationFromContext(runConfig: tsRunConfiguration, context: ConfigurationContext, sourceElement: Ref<PsiElement>): Boolean
	{
		val location = context.location ?: return false

		val psiElement = location.psiElement
		if (!psiElement.isValid)
		{
			return false
		}

		val psiFile = psiElement.containingFile
		if (psiFile == null)
		{
			return false
		}

		val virtualFile = location.virtualFile ?: return false

		val jsFile = findTestFile(runConfig, context) ?: return false
		sourceElement.set(jsFile)
		runConfig.setGeneratedName()

		runConfig.tsRunSettings.typescriptFile = virtualFile.path

		if (virtualFile.parent != null)
		{
			runConfig.setWorkingDirectory(virtualFile.parent.path)
		}

		return true
	}

	override fun isConfigurationFromContext(runConfig: tsRunConfiguration, context: ConfigurationContext): Boolean
	{
		return findTestFile(runConfig, context) != null
	}

	private fun findTestFile(runConfig: tsRunConfiguration, context: ConfigurationContext): PsiFile?
	{
		val element = context.psiLocation ?: return null
		val currentFile = PsiUtilCore.getVirtualFile(element) ?: return null
		if (currentFile.isDirectory)
		{
			// not sure the right way to run a dir
			return null
		}

		val psiFile = PsiUtil.getPsiFile(runConfig.project, currentFile) ?: return null
		//val psiFile = tryCast(element.containingFile, JSFile::class.java) ?: return null
		//psiFile.testFileType ?: return null

		if (psiFile == null || !FileUtil.pathsEqual(currentFile.path, runConfig.getScriptName()))
		{
			return null
		}

		runConfig.tsRunSettings = runConfig.tsRunSettings.copy(
			typescriptFile = currentFile.path
		)

		return psiFile
	}
}
