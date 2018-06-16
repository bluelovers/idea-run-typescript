package io.plugin.tsnode.execution

import com.intellij.execution.Executor
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.KillableColoredProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.testframework.TestConsoleProperties
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.project.Project
import com.intellij.util.execution.ParametersListUtil
import org.apache.commons.lang.StringUtils
import java.io.File
import java.nio.file.Paths

class TypeScriptRunProfileState(private var project: Project,
	private var runConfig: TypeScriptRunConfiguration,
	private var executor: Executor,
	environment: ExecutionEnvironment) : CommandLineState(environment)
{

	override fun startProcess(): ProcessHandler
	{
		val runSettings = runConfig.typescriptRunSettings
		val interpreter = runSettings.nodeJs.resolveAsLocal(project)
		val commandLine = GeneralCommandLine()

		if (StringUtils.isBlank(runSettings.workingDir))
		{
			commandLine.withWorkDirectory(project.baseDir.path)
		}
		else
		{
			commandLine.withWorkDirectory(runSettings.workingDir)
		}

		commandLine.exePath = interpreter.interpreterSystemDependentPath

		runSettings.envData.configureCommandLine(commandLine, true)

		val nodeOptionsList = ParametersListUtil.parse(runSettings.nodeOptions.trim())
		commandLine.addParameters(nodeOptionsList)

		commandLine.addParameter(tsnodePath(runConfig))

		val typescriptOptionsList = ParametersListUtil.parse(runSettings.extraTypeScriptOptions.trim())
		commandLine.addParameters(typescriptOptionsList)

		if (!StringUtils.isBlank(runSettings.typescriptConfigFile))
		{
			commandLine.addParameter("--project ${runSettings.typescriptConfigFile}")
		}

		if (!StringUtils.isBlank(runSettings.typescriptFile))
		{
			commandLine.addParameter(runSettings.typescriptFile)
		}

		val processHandler = KillableColoredProcessHandler(commandLine)
		ProcessTerminatedListener.attach(processHandler)
		return processHandler
	}

	override fun createConsole(executor: Executor): ConsoleView?
	{
		val props = TypeScriptConsoleProperties(runConfig, this.executor)
		return SMTestRunnerConnectionUtil.createConsole("TypeScript", props)
	}

	private fun tsnodePath(runConfig: TypeScriptRunConfiguration): String
	{
		val typescriptPath = Paths.get(runConfig.selectedTsNodePackage().systemDependentPath)
			.resolve("""dist${File.separatorChar}bin.js""")
		return typescriptPath.toAbsolutePath().toString()
	}

	private class TypeScriptConsoleProperties(configuration: TypeScriptRunConfiguration, executor: Executor) : SMTRunnerConsoleProperties(configuration, "TypeScript", executor)
	{

		init
		{
			isUsePredefinedMessageFilter = true
			setIfUndefined(TestConsoleProperties.HIDE_PASSED_TESTS, false)
			setIfUndefined(TestConsoleProperties.HIDE_IGNORED_TEST, true)
			setIfUndefined(TestConsoleProperties.SCROLL_TO_SOURCE, true)
			setIfUndefined(TestConsoleProperties.SELECT_FIRST_DEFECT, true)
			isIdBasedTestTree = true
			isPrintTestingStartedTime = false
		}
	}

}
