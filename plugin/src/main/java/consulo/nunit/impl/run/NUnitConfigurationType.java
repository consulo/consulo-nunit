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

import consulo.annotation.component.ExtensionImpl;
import consulo.execution.configuration.ConfigurationFactory;
import consulo.execution.configuration.ConfigurationTypeBase;
import consulo.execution.configuration.RunConfiguration;
import consulo.execution.configuration.RunConfigurationModule;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.module.ModuleManager;
import consulo.module.extension.ModuleExtensionHelper;
import consulo.nunit.icon.NUnitIconGroup;
import consulo.nunit.localize.NUnitLocalize;
import consulo.nunit.module.extension.NUnitModuleExtension;
import consulo.project.Project;
import consulo.ui.annotation.RequiredUIAccess;

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 28.03.14
 */
@ExtensionImpl
public class NUnitConfigurationType extends ConfigurationTypeBase
{
	@Nonnull
	public static NUnitConfigurationType getInstance()
	{
		return EP_NAME.findExtensionOrFail(NUnitConfigurationType.class);
	}

	public NUnitConfigurationType()
	{
		super("#NUnitConfigurationType", NUnitLocalize.nunitConfigurationName(), NUnitIconGroup.nunit());
		addFactory(new ConfigurationFactory(this)
		{
			@Override
			public RunConfiguration createTemplateConfiguration(Project project)
			{
				return new NUnitConfiguration("Unnamed", new RunConfigurationModule(project), this);
			}

			@Override
			public boolean isApplicable(@Nonnull Project project)
			{
				return ModuleExtensionHelper.getInstance(project).hasModuleExtension(NUnitModuleExtension.class);
			}

			@Override
			@RequiredUIAccess
			public void onNewConfigurationCreated(@Nonnull RunConfiguration configuration)
			{
				NUnitConfiguration dotNetConfiguration = (NUnitConfiguration) configuration;

				for(Module module : ModuleManager.getInstance(configuration.getProject()).getModules())
				{
					NUnitModuleExtension extension = ModuleUtilCore.getExtension(module, NUnitModuleExtension.class);
					if(extension != null)
					{
						dotNetConfiguration.setName(module.getName());
						dotNetConfiguration.setModule(module);
						break;
					}
				}
			}
		});
	}
}
