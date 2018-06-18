package io.plugin.tsnode.execution

import com.intellij.execution.Executor
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.KillableColoredProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.util.execution.ParametersListUtil
import io.plugin.tsnode.execution.TsUtil.tsnodePath

class TsRunProfileState(protected var project: Project,
	protected var runConfig: TsRunConfiguration,
	protected var executor: Executor,
	environment: ExecutionEnvironment) : CommandLineState(environment)
{

	fun createCommandLine(): GeneralCommandLine
	{
		val runSettings = runConfig.runSettings
		val commandLine = GeneralCommandLine()

		if (StringUtil.isEmptyOrSpaces(runSettings.workingDirectory))
		{
			commandLine.withWorkDirectory(project.baseDir.path)
		}
		else
		{
			commandLine.withWorkDirectory(runSettings.workingDirectory)
		}

		runSettings.envData.configureCommandLine(commandLine, true)

		commandLine.exePath = runConfig.getInterpreterSystemDependentPath()

		val nodeOptionsList = ParametersListUtil.parse(runSettings.interpreterOptions.trim())
		commandLine.addParameters(nodeOptionsList)

		val tsnode = tsnodePath(runConfig)

		if (StringUtil.isEmptyOrSpaces(tsnode))
		{
			return commandLine
		}

		commandLine.addParameter(tsnode)

		val typescriptOptionsList = ParametersListUtil.parse(runSettings.extraTypeScriptOptions.trim())
		commandLine.addParameters(typescriptOptionsList)

		if (!StringUtil.isEmptyOrSpaces(runSettings.typescriptConfigFile))
		{
			commandLine.addParameter("--project ${runSettings.typescriptConfigFile}")
		}

		if (!StringUtil.isEmptyOrSpaces(runSettings.scriptName))
		{
			commandLine.addParameter(runSettings.scriptName)

			if (!StringUtil.isEmptyOrSpaces(runSettings.programParameters))
			{
				commandLine.addParameter(runSettings.programParameters)
			}
		}

		return commandLine
	}

	override fun startProcess(): ProcessHandler
	{
		val commandLine = createCommandLine()

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

}
