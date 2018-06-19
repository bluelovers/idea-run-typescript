package io.plugin.tsnode.lib

//import com.intellij.javascript.nodejs.util.NodePackageField
import com.intellij.execution.configuration.EnvironmentVariablesTextFieldWithBrowseButton
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.RawCommandLineEditor
import com.intellij.ui.TextAccessor
import com.intellij.util.ui.ComponentWithEmptyText
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.StatusText
import com.intellij.util.ui.SwingHelper
import javax.swing.JComponent
import javax.swing.JTextField

class TsForm
{
	val LOG = TsLog(javaClass)

	open class TsFormBuilder : com.intellij.util.ui.FormBuilder()
	{
		fun append(field: Field<*>) = field.appendTo(this) as TsFormBuilder

		fun append(field: JComponent, label: String) = addLabeledComponent(label, field) as TsFormBuilder

		fun append(field: Field<*>, label: String) = this.addLabeledComponent(label, field.field as JComponent) as TsFormBuilder

		override fun addLabeledComponent(label: String, field: JComponent) = super.addLabeledComponent(label, field) as TsFormBuilder

		fun addLabeledComponent(label: String, field: Field<*>) = this.addLabeledComponent(label, field.field as JComponent)

		fun addLabeledComponent(field: Field<*>) = this.addLabeledComponent(field.label, field.field as JComponent)

		override fun setAlignLabelOnRight(alignLabelOnRight: Boolean) = super.setAlignLabelOnRight(alignLabelOnRight) as TsFormBuilder
	}

	interface Field<T>
	{
		val field: T
		val label: String

		fun appendTo(form: FormBuilder) = form.addLabeledComponent(label, field as JComponent)
		fun <F : TsFormBuilder> appendTo(form: F) = form.addLabeledComponent(label, field as JComponent) as F

		override abstract fun toString(): String
	}

	open class TextField<Comp : TextAccessor>(override val field: Comp, override val label: String, options: Options? = null) : Field<Comp>
	{
		companion object Options
		{
			var emptyText: String? = null
			var dialogCaption: String? = null
		}

		var text
			get() = this.field.text
			set(value)
			{
				this.field.text = value
			}

		override fun toString() = field.toString()
	}

	open class TextFieldWithBrowseSingleFolderButton<Comp : com.intellij.openapi.ui.TextFieldWithBrowseButton>(
		override val field: Comp
		, override val label: String, options: Options? = null
	) : TextField<Comp>(field, label)
	{
		var defaultFileChooserDescriptor: FileChooserDescriptor? = null
		var defaultProject: Project? = null
		var defaultBrowseDialogTitle: String? = null

		init
		{
			if (defaultProject != null && defaultProject is Project)
			{
				installFileCompletionAndBrowseDialog(defaultProject, defaultBrowseDialogTitle, defaultFileChooserDescriptor)
			}

			if (
				(field is com.intellij.openapi.ui.TextFieldWithBrowseButton)
				|| (field is com.intellij.ui.RawCommandLineEditor)
			)
			{
				val textField: JTextField? = field.textField

				if (textField != null && textField is ComponentWithEmptyText)
				{
					val emptyText = textField.emptyText

					/**
					 * @FIXME 不知道為什麼要寫成這樣才能成功更新文字
					 */
					if (StringUtil.isEmptyOrSpaces(emptyText?.text))
					{
						if (!StringUtil.isEmptyOrSpaces(options?.emptyText))
						{
							emptyText?.text = options?.emptyText as String
						}
						else if (!StringUtil.isEmptyOrSpaces(label))
						{
							emptyText?.text = Util.stripTitle(label)
						}
					}
				}
			}
		}

		fun createFileChooserDescriptor(fn: FileChooserDescriptor?): FileChooserDescriptor
		{
			return fn
				?: defaultFileChooserDescriptor
				?: FileChooserDescriptorFactory.createSingleFolderDescriptor()
		}

		fun installFileCompletionAndBrowseDialog(project: Project? = null, browseDialogTitle: String? = null, fn: com.intellij.openapi.fileChooser.FileChooserDescriptor? = null): TextFieldWithBrowseSingleFolderButton<Comp>
		{
			val project = project
				?: defaultProject

			val browseDialogTitle = browseDialogTitle
				?: defaultBrowseDialogTitle
				?: Util.stripTitle(this.label)

			SwingHelper
				.installFileCompletionAndBrowseDialog(project, this.field, browseDialogTitle,
					createFileChooserDescriptor(fn))

			return this
		}

		val textField
			get() = field.textField

		val emptyText: StatusText?
			get()
			{
				val field = this.field

				if (field.textField != null && field.textField is ComponentWithEmptyText)
				{
					val emptyText = field.textField as ComponentWithEmptyText

					return emptyText?.emptyText
				}

				return null
			}
	}

