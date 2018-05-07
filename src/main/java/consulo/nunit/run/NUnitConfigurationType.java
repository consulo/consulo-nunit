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

import javax.annotation.Nonnull;

import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import consulo.annotations.RequiredDispatchThread;
import consulo.module.extension.ModuleExtensionHelper;
import consulo.nunit.NUnitIcons;
import consulo.nunit.module.extension.NUnitModuleExtension;

/**
 * @author VISTALL
 * @since 28.03.14
 */
public class NUnitConfigurationType extends ConfigurationTypeBase
{
	@Nonnull
	public static NUnitConfigurationType getInstance()
	{
		return CONFIGURATION_TYPE_EP.findExtension(NUnitConfigurationType.class);
	}

	public NUnitConfigurationType()
	{
		super("#NUnitConfigurationType", "NUnit", "", NUnitIcons.NUnit);
		addFactory(new ConfigurationFactoryEx(this)
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
			@RequiredDispatchThread
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
