package io.plugin.tsnode.execution

import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.DefaultProgramRunner
import io.plugin.tsnode.lib.TsData

/*
object TypeScriptRunProgramRunner : GenericProgramRunner<RunnerSettings>()
{
	override fun getRunnerId() = "TypeScriptRunnerRun"

	override fun canRun(executorId: String, profile: RunProfile): Boolean
	{
		return DefaultRunExecutor.EXECUTOR_ID == executorId && profile is TsRunConfiguration
	}

	override fun doExecute(state: RunProfileState, environment: ExecutionEnvironment): RunContentDescriptor?
	{
		FileDocumentManager.getInstance().saveAllDocuments()
		val result = state.execute(environment.executor, this) ?: return null
		val builder = RunContentBuilder(result, environment)
		val descriptor = builder.showRunContent(environment.contentToReuse)

		RerunTestsNotification.showRerunNotification(environment.contentToReuse, result.executionConsole)
		RerunTestsAction.register(descriptor)
		return descriptor
	}
}
*/

class tsRunRunner : DefaultProgramRunner()
{
	override fun getRunnerId() = TsData.RunnerId

	override fun canRun(executorId: String, profile: RunProfile): Boolean
	{
		return DefaultRunExecutor.EXECUTOR_ID == executorId && profile is TsRunConfiguration
	}
}
