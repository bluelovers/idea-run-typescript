package io.plugin.tsnode.execution

import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterField
import com.intellij.javascript.nodejs.util.NodePackageField
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
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton
import com.intellij.ui.components.fields.ExpandableTextField
import com.intellij.util.ui.ComponentWithEmptyText
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.SwingHelper
import io.plugin.base.runner._ConfigurationEditor
import io.plugin.tsnode.lib.TsLog
import javax.swing.JComponent
import javax.swing.JPanel

class TsConfigurationEditor(runConfig: TsRunConfiguration, project: Project) : _ConfigurationEditor<TsRunConfiguration>(runConfig, project)
{

	private var nodeJsInterpreterField: NodeJsInterpreterField = NodeJsInterpreterField(project, false)
	private var nodeOptionsField: RawCommandLineEditor = RawCommandLineEditor()

	private var tsnodePackageField: NodePackageField = NodePackageField(nodeJsInterpreterField, "ts-node")
	private var typescriptOptionsField = createTypeScriptOptionsField()
	private var typescriptConfigFileField = createTypeScriptConfigFileField()

	private var typescriptFileField = createTypeScriptFileField()
	private var typescriptFileOptionsField = createTypeScriptFileOptionsField()

	override val form: JPanel

	init
	{
		nodeOptionsField.dialogCaption = "Node Options"
		form = FormBuilder()
			.setAlignLabelOnRight(false)
			.addLabeledComponent("Node &interpreter:", nodeJsInterpreterField)
			.addLabeledComponent("Node &options:", nodeOptionsField)
			.addLabeledComponent("&Working directory:", workingDirectoryField)

			.addLabeledComponent("&TypeScript Node package:", tsnodePackageField)
			.addLabeledComponent("ts&config file:", typescriptConfigFileField)
			.addLabeledComponent("E&xtra ts-node options:", typescriptOptionsField)

			.addLabeledComponent("TypeScript &file:", typescriptFileField)
			.addLabeledComponent("&Application parameters:", typescriptFileOptionsField)

			.addLabeledComponent("&Environment variables:", envVars)

			.panel

		TsLog.info("runConfig.suggestedName()" + runConfig.suggestedName())
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

	private fun createTypeScriptConfigFileField(): TextFieldWithHistoryWithBrowseButton
	{
		val fullField = TextFieldWithHistoryWithBrowseButton()
		val innerField = fullField.childComponent
		innerField.setHistorySize(-1)
		innerField.setMinimumAndPreferredWidth(0)

		SwingHelper.addHistoryOnExpansion(innerField) {
			innerField.history = emptyList<String>()
			listPossibleConfigFilesInProject().map { file ->
				FileUtil.toSystemDependentName(file.path)
			}.sorted()
		}

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

	override fun createEditor(): JComponent = form

	override fun applyEditorTo(config: TsRunConfiguration)
	{
		config.runSettings = config.runSettings.copy(
			interpreterRef = nodeJsInterpreterField.interpreterRef,
			interpreterOptions = nodeOptionsField.text,
			workingDirectory = workingDirectoryField.text,
			envData = envVars.data,

			scriptName = typescriptFileField.text,
			programParameters = typescriptFileOptionsField.text,

			typescriptConfigFile = typescriptConfigFileField.text,
			extraTypeScriptOptions = typescriptOptionsField.text)

		config.setTypeScriptPackage(tsnodePackageField.selected)
	}

	override fun resetEditorFrom(config: TsRunConfiguration)
	{
		val runSettings = config.runSettings
		nodeJsInterpreterField.interpreterRef = runSettings.interpreterRef
		nodeOptionsField.text = runSettings.interpreterOptions
		workingDirectoryField.text = FileUtil.toSystemDependentName(runSettings.workingDirectory)
		envVars.data = runSettings.envData
		tsnodePackageField.selected = config.selectedTsNodePackage()
		typescriptConfigFileField.text = runSettings.typescriptConfigFile
		typescriptOptionsField.text = runSettings.extraTypeScriptOptions

		typescriptFileField.text = runSettings.scriptName
		typescriptFileOptionsField.text = runSettings.programParameters

	}

}
