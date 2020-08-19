package io.plugin.tsnode.lib

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager

object ActivityUtil {
    fun runLater(project: Project, delayCount: Int, action: () -> Unit) {
        if (project.isDisposed) {
            return
        }

        if (delayCount > 0) {
            ToolWindowManager.getInstance(project).invokeLater(Runnable {
                    this.runLater(project, delayCount - 1, action)
            })
        } else {
            action()
        }
    }
}
