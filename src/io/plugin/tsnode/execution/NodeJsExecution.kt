package io.plugin.tsnode.execution

import com.google.gson.JsonParser
import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl
import com.intellij.lang.typescript.compiler.TypeScriptCompilerService
import com.intellij.lang.typescript.compiler.TypeScriptCompilerSettings
import com.intellij.lang.typescript.compiler.action.before.TypeScriptCompileBeforeRunTaskProvider
import com.intellij.lang.typescript.tsconfig.TypeScriptConfigService
import com.intellij.lang.typescript.tsconfig.TypeScriptConfigUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.jetbrains.nodejs.run.NodeJsRunConfiguration
import com.jetbrains.nodejs.run.NodeJsRunConfigurationState
import com.jetbrains.nodejs.run.NodeJsRunConfigurationType
import java.io.File
import java.lang.reflect.Method
import java.nio.charset.StandardCharsets
import java.util.*

object NodeJsExecution
{

	private val TypeScriptFileType = "com.intellij.lang.javascript.TypeScriptFileType"

	private val tsconfig_json = "tsconfig.json"

	private val jsonParser = JsonParser()

	private val configurations = HashMap<String, RunnerAndConfigurationSettingsImpl>()

	private val getOptions: Method?

	private val provider = TypeScriptCompileBeforeRunTaskProvider()

	init
	{
		var temp: Method? = null
		try
		{
			temp = NodeJsRunConfiguration::class.java.getDeclaredMethod("getOptions")
			temp!!.isAccessible = true
		}
		catch (e: NoSuchMethodException)
		{
			e.printStackTrace()
		}

		getOptions = temp
	}

	fun resetTsCompilerSettings(project: Project)
	{
		try
		{
			val it = TypeScriptConfigService.Provider.getConfigFiles(project).iterator()
			if (it.hasNext())
			{
				val settings = TypeScriptCompilerSettings.getSettings(project)
				settings.isRecompileOnChanges = true
				settings.setUseConfig(true)
				settings.setUseService(true)
				val future = TypeScriptCompilerService.getDefaultService(project)
					.compileConfigProjectAndGetErrors(it.next())
				ApplicationManager.getApplication().invokeLater {
					try
					{
						future!!.get()
						VirtualFileManager.getInstance().syncRefresh()
					}
					catch (e: Exception)
					{
						e.printStackTrace()
					}
				}
			}
		}
		catch (e: Exception)
		{
			e.printStackTrace()
		}

	}

	fun execute(project: Project, virtualFile: VirtualFile, debug: Boolean)
	{
		val runManager = RunManager.getInstance(project)
		val configuration = getConfiguration(project, virtualFile)
		if (configuration != null)
		{
			val configurationsList = runManager.getConfigurationsList(NodeJsRunConfigurationType.getInstance())
			if (!configurationsList.contains(configuration.configuration))
			{
				runManager.addConfiguration(configuration)
			}
			if (runManager.selectedConfiguration !== configuration)
			{
				runManager.selectedConfiguration = configuration
			}
			ProgramRunnerUtil.executeConfiguration(configuration,
				if (debug) DefaultDebugExecutor.getDebugExecutorInstance() else DefaultRunExecutor.getRunExecutorInstance())
		}
	}

	fun executable(project: Project, virtualFile: VirtualFile): Boolean
	{
		return TypeScriptFileType == virtualFile.fileType.javaClass.name
			//&& getConfiguration(project, virtualFile) != null
	}

	private fun getConfiguration(project: Project, virtualFile: VirtualFile): RunnerAndConfigurationSettingsImpl?
	{
		val tsPath = virtualFile.canonicalPath
		var configuration: RunnerAndConfigurationSettingsImpl? = configurations[tsPath]
		if (configuration == null)
		{
			if (tsPath == null) return null

			try
			{
				val tsconfig = TypeScriptConfigUtil.getConfigForFile(project, virtualFile)
				if (tsconfig != null)
				{
					val tscofigStr = String(tsconfig.configFile.contentsToByteArray(), StandardCharsets.UTF_8)
					val tsconfigJson = jsonParser.parse(tscofigStr).asJsonObject
					val compilerOptions = tsconfigJson.getAsJsonObject("compilerOptions")

					val tsconfigDir = tsconfig.configDirectory.canonicalPath
					var rootDir = compilerOptions.get("rootDir").asString
					var outDir = compilerOptions.get("outDir").asString
					rootDir = getPath(tsconfigDir, rootDir)
					outDir = getPath(tsconfigDir, outDir)

					if (tsPath.replace("\\\\".toRegex(), "/")
							.indexOf((rootDir + File.separator).replace("\\\\".toRegex(), "/")) == 0)
					{
						val relatedTsPath = tsPath.substring(rootDir.length + 1).replace(".ts", ".js")
						val complieJs = File(outDir + File.separator + relatedTsPath)
						if (complieJs.exists())
						{
							val complieJsName = complieJs.name
							val runManager = RunManager.getInstance(project)
							configuration = runManager.createConfiguration(virtualFile.name,
								NodeJsRunConfigurationType.getInstance().factory) as RunnerAndConfigurationSettingsImpl
							val con = configuration.configuration
							val state = getOptions!!.invoke(con) as NodeJsRunConfigurationState
							state.workingDir = complieJs.parent
							state.pathToJsFile = complieJsName
							runManager.addConfiguration(configuration)
							configurations[tsPath] = configuration
						}
					}
				}
			}
			catch (e: Exception)
			{
				e.printStackTrace()
			}

		}
		return configuration
	}

	private fun getPath(curDirPath: String?, path: String): String
	{
		if (path.startsWith("/")) return path
		try
		{
			return File("$curDirPath/$path").canonicalPath
		}
		catch (e: Exception)
		{
			e.printStackTrace()
		}

		return path
	}

}
