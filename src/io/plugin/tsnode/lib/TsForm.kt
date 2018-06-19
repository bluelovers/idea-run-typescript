package io.plugin.tsnode.lib

import com.intellij.execution.configuration.EnvironmentVariablesData
import com.intellij.execution.configuration.EnvironmentVariablesTextFieldWithBrowseButton
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterField
import com.intellij.javascript.nodejs.util.NodePackage
import com.intellij.javascript.nodejs.util.NodePackageField
import com.intellij.javascript.nodejs.util.NodePackageRef
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.RawCommandLineEditor
import com.intellij.ui.TextAccessor
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.SwingHelper
import javax.swing.JComponent

class TsForm
{
	open class TsFormBuilder: com.intellij.util.ui.FormBuilder()
	{
		fun append(field: Field<*>) = field.appendTo(this) as TsFormBuilder

		fun append(field: JComponent, label: String) = addLabeledComponent(label, field) as TsFormBuilder

		fun append(field: Field<*>, label: String) = this.addLabeledComponent(label, field.field as JComponent) as TsFormBuilder
	}

	interface Field<T>
	{
		val field: T
		val label: String

		fun appendTo(form: FormBuilder) = form.addLabeledComponent(label, field as JComponent)
		fun <F: TsFormBuilder>appendTo(form: F) = form.addLabeledComponent(label, field as JComponent) as F
	}

	open class TextField<Comp: TextAccessor>(override val field: Comp, override val label: String): Field<Comp>
	{
		fun getText() = field.text
		fun setText(value: String)
		{
			field.text = value
		}
	}

	open class NodePackageField<Comp: com.intellij.javascript.nodejs.util.NodePackageField>(override val field: Comp, override val label: String): Field<Comp>
	{
		fun getSelected() = field.selected
		fun setSelected(value: NodePackage)
		{
			field.selected = value
		}

		fun getSelectedRef() = field.selectedRef
		fun setSelectedRef(value: NodePackageRef)
		{
			field.selectedRef = value
		}
	}

	open class EnvironmentVariablesTextFieldWithBrowseButtonField<Comp: com.intellij.execution.configuration.EnvironmentVariablesTextFieldWithBrowseButton>(override val field: Comp, override val label: String): Field<Comp>
	{
		fun isPassParentEnvs() = field.isPassParentEnvs()

		fun getData() = field.data
		fun setData(value: EnvironmentVariablesData)
		{
			field.data = value
		}

		fun getEnvs() = field.envs
		fun setEnvs(value: Map<String, String>)
		{
			field.envs = value
		}
	}

	companion object
	{
		fun LazyTextFieldWithBrowseButton(label: String, fieldFactory: TextFieldWithBrowseButton = TextFieldWithBrowseButton())= TextField(fieldFactory, label)

		fun LazyTextFieldWithBrowseSingleFolderButton(label: String, fieldFactory: TextFieldWithBrowseButton = TextFieldWithBrowseButton())= TextField(fieldFactory, label)

		fun LazyTextFieldWithBrowseSingleFolderButton(label: String, project: Project, browseDialogTitle: String = "Directory", fieldFactory: TextFieldWithBrowseButton = Util.createWorkingDirectoryField(project, browseDialogTitle))= TextField(fieldFactory, label)

		fun LazyRawCommandLineEditor(label: String, fieldFactory: RawCommandLineEditor = com.intellij.ui.RawCommandLineEditor()) = TextField(fieldFactory, label)

		fun LazyNodePackageField(label: String, interpreterField: NodeJsInterpreterField, packageName: String, fieldFactory: com.intellij.javascript.nodejs.util.NodePackageField = NodePackageField(interpreterField, packageName)) = NodePackageField(fieldFactory, label)

		fun LazyNodePackageField(label: String, fieldFactory: com.intellij.javascript.nodejs.util.NodePackageField) = NodePackageField(fieldFactory, label)

		fun LazyEnvironmentVariablesTextFieldWithBrowseButton(label: String, fieldFactory: EnvironmentVariablesTextFieldWithBrowseButton = EnvironmentVariablesTextFieldWithBrowseButton())= TextField(fieldFactory, label)

	}

	object Util
	{
		fun createWorkingDirectoryField(project: Project, browseDialogTitle: String = "Select Path"): TextFieldWithBrowseButton
		{
			val field = TextFieldWithBrowseButton()

			SwingHelper
				.installFileCompletionAndBrowseDialog(project, field, browseDialogTitle,
					FileChooserDescriptorFactory.createSingleFolderDescriptor())

			return field
		}
	}
}
