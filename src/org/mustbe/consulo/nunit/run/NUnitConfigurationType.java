package org.mustbe.consulo.nunit.run;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.nunit.NUnitIcons;
import org.mustbe.consulo.nunit.module.extension.NUnitModuleExtension;
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

/**
 * @author VISTALL
 * @since 28.03.14
 */
public class NUnitConfigurationType extends ConfigurationTypeBase
{
	@NotNull
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
			public boolean isApplicable(@NotNull Project project)
			{
				return ModuleExtensionHelper.getInstance(project).hasModuleExtension(NUnitModuleExtension.class);
			}

			@Override
			@RequiredDispatchThread
			public void onNewConfigurationCreated(@NotNull RunConfiguration configuration)
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
