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
import io.plugin.tsnode.lib.TsLog

class TsRunProfileState(protected var project: Project,
	protected var runConfig: TsRunConfiguration,
	protected var executor: Executor,
	environment: ExecutionEnvironment) : CommandLineState(environment)
{
	val LOG = TsLog(javaClass)

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

		/**
		 * same as node.js run add FORCE_COLOR=true
		 */
		commandLine.environment.putIfAbsent("FORCE_COLOR", "1")

		commandLine.exePath = runConfig.getInterpreterSystemDependentPath()

		val nodeOptionsList = ParametersListUtil.parse(runSettings.interpreterOptions.trim())
		commandLine.addParameters(nodeOptionsList)

		val tsnode = tsnodePath(runConfig)

		if (!StringUtil.isEmptyOrSpaces(tsnode))
		{
			commandLine.addParameter(tsnode)

			val typescriptOptionsList = ParametersListUtil.parse(runSettings.extraTypeScriptOptions.trim())
			commandLine.addParameters(typescriptOptionsList)

			if (!StringUtil.isEmptyOrSpaces(runSettings.tsconfigFile))
			{
				//commandLine.addParameter("--project ${runSettings.tsconfigFile}")
				commandLine.addParameter("--project")
				commandLine.addParameter(runSettings.tsconfigFile)
			}

			if (!StringUtil.isEmptyOrSpaces(runSettings.scriptName))
			{
				commandLine.addParameter(runSettings.scriptName)

				if (!StringUtil.isEmptyOrSpaces(runSettings.programParameters))
				{
					val programParametersList = ParametersListUtil.parse(runSettings.programParameters.trim())

					commandLine.addParameters(programParametersList)
				}
			}
		}

		LOG.info("[createCommandLine] $commandLine")

		return commandLine
	}

	override fun startProcess(): ProcessHandler
	{
		val commandLine = createCommandLine()

		val processHandler = KillableColoredProcessHandler(commandLine)
		ProcessTerminatedListener.attach(processHandler)

		LOG.info("[startProcess] $processHandler")

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
