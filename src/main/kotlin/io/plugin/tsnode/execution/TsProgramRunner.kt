package io.plugin.tsnode.execution

//import com.intellij.execution.runners.DefaultProgramRunner
//import com.intellij.javascript.debugger.execution.DebuggableProgramRunner
import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.DefaultProgramRunner
import io.plugin.tsnode.lib.TsLog

class TsProgramRunner : DefaultProgramRunner()
{
	override fun getRunnerId() = javaClass.simpleName

	val LOG = TsLog(javaClass)

	override fun canRun(executorId: String, profile: RunProfile): Boolean
	{
		val bool = DefaultRunExecutor.EXECUTOR_ID == executorId && profile is TsRunConfiguration

		//LOG.info("[canRun] $bool $executorId $profile")

		return bool
	}

	/*
	override fun execute(environment: ExecutionEnvironment, state: RunProfileState): Promise<RunContentDescriptor?>
	{
		LOG.info("[execute] $environment $state")

		return super.execute(environment, state)
	}
	*/
}
