package io.plugin.tsnode.lib.cmd

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.PtyCommandLine
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.javascript.debugger.CommandLineDebugConfigurator
import com.intellij.javascript.nodejs.NodeCommandLineUtil
import com.intellij.javascript.nodejs.NodeCommandLineUtil.shouldUseTerminalConsole
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.registry.Registry
import com.intellij.openapi.util.text.StringUtil
import com.intellij.terminal.TerminalExecutionConsole
import com.intellij.util.ThrowableConsumer
import io.plugin.tsnode.execution.TsRunConfiguration
import io.plugin.tsnode.execution.TsRunSettings
import io.plugin.tsnode.execution.TsUtil
import io.plugin.tsnode.lib.TsLog
import java.nio.charset.Charset

object MyNodeCommandLineUtil
{
	val LOG = TsLog(javaClass)

	fun createCommandLine(runConfig: TsRunConfiguration?, project: Project?): GeneralCommandLine
	{
		var usePtyWithTerminalConsole: Boolean? = null

		if (runConfig != null)
		{
			/**
			 * allow set env var nodejs.console.use.terminal=true/false for enable or disable
			 */
			val v = runConfig.runSettings.envData.envs.get("NODEJS_CONSOLE_USE_TERMINAL")

			if (!TsUtil.isEmptyOrSpacesOrNull(v))
			{
				usePtyWithTerminalConsole = v.toString().toBoolean()
			}
		}

		if (usePtyWithTerminalConsole == null)
		{
			/**
			 * use idea Registry nodejs.console.use.terminal
			 */
			usePtyWithTerminalConsole = Registry.`is`("nodejs.console.use.terminal")
		}

		var commandLine = NodeCommandLineUtil.createCommandLine(usePtyWithTerminalConsole)

		if (NodeCommandLineUtil.isTerminalCommandLine(commandLine))
		{
			// do something
			//(commandLine as PtyCommandLine).withConsoleMode(true);
		}
		else
		{
			// do something
		}

		return commandLine
	}

	fun createCommandLine(): GeneralCommandLine
	{
		return createCommandLine(null, null)
	}

	fun createCommandLine(runConfig: TsRunConfiguration?): GeneralCommandLine
	{
		return createCommandLine(runConfig, null)
	}

	fun createCommandLine(project: Project?): GeneralCommandLine
	{
		return createCommandLine(null, project)
	}

	fun configureDebugConfigurator(commandLine: GeneralCommandLine, debugConfigurator: CommandLineDebugConfigurator?)
	{
		if (debugConfigurator !== null) {
			NodeCommandLineUtil.configureCommandLine(commandLine, debugConfigurator, ThrowableConsumer { })
		}
	}

	fun configureWorkDirectory(commandLine: GeneralCommandLine, runSettings: TsRunSettings, project: Project)
	{
		if (StringUtil.isEmptyOrSpaces(runSettings.workingDirectory))
		{
			commandLine.withWorkDirectory(project.basePath)
		}
		else
		{
			commandLine.withWorkDirectory(runSettings.workingDirectory)
		}
	}

	fun configureEnvironment(commandLine: GeneralCommandLine, envs: Map<String, String>?)
	{
		if (envs !== null) {
			commandLine.withEnvironment(envs)
		}

		val env = commandLine.environment;

		/**
		 * same as node.js run add FORCE_COLOR=true
		 */
		env.putIfAbsent("FORCE_COLOR", "3")

		NodeCommandLineUtil.configureUsefulEnvironment(commandLine)

		/*
		env.putIfAbsent("COLORTERM", "true")
		env.putIfAbsent("FORCE_COLOR", "true")
		env.putIfAbsent("npm_config_color", "always")
		env.putIfAbsent("MOCHA_COLORS", "1")
		 */
	}

	fun configureCharset(commandLine: GeneralCommandLine)
	{
		val charset = commandLine.charset.toString()
		if (charset == "x-windows-950" || charset == "x-windows-936")
		{
			commandLine.withCharset(Charset.forName("UTF-8"))
		}
	}

	fun createProcessHandler(commandLine: GeneralCommandLine, project: Project?, debugConfigurator: CommandLineDebugConfigurator?): OSProcessHandler
	{
		val processHandler = if (NodeCommandLineUtil.isTerminalCommandLine(commandLine))
		{
			val p = NodeCommandLineUtil.createKillableColoredProcessHandler(commandLine, true)

			p.setShouldKillProcessSoftlyWithWinP(true)

			p
		}
		else
		{
			NodeCommandLineUtil.createProcessHandler(commandLine, true, debugConfigurator)
		}

		configureProcess(processHandler, project)

		return processHandler
	}

	fun createProcessHandler(commandLine: GeneralCommandLine): OSProcessHandler
	{
		return createProcessHandler(commandLine, null, null)
	}

	fun configureProcess(processHandler: OSProcessHandler, project: Project?)
	{
		processHandler.setShouldDestroyProcessRecursively(true);

		if (project != null)
		{
			ProcessTerminatedListener.attach(processHandler, project)
		}
		else
		{
			ProcessTerminatedListener.attach(processHandler)
		}
	}

	fun configureProcess(processHandler: OSProcessHandler)
	{
		return configureProcess(processHandler, null)
	}

}
