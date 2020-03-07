package io.plugin.tsnode.execution

import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.configurations.RuntimeConfigurationException
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl
import com.intellij.ide.scratch.ScratchUtil
import com.intellij.javascript.nodejs.util.NodePackage
import com.intellij.lang.javascript.TypeScriptFileType
import com.intellij.lang.javascript.TypeScriptJSXFileType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.util.PathUtil
import com.jetbrains.nodejs.run.NodeJsRunConfiguration
import com.jetbrains.nodejs.run.NodeJsRunConfigurationType
import io.plugin.tsnode.lib.TsData
import io.plugin.tsnode.lib.TsLog
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

object TsUtil {
    public val TypeScriptFileType = TsData.FileTypeClassName
    public val FileTypeJSXClassName = TsData.FileTypeJSXClassName

    val LOG = TsLog(javaClass)

    //val logger2 = Logger.getInstance(javaClass)

    private val configurations = HashMap<String, RunnerAndConfigurationSettingsImpl>()

    fun isScratchFileType(psiFile: PsiFile): Boolean {
        return ScratchUtil.isScratch(psiFile.virtualFile)
    }

    fun isScratchFileType(virtualFile: VirtualFile): Boolean {
        return ScratchUtil.isScratch(virtualFile)
    }

    fun isTypeScript(psiFile: PsiFile?): Boolean {
        if (psiFile !== null && !psiFile.isDirectory) {
            if (psiFile.fileType is TypeScriptFileType || psiFile.fileType is TypeScriptJSXFileType) {
                return true
            } else if (isScratchFileType(psiFile)) {
                val ext = PathUtil.getFileExtension(psiFile.originalFile.toString())

                if ("ts" == ext || "tsx" == ext) {
                    return true
                }

                //LOG.info("${psiFile.fileType}")
                //LOG.info("${psiFile.originalFile}")
                //LOG.info("${psiFile.originalFile.fileType}")
                //LOG.info("${psiFile.fileElementType}")
                //LOG.info("${psiFile.originalFile.toString()}")
                //LOG.info("${PathUtil.getFileExtension(psiFile.originalFile.toString())}")

                //LOG.info("[isTypeScript] ${psiFile.name}")
                //LOG.info("[isTypeScript:ext] ${ext}")
            }

            //LOG.info("[isTypeScript:fileType] ${psiFile.fileType}")
        }
        return false
    }

    fun isTypeScript(virtualFile: VirtualFile?): Boolean {
        if (virtualFile !== null && !virtualFile.isDirectory) {
            if (virtualFile.fileType is TypeScriptFileType || virtualFile.fileType is TypeScriptJSXFileType) {
                return true
            } else if (isScratchFileType(virtualFile)) {
                //val ext = PathUtil.getFileExtension(virtualFile.extension.toString())
                val ext = virtualFile.extension.toString()

                if ("ts" == ext || "tsx" == ext) {
                    return true
                }

                //LOG.info("[isTypeScript] ${ext} ${virtualFile.name}")
                //LOG.info("[isTypeScript:ext] ${ext} ${virtualFile.extension}")
            }

            //LOG.info("[isTypeScript:fileType] ${virtualFile.fileType}")
        }
        return false
    }

    fun compatibleFiles(event: AnActionEvent): List<VirtualFile> {
        val files = event.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY).orEmpty()

        //logger2.debug("[tsnode][compatibleFiles]" + files.size)

