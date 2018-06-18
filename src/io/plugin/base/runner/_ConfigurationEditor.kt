package io.plugin.base.runner

import com.intellij.execution.configuration.EnvironmentVariablesTextFieldWithBrowseButton
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.util.ui.SwingHelper
import javax.swing.JPanel

public abstract class _ConfigurationEditor<T>(var runConfig: T, var project: Project) : SettingsEditor<T>()
	//, RunConfigurationSettingsEditor
{
	protected abstract val form: JPanel

	protected open val workingDirectoryField = createWorkingDirectoryField()

	protected open val envVars: EnvironmentVariablesTextFieldWithBrowseButton = EnvironmentVariablesTextFieldWithBrowseButton()

	protected fun createWorkingDirectoryField(browseDialogTitle: String = "Working Directory"): TextFieldWithBrowseButton
	{
		val field = TextFieldWithBrowseButton()

		SwingHelper
			.installFileCompletionAndBrowseDialog(project, field, browseDialogTitle,
				FileChooserDescriptorFactory.createSingleFolderDescriptor())

		return field
	}

}
