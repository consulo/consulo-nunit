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

import consulo.annotation.component.ExtensionImpl;
import consulo.execution.action.ConfigurationContext;
import consulo.execution.action.RunConfigurationProducer;
import consulo.language.psi.PsiElement;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.nunit.module.extension.NUnitModuleExtension;
import consulo.util.lang.ref.Ref;

/**
 * @author VISTALL
 * @since 26.07.14
 */
@ExtensionImpl
public class NUnitConfigurationProducer extends RunConfigurationProducer<NUnitConfiguration>
{
	public NUnitConfigurationProducer()
	{
		super(NUnitConfigurationType.getInstance().getConfigurationFactories()[0]);
	}

	@Override
	protected boolean setupConfigurationFromContext(NUnitConfiguration configuration, ConfigurationContext context, Ref<PsiElement> sourceElement)
	{
		PsiElement psiLocation = context.getPsiLocation();
		if(psiLocation == null)
		{
			return false;
		}

		Module moduleForRun = getModuleForRun(context);
		if(moduleForRun != null)
		{
			configuration.setName(moduleForRun.getName());
			configuration.setModule(moduleForRun);
			return true;
		}
		return false;
	}

	@Override
	public boolean isConfigurationFromContext(NUnitConfiguration configuration, ConfigurationContext context)
	{
		Module moduleForRun = getModuleForRun(context);
		if(moduleForRun == null)
		{
			return false;
		}

		return configuration.getConfigurationModule().getModule() == moduleForRun;
	}

	private static Module getModuleForRun(ConfigurationContext configurationContext)
	{
		Module module = configurationContext.getModule();
		if(module == null)
		{
			return null;
		}
		NUnitModuleExtension extension = ModuleUtilCore.getExtension(module, NUnitModuleExtension.class);
		if(extension != null)
		{
			return module;
		}
		return null;
	}
}
