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
import io.plugin.tsnode.runner._ConfigurationEditor
import io.plugin.tsnode.lib.TsForm
import io.plugin.tsnode.lib.TsLog
import javax.swing.JComponent
import javax.swing.JPanel

class TsConfigurationEditor(runConfig: TsRunConfiguration, project: Project) : _ConfigurationEditor<TsRunConfiguration>(runConfig, project)
{
	val LOG = TsLog(javaClass)

	val interpreterField = TsForm.LazyNodeJsInterpreterField("Node &interpreter:", project)

	var interpreterRef
		get() = interpreterField.interpreterRef
		set(value)
		{
			interpreterField.interpreterRef = value
		}

	val interpreterOptionsField = TsForm.LazyRawCommandLineEditor("Node &options:")

	override var interpreterOptions
		get() = interpreterOptionsField.text
		set(value)
		{
			interpreterOptionsField.text = value
		}

	//val tsnodePackageField = TsForm.LazyNodePackageField("&TypeScript Node package:", interpreterField, "ts-node")

	val tsnodePackageField = TsForm.LazyNodePackageField("&TypeScript Node package:", interpreterField, listOf("ts-node", "esm-ts-node"))


	val extraTypeScriptOptionsField = TsForm.LazyRawCommandLineEditor("E&xtra ts-node options:")

	var extraTypeScriptOptions
		get() = extraTypeScriptOptionsField.text
		set(value)
		{
			extraTypeScriptOptionsField.text = value
		}

	//private var extraTypeScriptOptionsField = createTypeScriptOptionsField()

	val tsconfigFileField = TsForm.LazyTextFieldWithBrowseSingleFileButton("ts&config file:", project)

	var tsconfigFile
		get() = tsconfigFileField.text
		set(value)
		{
			tsconfigFileField.text = value
		}


	//private var tsconfigFileField = createTsconfigFileField()

	val scriptNameField = TsForm.LazyTextFieldWithBrowseSingleFileButton("TypeScript &file:", project)
	val programParametersField = TsForm.LazyRawCommandLineEditor("&Application parameters:")

	override var scriptName
		get() = scriptNameField.text
		set(value)
		{
			scriptNameField.text = value
		}

	override var programParameters
		get() = programParametersField.text
		set(value)
		{
			programParametersField.text = value
		}

	//private var scriptNameField = createTypeScriptFileField()
	//private var programParametersField = createTypeScriptFileOptionsField()

	override val form: JPanel

	init
	{
		//LOG.info("[init] $this")

		//interpreterOptionsField.dialogCaption = "Node Options"

		form = TsForm.TsFormBuilder()
			.setAlignLabelOnRight(false)

			.addLabeledComponent(interpreterField)
			.addLabeledComponent(interpreterOptionsField)

			.addLabeledComponent(workingDirectoryField)

			.addLabeledComponent(tsnodePackageField)

			.addLabeledComponent(tsconfigFileField)
			.addLabeledComponent(extraTypeScriptOptionsField)

			.addLabeledComponent(scriptNameField)
			.addLabeledComponent(programParametersField)

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
		//editor.dialogCaption = "Extra ts-node Options"

		editor.toolTipText = "Extra ts-node Options"

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

	private fun createTsconfigFileField(): com.intellij.openapi.ui.TextFieldWithBrowseButton
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

		return files.filter { it != null && it.isValid && !it.isDirectory && isTsconfigFile(it.nameSequence) && !JSLibraryUtil.isProbableLibraryFile(it) }
	}

	private fun isTsconfigFile(filename: CharSequence): Boolean
	{
		return filename.startsWith("tsconfig", true)
	}

	override fun createEditor(): JComponent
	{
		//LOG.info("[createEditor] $form")

		return form
	}

	override fun applyEditorTo(config: TsRunConfiguration)
	{
		//LOG.info("[applyEditorTo] $this $config")

		val runSettings = config.runSettings

		config.runSettings = config.runSettings.copy(
			interpreterRef = interpreterField.interpreterRef,
			interpreterOptions = interpreterOptionsField.text,
			workingDirectory = workingDirectoryField.text,
			envData = envVars.data,

			scriptName = scriptNameField.text,
			programParameters = this.programParameters,

			tsnodePackage = tsnodePackageField.selected,

			tsconfigFile = tsconfigFileField.text,
			extraTypeScriptOptions = extraTypeScriptOptionsField.text)


		//config.envs2.clear()
		//config.envs2.putAll(envVars.envs)

		config.envs2 = envVars.envs.toMutableMap()

		config.setTypeScriptPackage(tsnodePackageField.selected)

		//LOG.info("config.runSettings=${config.runSettings}")
	}

	override fun resetEditorFrom(config: TsRunConfiguration)
	{
		//LOG.info("[resetEditorFrom] $this $config")

		val runSettings = config.runSettings
		interpreterField.interpreterRef = runSettings.interpreterRef
		interpreterOptionsField.text = runSettings.interpreterOptions
		workingDirectoryField.text = FileUtil.toSystemDependentName(runSettings.workingDirectory)


		//envVars.data = runSettings.envData
		envVars.envs = config.envs2

		tsnodePackageField.selected = config.selectedTsNodePackage()!!
		tsconfigFileField.text = runSettings.tsconfigFile
		extraTypeScriptOptionsField.text = runSettings.extraTypeScriptOptions

		scriptNameField.text = runSettings.scriptName
		programParametersField.text = runSettings.programParameters

	}

}
