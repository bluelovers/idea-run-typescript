package io.plugin.tsnode.execution

import com.intellij.json.JsonFileType
import com.intellij.lang.javascript.library.JSLibraryUtil
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.ProjectScope
import com.intellij.ui.RawCommandLineEditor
import com.intellij.ui.components.fields.ExpandableTextField
import com.intellij.util.ui.ComponentWithEmptyText
import com.intellij.util.ui.SwingHelper
import io.plugin.base.runner._ConfigurationEditor
import io.plugin.tsnode.lib.TsForm
import io.plugin.tsnode.lib.TsLog
import javax.swing.JComponent
import javax.swing.JPanel

class TsConfigurationEditor(runConfig: TsRunConfiguration, project: Project) : _ConfigurationEditor<TsRunConfiguration>(runConfig, project)
{
	val LOG = TsLog(javaClass)

	val nodeJsInterpreterField = TsForm.LazyNodeJsInterpreterField("Node &interpreter:", project)

	var interpreterRef
		get() = nodeJsInterpreterField.interpreterRef
		set(value)
		{
			nodeJsInterpreterField.interpreterRef = value
		}

	val nodeOptionsField = TsForm.LazyRawCommandLineEditor("Node &options:")

	override var interpreterOptions
		get() = nodeOptionsField.text
		set(value)
		{
			nodeOptionsField.text = value
		}

	val tsnodePackageField = TsForm.LazyNodePackageField("&TypeScript Node package:", nodeJsInterpreterField, "ts-node")

	val typescriptOptionsField = TsForm.LazyRawCommandLineEditor("E&xtra ts-node options:")

	var extraTypeScriptOptions
		get() = typescriptOptionsField.text
		set(value)
		{
			typescriptOptionsField.text = value
		}

	//private var typescriptOptionsField = createTypeScriptOptionsField()

	val typescriptConfigFileField = TsForm.LazyTextFieldWithBrowseSingleFileButton("ts&config file:", project)

	var typescriptConfigFile
		get() = typescriptConfigFileField.text
		set(value)
		{
			typescriptConfigFileField.text = value
		}


	//private var typescriptConfigFileField = createTypeScriptConfigFileField()

	val typescriptFileField = TsForm.LazyTextFieldWithBrowseSingleFileButton("TypeScript &file:", project)
	val typescriptFileOptionsField = TsForm.LazyRawCommandLineEditor("&Application parameters:")

	override var scriptName
		get() = typescriptFileField.text
		set(value)
		{
			typescriptFileField.text = value
		}

	override var scriptParameters
		get() = typescriptFileOptionsField.text
		set(value)
		{
			typescriptFileOptionsField.text = value
		}

	//private var typescriptFileField = createTypeScriptFileField()
	//private var typescriptFileOptionsField = createTypeScriptFileOptionsField()

	override val form: JPanel

	init
	{
		LOG.info("[init] $this")

		//nodeOptionsField.dialogCaption = "Node Options"

		form = TsForm.TsFormBuilder()
			.setAlignLabelOnRight(false)

			.addLabeledComponent(nodeJsInterpreterField)
			.addLabeledComponent(nodeOptionsField)

			.addLabeledComponent(workingDirectoryField)

			.addLabeledComponent(tsnodePackageField)

			.addLabeledComponent(typescriptConfigFileField)
			.addLabeledComponent(typescriptOptionsField)

			.addLabeledComponent(typescriptFileField)
			.addLabeledComponent(typescriptFileOptionsField)

			.addLabeledComponent(envVars)

			.panel
	}

	private fun createTypeScriptFileField(): TextFieldWithBrowseButton
	{
		val field = TextFieldWithBrowseButton()
		SwingHelper.installFileCompletionAndBrowseDialog(project, field, "TypeScript file",
			FileChooserDescriptorFactory.createSingleFileDescriptor())
		return field
	}

	private fun createTypeScriptFileOptionsField(): RawCommandLineEditor
	{
		val editor = RawCommandLineEditor()
		editor.dialogCaption = "Application parameters"
		val field = editor.textField
		if (field is ExpandableTextField)
		{
			field.putClientProperty("monospaced", false)
		}

		if (field is ComponentWithEmptyText)
		{
			(field as ComponentWithEmptyText).emptyText.text = "CLI options"
		}

		return editor
	}

