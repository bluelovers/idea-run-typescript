package io.plugin.tsnode.execution

//import com.intellij.execution.runners.DefaultProgramRunner
import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.javascript.debugger.execution.DebuggableProgramRunner
import io.plugin.tsnode.lib.TsLog

class TsProgramRunner : DebuggableProgramRunner()
{
	override fun getRunnerId() = javaClass.simpleName

	override fun canRun(executorId: String, profile: RunProfile): Boolean
	{
		val bool = DefaultRunExecutor.EXECUTOR_ID == executorId && profile is TsRunConfiguration

		TsLog(javaClass).info("[canRun] $bool $executorId $profile")

		return bool
	}
}
