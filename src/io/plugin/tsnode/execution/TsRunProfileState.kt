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

class TsRunProfileState(protected var project: Project,
	protected var runConfig: TsRunConfiguration,
	protected var executor: Executor,
	environment: ExecutionEnvironment) : CommandLineState(environment)
{

	fun createCommandLine(): GeneralCommandLine
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

	protected fun tsnodePath(runConfig: TsRunConfiguration): String
	{
		return TsUtil.NodePackagePathResolve(runConfig.selectedTsNodePackage(), """dist${File.separatorChar}bin.js""")
			.toAbsolutePath()
			.toString()
	}

}
