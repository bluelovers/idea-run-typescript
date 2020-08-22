package io.plugin.tsnode.execution

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.process.KillableColoredProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.DebuggableRunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.testframework.TestConsoleProperties
import com.intellij.execution.testframework.autotest.ToggleAutoTestAction
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView
import com.intellij.execution.ui.ConsoleView
import com.intellij.javascript.debugger.CommandLineDebugConfigurator
import com.intellij.javascript.nodejs.NodeCommandLineUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.util.execution.ParametersListUtil
import io.plugin.tsnode.execution.TsUtil.tsnodePath
import io.plugin.tsnode.lib.TsLog
import com.intellij.javascript.nodejs.debug.createDebugPortString
import com.intellij.javascript.nodejs.debug.NodeLocalDebugRunProfileState
import com.intellij.javascript.nodejs.debug.NodeLocalDebuggableRunProfileState
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreter
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef
import com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreter
import com.intellij.javascript.protractor.ProtractorConsoleFilter
import com.intellij.javascript.protractor.ProtractorConsoleProperties
import com.intellij.javascript.protractor.ProtractorRunState.FRAMEWORK_NAME
import java.nio.charset.Charset
import java.io.File
import com.intellij.javascript.testFramework.navigation.JSTestLocationProvider
import com.intellij.lang.javascript.buildTools.base.JsbtUtil.foldCommandLine
import com.intellij.openapi.util.Disposer
import com.intellij.util.ThrowableConsumer
import io.plugin.tsnode.execution.TsUtil.isEmptyOrSpacesOrNull
import org.jetbrains.concurrency.Promise
import org.jetbrains.concurrency.resolvedPromise

class TsRunProfileState(protected var project: Project,
	protected var runConfig: TsRunConfiguration,
	protected var executor: Executor,
	environment: ExecutionEnvironment) : CommandLineState(environment), RunProfileState, NodeLocalDebuggableRunProfileState
{
	private var debugConfigurator: CommandLineDebugConfigurator? = null
	val LOG = TsLog(javaClass)

	fun createCommandLine(): GeneralCommandLine
	{
		val runSettings = runConfig.runSettings
		val commandLine = NodeCommandLineUtil.createCommandLine()

		if (this.debugConfigurator !== null) {
			NodeCommandLineUtil.configureCommandLine(commandLine, debugConfigurator, ThrowableConsumer {  })
		}

		if (StringUtil.isEmptyOrSpaces(runSettings.workingDirectory))
		{
			commandLine.withWorkDirectory(project.basePath)
		}
		else
		{
			commandLine.withWorkDirectory(runSettings.workingDirectory)
		}

		commandLine.withEnvironment(this.runConfig.envs)

		/**
		 * same as node.js run add FORCE_COLOR=true
		 */
		commandLine.environment.putIfAbsent("FORCE_COLOR", "3")

		commandLine.exePath = runConfig.getInterpreterSystemDependentPath()

		val nodeOptionsList = ParametersListUtil.parse(runSettings.interpreterOptions.trim())
		commandLine.addParameters(nodeOptionsList)

		val tsnode = tsnodePath(runConfig)

		if (isEmptyOrSpacesOrNull(tsnode))
		{
			commandLine.addParameter("--require")
			commandLine.addParameter("ts-node/register")

			if (!StringUtil.isEmptyOrSpaces(runSettings.tsconfigFile))
			{
				commandLine.environment.putIfAbsent("TS_NODE_PROJECT", runSettings.tsconfigFile)
			}
		}
		else
		{
			commandLine.addParameter(tsnode)

			val typescriptOptionsList = ParametersListUtil.parse(runSettings.extraTypeScriptOptions.trim())
			commandLine.addParameters(typescriptOptionsList)

			if (!StringUtil.isEmptyOrSpaces(runSettings.tsconfigFile))
			{
				commandLine.addParameter("--project")
				commandLine.addParameter(runSettings.tsconfigFile)
			}
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

		return commandLine
	}

	override fun startProcess(): ProcessHandler
	{
		val commandLine = createCommandLine()

		if (commandLine.charset.toString() == "x-windows-950" || commandLine.charset.toString() == "x-windows-936")
		{
			commandLine.charset = Charset.forName("UTF-8")
		}

//		val processHandler = KillableColoredProcessHandler(commandLine)
//		/**
//		 * Sergey Simonchik
//		 * https://youtrack.jetbrains.com/issue/WEB-43796#focus=streamItem-27-3945268.0-0
//		 */
//		processHandler.setShouldKillProcessSoftlyWithWinP(true)

		val processHandler = NodeCommandLineUtil.createProcessHandler(commandLine, true)

		ProcessTerminatedListener.attach(processHandler)

		return processHandler
	}

	override fun execute(configurator: CommandLineDebugConfigurator?): Promise<ExecutionResult> {
		this.debugConfigurator = configurator
		return resolvedPromise(super.execute(executor, TsDebugProgramRunner()));
	}
}