	open class RawCommandLineEditorField<Comp : RawCommandLineEditor>(override val field: Comp, override val label: String, options: Options? = null) : TextField<Comp>(field, label)
	{
		//val LOG = TsLog(javaClass)

		init
		{
			val text = if (StringUtil.isEmptyOrSpaces(field.dialogCaption!!))
				Util.stripTitle(
					options?.dialogCaption
						?: label
				)
			else field.dialogCaption as String

			field.dialogCaption = text

			/*
			if (textField is ExpandableTextField)
			{
				textField?.putClientProperty("monospaced", false)
			}
			*/

			if (
				(field is com.intellij.openapi.ui.TextFieldWithBrowseButton)
				|| (field is com.intellij.ui.RawCommandLineEditor)
			)
			{
				val textField: JTextField? = field.textField

				if (textField != null && textField is ComponentWithEmptyText)
				{
					val emptyText = textField.emptyText

					/**
					 * @FIXME 不知道為什麼要寫成這樣才能成功更新文字
					 */
					if (StringUtil.isEmptyOrSpaces(emptyText?.text))
					{
						if (!StringUtil.isEmptyOrSpaces(options?.emptyText))
						{
							emptyText?.text = options?.emptyText as String
						}
						else if (!StringUtil.isEmptyOrSpaces(label))
						{
							emptyText?.text = Util.stripTitle(label)
						}
					}
				}
			}
		}

		var dialogCaption
			get() = this.field.dialogCaption
			set(value)
			{
				this.field.dialogCaption = value
			}

		val textField
			get() = field.textField

		val emptyText: StatusText?
			get()
			{
				val field = this.field

				if (field.textField != null && field.textField is ComponentWithEmptyText)
				{
					val emptyText = field.textField as ComponentWithEmptyText

					return emptyText?.emptyText
				}

				return null
			}
	}

	open class NodePackageField<Comp : com.intellij.javascript.nodejs.util.NodePackageField>(override val field: Comp, override val label: String) : Field<Comp>
	{
		var selected
			get() = this.field.selected
			set(value)
			{
				this.field.selected = value
			}

		var selectedRef
			get() = this.field.selectedRef
			set(value)
			{
				this.field.selectedRef = value
			}

		override fun toString() = field.toString()
	}

	open class NodeJsInterpreterField<Comp : com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterField>(override val field: Comp, override val label: String) : Field<Comp>
	{
		var interpreter
			get() = this.field.interpreter
			set(value)
			{
				this.field.interpreter = value
			}

		var interpreterRef
			get() = this.field.interpreterRef
			set(value)
			{
				this.field.interpreterRef = value
			}

		override fun toString() = field.toString()
	}

	open class EnvironmentVariablesTextFieldWithBrowseButtonField<Comp : com.intellij.execution.configuration.EnvironmentVariablesTextFieldWithBrowseButton>(override val field: Comp, override val label: String) : Field<Comp>
	{
		//fun isPassParentEnvs() = field.isPassParentEnvs

		val isPassParentEnvs
			get() = field.isPassParentEnvs

		var data
			get() = this.field.data
			set(value)
			{
				this.field.data = value
			}

		var envs
			get() = this.field.envs
			set(value)
			{
				this.field.envs = value
			}

		override fun toString() = field.toString()
	}

