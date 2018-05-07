/*
 * Copyright 2013-2017 consulo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package consulo.nunit.run;

import javax.swing.JComponent;

import javax.annotation.Nonnull;
import com.intellij.application.options.ModuleListCellRenderer;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.util.ui.FormBuilder;
import consulo.annotations.RequiredDispatchThread;
import consulo.dotnet.module.extension.DotNetModuleExtension;

/**
 * @author VISTALL
 * @since 28.03.14
 */
public class NUnitConfigurationEditor extends SettingsEditor<NUnitConfiguration>
{
	private final Project myProject;

	private ComboBox myModuleComboBox;

	public NUnitConfigurationEditor(Project project)
	{
		myProject = project;
	}

	@Override
	protected void resetEditorFrom(NUnitConfiguration runConfiguration)
	{
		myModuleComboBox.setSelectedItem(runConfiguration.getConfigurationModule().getModule());
	}

	@Override
	protected void applyEditorTo(NUnitConfiguration runConfiguration) throws ConfigurationException
	{
		runConfiguration.getConfigurationModule().setModule((Module) myModuleComboBox.getSelectedItem());
	}

	@Nonnull
	@Override
	@RequiredDispatchThread
	protected JComponent createEditor()
	{
		myModuleComboBox = new ComboBox();
		myModuleComboBox.setRenderer(new ModuleListCellRenderer());
		for(Module module : ModuleManager.getInstance(myProject).getModules())
		{
			if(ModuleUtilCore.getExtension(module, DotNetModuleExtension.class) != null)
			{
				myModuleComboBox.addItem(module);
			}
		}

		FormBuilder formBuilder = FormBuilder.createFormBuilder();
		formBuilder.addLabeledComponent("Module", myModuleComboBox);

		return formBuilder.getPanel();
	}
}