	private fun createTypeScriptOptionsField(): RawCommandLineEditor
	{
		val editor = RawCommandLineEditor()
		editor.dialogCaption = "Extra ts-node Options"
		val field = editor.textField
		if (field is ExpandableTextField)
		{
			field.putClientProperty("monospaced", false)
		}

		if (field is ComponentWithEmptyText)
		{
			(field as ComponentWithEmptyText).emptyText.text = "CLI options, e.g. --transpileOnly --skip-project"
		}

		return editor
	}

	private fun createTypeScriptConfigFileField(): com.intellij.openapi.ui.TextFieldWithBrowseButton
	{
		//val fullField = TextFieldWithHistoryWithBrowseButton()
		val fullField = com.intellij.openapi.ui.TextFieldWithBrowseButton()

//		val innerField = fullField.childComponent
//		innerField.setHistorySize(-1)
//		innerField.setMinimumAndPreferredWidth(0)
//
//		SwingHelper.addHistoryOnExpansion(innerField) {
//			innerField.history = emptyList<String>()
//			listPossibleConfigFilesInProject().map { file ->
//				FileUtil.toSystemDependentName(file.path)
//			}.sorted()
//		}

		SwingHelper.installFileCompletionAndBrowseDialog(
			project,
			fullField,
			"Select override tsconfig file",
			FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor()
		)

		return fullField
	}

	private fun listPossibleConfigFilesInProject(): List<VirtualFile>
	{
		val contentScope = ProjectScope.getContentScope(project)
		val scope = contentScope.intersectWith(GlobalSearchScope.notScope(ProjectScope.getLibrariesScope(project)))
		val jsonFileType = JsonFileType.INSTANCE

		val files = FileTypeIndex.getFiles(jsonFileType, scope)

		return files.filter { it != null && it.isValid && !it.isDirectory && isTypeScriptConfigFile(it.nameSequence) && !JSLibraryUtil.isProbableLibraryFile(it) }
	}

	private fun isTypeScriptConfigFile(filename: CharSequence): Boolean
	{
		return filename.startsWith("tsconfig", true)
	}

	override fun createEditor(): JComponent
	{
		LOG.info("[createEditor] $form")

		return form
	}

	override fun applyEditorTo(config: TsRunConfiguration)
	{
		LOG.info("[applyEditorTo] $this $config")

		val runSettings = config.runSettings

		config.runSettings = config.runSettings.copy(
			interpreterRef = nodeJsInterpreterField.interpreterRef,
			interpreterOptions = nodeOptionsField.text,
			workingDirectory = workingDirectoryField.text,
			envData = envVars.data,

			scriptName = typescriptFileField.text,
			programParameters = this.scriptParameters,

			typescriptConfigFile = typescriptConfigFileField.text,
			extraTypeScriptOptions = typescriptOptionsField.text)


		//config.envs2.clear()
		//config.envs2.putAll(envVars.envs)

		config.envs2 = envVars.envs.toMutableMap()

		config.setTypeScriptPackage(tsnodePackageField.selected)

		LOG.info("config.runSettings=${config.runSettings}")
	}

	override fun resetEditorFrom(config: TsRunConfiguration)
	{
		LOG.info("[resetEditorFrom] $this $config")

		val runSettings = config.runSettings
		nodeJsInterpreterField.interpreterRef = runSettings.interpreterRef
		nodeOptionsField.text = runSettings.interpreterOptions
		workingDirectoryField.text = FileUtil.toSystemDependentName(runSettings.workingDirectory)


		//envVars.data = runSettings.envData
		envVars.envs = config.envs2

		tsnodePackageField.selected = config.selectedTsNodePackage()
		typescriptConfigFileField.text = runSettings.typescriptConfigFile
		typescriptOptionsField.text = runSettings.extraTypeScriptOptions

		typescriptFileField.text = runSettings.scriptName
		typescriptFileOptionsField.text = runSettings.programParameters

	}

}