	companion object
	{

		fun LazyNodeJsInterpreterField(label: String, project: Project, withRemote: Boolean = false, fieldFactory: com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterField = com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterField(project, withRemote)) = NodeJsInterpreterField(fieldFactory, label)

		fun LazyTextFieldWithBrowseButton(label: String, fieldFactory: com.intellij.openapi.ui.TextFieldWithBrowseButton = TextFieldWithBrowseButton()) = TextField(fieldFactory, label)

		fun LazyTextFieldWithBrowseSingleFolderButton(label: String, fieldFactory: com.intellij.openapi.ui.TextFieldWithBrowseButton) = TextField(fieldFactory, label)

		fun LazyTextFieldWithBrowseSingleFolderButton(label: String, project: Project, browseDialogTitle: String, fieldFactory: com.intellij.openapi.ui.TextFieldWithBrowseButton = Util.createWorkingDirectoryField(project, browseDialogTitle)) = TsForm.TextFieldWithBrowseSingleFolderButton(fieldFactory, label)

		fun LazyTextFieldWithBrowseSingleFolderButton(label: String, project: Project, fieldFactory: com.intellij.openapi.ui.TextFieldWithBrowseButton = Util.createWorkingDirectoryField(project, label)) = TextFieldWithBrowseSingleFolderButton(fieldFactory, label)

		fun LazyRawCommandLineEditor(label: String, fieldFactory: RawCommandLineEditor = com.intellij.ui.RawCommandLineEditor()) = RawCommandLineEditorField(fieldFactory, label)

		fun LazyRawCommandLineEditor(label: String, fieldFactory: RawCommandLineEditor = com.intellij.ui.RawCommandLineEditor(), options: TextField.Options? = null) = RawCommandLineEditorField(fieldFactory, label, options)

		fun LazyNodePackageField(label: String, interpreterField: com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterField, packageName: String, fieldFactory: com.intellij.javascript.nodejs.util.NodePackageField = com.intellij.javascript.nodejs.util.NodePackageField(interpreterField, packageName)) = NodePackageField(fieldFactory, label)

		fun LazyNodePackageField(
			label: String
			, interpreterField: NodeJsInterpreterField<com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterField>
			, packageName: String, fieldFactory: com.intellij.javascript.nodejs.util.NodePackageField
			= com.intellij.javascript.nodejs.util.NodePackageField(
					interpreterField.field
					, packageName
				)
		) = NodePackageField(fieldFactory, label)

		fun LazyNodePackageField(label: String, fieldFactory: com.intellij.javascript.nodejs.util.NodePackageField) = NodePackageField(fieldFactory, label)

		fun LazyEnvironmentVariablesTextFieldWithBrowseButton(label: String, fieldFactory: EnvironmentVariablesTextFieldWithBrowseButton = EnvironmentVariablesTextFieldWithBrowseButton()) = EnvironmentVariablesTextFieldWithBrowseButtonField(fieldFactory, label)

	}

	object Util
	{
		fun <T : com.intellij.openapi.ui.TextFieldWithBrowseButton> installFileCompletionAndBrowseDialog(project: Project, field: T, browseDialogTitle: String, fileChooserDescriptor: com.intellij.openapi.fileChooser.FileChooserDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()): T
		{
			SwingHelper
				.installFileCompletionAndBrowseDialog(project, field, browseDialogTitle,
					fileChooserDescriptor)

			return field
		}

		fun <T : com.intellij.openapi.ui.TextFieldWithBrowseButton> installFileCompletionAndBrowseDialog(project: Project, field: TextField<T>, browseDialogTitle: String, fileChooserDescriptor: com.intellij.openapi.fileChooser.FileChooserDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()) = installFileCompletionAndBrowseDialog(project, field.field, browseDialogTitle, fileChooserDescriptor)

		fun createWorkingDirectoryField(project: Project, browseDialogTitle: String = "Select Path"): com.intellij.openapi.ui.TextFieldWithBrowseButton
		{
			val field = TextFieldWithBrowseButton()

			SwingHelper
				.installFileCompletionAndBrowseDialog(project, field, stripTitle(browseDialogTitle),
					FileChooserDescriptorFactory.createSingleFolderDescriptor())

			return field
		}

		fun stripTitle(title: String): String
		{
			return title
				.replace("&(\\w)".toRegex(), "$1")
				.replace(":\\s*$".toRegex(), "")
		}
	}
}
