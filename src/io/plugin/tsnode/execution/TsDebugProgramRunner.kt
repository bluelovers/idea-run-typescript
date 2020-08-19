package io.plugin.tsnode.execution

//import com.intellij.execution.runners.DefaultProgramRunner
//import com.intellij.javascript.debugger.execution.DebuggableProgramRunner
import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.DefaultProgramRunner
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.javascript.debugger.execution.DebuggableProgramRunner
import io.plugin.tsnode.lib.TsLog

class TsDebugProgramRunner : DebuggableProgramRunner()
{
    override fun getRunnerId() = javaClass.simpleName

    val LOG = TsLog(javaClass)

    override fun canRun(executorId: String, profile: RunProfile): Boolean
    {
        val bool = executorId === "Debug" && profile is TsRunConfiguration

        return bool
    }
}
