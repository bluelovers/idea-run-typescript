package io.plugin.tsnode.runner

import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import io.plugin.tsnode.execution.TsRunConfigurationParams
import io.plugin.tsnode.lib.TsForm
import javax.swing.JPanel

public abstract class _ConfigurationEditor<T>(var runConfig: T, var project: Project) : SettingsEditor<T>()
	//, RunConfigurationSettingsEditor
	, TsRunConfigurationParams
{
	protected abstract val form: JPanel

	protected open val workingDirectoryField = TsForm.LazyTextFieldWithBrowseSingleFolderButton("&Working directory:", project)

	override var workingDirectory
		get() = workingDirectoryField.text
		set(value)
		{
			workingDirectoryField.text = value
		}

	//protected open val workingDirectoryField = createWorkingDirectoryField()

	protected open val envVars = TsForm.LazyEnvironmentVariablesTextFieldWithBrowseButton("&Environment variables:")


	override var envs
	get() = envVars.envs
	set(value)
	{
		envVars.envs = value
	}

	override val isPassParentEnvs
		get() = envVars.isPassParentEnvs

	//protected open val envVars: EnvironmentVariablesTextFieldWithBrowseButton = EnvironmentVariablesTextFieldWithBrowseButton()

	/*
	protected fun createWorkingDirectoryField(browseDialogTitle: String = "Working Directory"): TextFieldWithBrowseButton
	{
		val field = TextFieldWithBrowseButton()

		SwingHelper
			.installFileCompletionAndBrowseDialog(project, field, browseDialogTitle,
				FileChooserDescriptorFactory.createSingleFolderDescriptor())

		return field
	}
	*/

}
