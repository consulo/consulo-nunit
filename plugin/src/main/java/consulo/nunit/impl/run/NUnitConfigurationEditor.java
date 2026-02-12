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
package consulo.nunit.impl.run;

import consulo.configurable.ConfigurationException;
import consulo.dotnet.module.extension.DotNetModuleExtension;
import consulo.execution.configuration.ui.SettingsEditor;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.module.ModuleManager;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.project.Project;
import consulo.ui.ComboBox;
import consulo.ui.Component;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.layout.VerticalLayout;
import consulo.ui.model.ListModel;
import consulo.ui.util.LabeledComponents;

import jakarta.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @since 2014-03-28
 */
public class NUnitConfigurationEditor extends SettingsEditor<NUnitConfiguration> {
    private final Project myProject;

    private consulo.ui.ComboBox<Module> myModuleComboBox;

    public NUnitConfigurationEditor(Project project) {
        myProject = project;
    }

    @Override
    @RequiredUIAccess
    protected void resetEditorFrom(NUnitConfiguration runConfiguration) {
        myModuleComboBox.setValue(runConfiguration.getConfigurationModule().getModule());
    }

    @Override
    @RequiredUIAccess
    protected void applyEditorTo(NUnitConfiguration runConfiguration) throws ConfigurationException {
        runConfiguration.getConfigurationModule().setModule(myModuleComboBox.getValue());
    }

    @Nullable
    @Override
    @RequiredUIAccess
    protected Component createUIComponent() {
        VerticalLayout layout = VerticalLayout.create();

        List<Module> list = new ArrayList<>();
        for (Module module : ModuleManager.getInstance(myProject).getModules()) {
            if (ModuleUtilCore.getExtension(module, DotNetModuleExtension.class) != null) {
                list.add(module);
            }
        }
        myModuleComboBox = ComboBox.create(ListModel.of(list));
        myModuleComboBox.setRenderer((itemPresentation, i, module) -> {
            if (module != null) {
                itemPresentation.append(module.getName());
                itemPresentation.withIcon(PlatformIconGroup.nodesModule());
            }
        });
        layout.add(LabeledComponents.left("Module", myModuleComboBox));

        return layout;
    }
}
