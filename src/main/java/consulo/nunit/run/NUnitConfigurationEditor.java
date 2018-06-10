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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import consulo.dotnet.module.extension.DotNetModuleExtension;
import consulo.ui.Component;
import consulo.ui.LabeledComponents;
import consulo.ui.RequiredUIAccess;
import consulo.ui.VerticalLayout;
import consulo.ui.model.ListModel;

/**
 * @author VISTALL
 * @since 28.03.14
 */
public class NUnitConfigurationEditor extends SettingsEditor<NUnitConfiguration>
{
	private final Project myProject;

	private consulo.ui.ComboBox<Module> myModuleComboBox;

	public NUnitConfigurationEditor(Project project)
	{
		myProject = project;
	}

	@Override
	@RequiredUIAccess
	protected void resetEditorFrom(NUnitConfiguration runConfiguration)
	{
		myModuleComboBox.setValue(runConfiguration.getConfigurationModule().getModule());
	}

	@Override
	@RequiredUIAccess
	protected void applyEditorTo(NUnitConfiguration runConfiguration) throws ConfigurationException
	{
		runConfiguration.getConfigurationModule().setModule(myModuleComboBox.getValue());
	}

	@Nullable
	@Override
	@RequiredUIAccess
	protected Component createUIComponent()
	{
		VerticalLayout layout = VerticalLayout.create();

		List<Module> list = new ArrayList<>();
		for(Module module : ModuleManager.getInstance(myProject).getModules())
		{
			if(ModuleUtilCore.getExtension(module, DotNetModuleExtension.class) != null)
			{
				list.add(module);
			}
		}
		myModuleComboBox = consulo.ui.ComboBox.create(ListModel.create(list));
		myModuleComboBox.setRender((itemPresentation, i, module) ->
		{
			itemPresentation.append(module.getName());
			itemPresentation.setIcon(AllIcons.Nodes.Module);
		});
		layout.add(LabeledComponents.left("Module", myModuleComboBox));

		return layout;
	}
}