        return files
            .filter { it -> TsUtil.isTypeScript(it) }
    }

    fun executable(project: Project, virtualFile: VirtualFile): Boolean {
        //TsLog.debug("virtualFile.fileType.name=" + virtualFile.fileType.name)
        //TsLog.debug(virtualFile.fileType.defaultExtension)
        //TsLog.debug(virtualFile.fileType.javaClass.name)

        val fileType = virtualFile.fileType.name

        return TypeScriptFileType == fileType || FileTypeJSXClassName == fileType
        //&& getConfiguration(project, virtualFile) != null
    }

    private fun getConfiguration(project: Project, virtualFile: VirtualFile): RunnerAndConfigurationSettingsImpl? {
        val tsPath = virtualFile.canonicalPath
        val configuration: RunnerAndConfigurationSettingsImpl? = configurations[tsPath]

        if (configuration == null) {
            if (tsPath == null) return null

            if (virtualFile.exists()) {
                val runManager = RunManager.getInstance(project)

                //runManager.addConfiguration(configuration)
            }

        }

        return configuration
    }

    fun execute(project: Project, virtualFile: VirtualFile, debug: Boolean) {
        val runManager = RunManager.getInstance(project)
        val configuration = getConfiguration(project, virtualFile)

        //logger2.debug("[tsnode][execute]" + configuration)

        if (configuration != null) {
            val configurationsList = runManager.getConfigurationsList(TsConfigurationType.getInstance())
            if (!configurationsList.contains(configuration.configuration)) {
                runManager.addConfiguration(configuration)
            }
            if (runManager.selectedConfiguration !== configuration) {
                runManager.selectedConfiguration = configuration
            }
            ProgramRunnerUtil.executeConfiguration(
                configuration,
                if (debug) DefaultDebugExecutor.getDebugExecutorInstance() else DefaultRunExecutor.getRunExecutorInstance()
            )
        }
    }

    @Throws(RuntimeConfigurationException::class)
    fun expectFile(path: String, throwError: Boolean = false, name: String = "path"): Boolean {
        if (StringUtil.isEmptyOrSpaces(path)) {
            if (throwError) {
                throw RuntimeConfigurationException("No $name given.")
            }

            return false
        }

        val file = File(path)

        if (!file.isFile || !file.canRead()) {
            if (throwError) {
                throw RuntimeConfigurationException("$name is invalid or not readable.")
            }

            return false
        }

        return true
    }

    fun NodePackagePathResolve(pkg: NodePackage, path: String): Path {
        return Paths.get(pkg.systemDependentPath)
            .resolve(path)
    }

    fun isEmptyOrSpacesOrNull(target: Any?): Boolean {
        if (target != null) {
            if (target is String) {
                return StringUtil.isEmptyOrSpaces(target)
            }

            return false
        }

        return true
    }

    fun tsnodePath(runConfig: TsRunConfiguration): String {
        val pkg = runConfig.selectedTsNodePackage()
        var file: Path? = null

        if (!isEmptyOrSpacesOrNull(pkg)) {
            // 改進 搜尋 ts-node bin 的方法
            file = pkg?.findBinFile()?.absoluteFile?.toPath()?.toAbsolutePath()

            //LOG.info("""[tsnodePath] ${file} ${pkg}""");

            if (file == null && pkg?.isValid == true) {
                file = TsUtil.NodePackagePathResolve(pkg!!, """dist${File.separatorChar}bin.js""").toAbsolutePath();
            }
        }

        //LOG.info("""[tsnodePath] ${file}""");

        return file?.toString() ?: ""
    }

    fun allNodeJsConfiguration(project: Project): List<RunnerAndConfigurationSettings> {
        val runManager = RunManager.getInstance(project)

        return runManager.getConfigurationSettingsList(NodeJsRunConfigurationType.getInstance())
    }

    fun allTsConfiguration(project: Project): List<RunnerAndConfigurationSettings> {
        val runManager = RunManager.getInstance(project)

        return runManager.getConfigurationSettingsList(TsConfigurationType.getInstance())
    }

    fun getExistedNodeJsConfiguration(
        virtualFile: VirtualFile,
        allSettings: List<RunnerAndConfigurationSettings>
    ): RunnerAndConfigurationSettings? {
        return allSettings.find { x ->
            val config = x.configuration as NodeJsRunConfiguration

            virtualFile.canonicalPath.equals(config.inputPath)
        }
    }

    fun getExistedTsConfiguration(
        virtualFile: VirtualFile,
        allSettings: List<RunnerAndConfigurationSettings>
    ): RunnerAndConfigurationSettings? {
        return allSettings.find { x ->
            val config = x.configuration as TsRunConfiguration

            virtualFile.canonicalPath.equals(config.getScriptName())
        }
    }

}
