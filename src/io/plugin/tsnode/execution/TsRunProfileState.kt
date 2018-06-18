package io.plugin.tsnode.execution

import com.intellij.execution.Executor
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.KillableColoredProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.project.Project
import com.intellij.util.execution.ParametersListUtil
import org.apache.commons.lang.StringUtils
import java.io.File
import java.nio.file.Paths

class TsRunProfileState(private var project: Project,
	private var runConfig: TsRunConfiguration,
	private var executor: Executor,
	environment: ExecutionEnvironment) : CommandLineState(environment)
{

	override fun startProcess(): ProcessHandler
	{
		val runSettings = runConfig.runSettings
		val commandLine = GeneralCommandLine()

		if (StringUtils.isBlank(runSettings.workingDirectory))
		{
			commandLine.withWorkDirectory(project.baseDir.path)
		}
		else
		{
			commandLine.withWorkDirectory(runSettings.workingDirectory)
		}

		commandLine.exePath = runConfig.getInterpreterSystemDependentPath()

		runSettings.envData.configureCommandLine(commandLine, true)

		val nodeOptionsList = ParametersListUtil.parse(runSettings.interpreterOptions.trim())
		commandLine.addParameters(nodeOptionsList)

		commandLine.addParameter(tsnodePath(runConfig))

		val typescriptOptionsList = ParametersListUtil.parse(runSettings.extraTypeScriptOptions.trim())
		commandLine.addParameters(typescriptOptionsList)

		if (!StringUtils.isBlank(runSettings.typescriptConfigFile))
		{
			commandLine.addParameter("--project ${runSettings.typescriptConfigFile}")
		}

		if (!StringUtils.isBlank(runSettings.scriptName))
		{
			commandLine.addParameter(runSettings.scriptName)

			if (!StringUtils.isBlank(runSettings.programParameters))
			{
				commandLine.addParameter(runSettings.programParameters)
			}
		}

		val processHandler = KillableColoredProcessHandler(commandLine)
		ProcessTerminatedListener.attach(processHandler)
		return processHandler
	}

	/*
	override fun createConsole(executor: Executor): ConsoleView?
	{
		val props = TypeScriptConsoleProperties(runConfig, this.executor)
		return SMTestRunnerConnectionUtil.createConsole("TypeScript", props)
	}
	*/

	private fun tsnodePath(runConfig: TsRunConfiguration): String
	{
		val typescriptPath = Paths.get(runConfig.selectedTsNodePackage().systemDependentPath)
			.resolve("""dist${File.separatorChar}bin.js""")
		return typescriptPath.toAbsolutePath().toString()
	}

}
