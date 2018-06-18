package io.plugin.tsnode.execution

import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.executors.DefaultRunExecutor
//import com.intellij.execution.runners.DefaultProgramRunner
import com.intellij.javascript.debugger.execution.DebuggableProgramRunner
import io.plugin.tsnode.lib.TsData

class TsProgramRunner : DebuggableProgramRunner()
{
	override fun getRunnerId() = TsData.RunnerId

	override fun canRun(executorId: String, profile: RunProfile): Boolean
	{
		return DefaultRunExecutor.EXECUTOR_ID == executorId && profile is TsRunConfiguration
	}
}
