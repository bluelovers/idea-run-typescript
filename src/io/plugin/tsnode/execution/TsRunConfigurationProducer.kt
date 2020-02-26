package io.plugin.tsnode.execution

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.lang.javascript.TypeScriptFileType
import com.intellij.lang.javascript.TypeScriptJSXFileType
import com.intellij.openapi.util.Ref
import com.intellij.openapi.util.io.FileUtil
import com.intellij.psi.PsiElement
import io.plugin.tsnode.lib.TsLog
import com.intellij.ide.scratch.ScratchFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.PathUtil;
import com.intellij.openapi.project.DumbAware

/**
 * @todo 不知道如何支援 Debug 模式
 */
class TsRunConfigurationProducer: RunConfigurationProducer<TsRunConfiguration>(TsConfigurationType.getInstance()), DumbAware
{
	protected val LOG = TsLog(javaClass)

	override fun setupConfigurationFromContext(runConfig: TsRunConfiguration, context: ConfigurationContext, sourceElement: Ref<PsiElement>): Boolean
	{
		val bool = fun (): Boolean
		{
			val location = context.location ?: return false

			val psiElement = location.psiElement
			if (!psiElement.isValid)
			{
				//LOG.info("$psiElement ${psiElement.isValid}")

				return false
			}

			val psiFile = psiElement.containingFile

			//LOG.info("${psiFile.fileType}")

			if (!TsUtil.isTypeScript(psiFile))
			{
				return false
			}

			val virtualFile = location.virtualFile ?: return false

			sourceElement.set(psiFile)

			runConfig.setName(virtualFile.presentableName)
			runConfig.setScriptName(virtualFile.path)

			if (TsUtil.isScratchFileType(psiFile))
			{
				// make ScratchFile run in project path
			}
			else if (virtualFile.parent != null)
			{
				runConfig.setWorkingDirectory(virtualFile.parent.path)
			}

			val module = context.module
			if (module != null)
			{
				runConfig.setModule(module)
			}

			return true
		}.invoke()

		//LOG.info("[setupConfigurationFromContext] $bool")

		return bool
	}

	override fun isConfigurationFromContext(runConfig: TsRunConfiguration, context: ConfigurationContext): Boolean
	{
		val location = context.location ?: return false

		val virtualFile = location.virtualFile

		val bool = virtualFile != null && FileUtil.pathsEqual(virtualFile.path, runConfig.getScriptName())

		//LOG.info("[isConfigurationFromContext] ${runConfig.name} $bool")

		return bool
	}
}
