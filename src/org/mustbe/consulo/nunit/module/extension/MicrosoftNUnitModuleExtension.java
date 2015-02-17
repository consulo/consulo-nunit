/*
 * Copyright 2013-2014 must-be.org
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

package org.mustbe.consulo.nunit.module.extension;

import java.io.File;

import org.consulo.module.extension.impl.ModuleExtensionWithSdkImpl;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.sdk.DotNetSdkType;
import org.mustbe.consulo.nunit.bundle.NUnitBundleType;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.plugins.cl.PluginClassLoader;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.ModuleRootLayer;

/**
 * @author VISTALL
 * @since 10.02.14
 */
public class MicrosoftNUnitModuleExtension extends ModuleExtensionWithSdkImpl<MicrosoftNUnitModuleExtension> implements
		NUnitModuleExtension<MicrosoftNUnitModuleExtension>
{
	public MicrosoftNUnitModuleExtension(@NotNull String id, @NotNull ModuleRootLayer moduleRootLayer)
	{
		super(id, moduleRootLayer);
	}

	@NotNull
	@Override
	public Class<? extends SdkType> getSdkTypeClass()
	{
		return NUnitBundleType.class;
	}

	@NotNull
	@Override
	public GeneralCommandLine createCommandLine(@NotNull Executor executor, @NotNull Sdk dotNetSdk, @NotNull Sdk nunitSdk) throws ExecutionException
	{
		DotNetSdkType dotNetSdkType = (DotNetSdkType) dotNetSdk.getSdkType();

		GeneralCommandLine commandLine = new GeneralCommandLine();
		commandLine.setExePath(dotNetSdkType.getLoaderFile(dotNetSdk).getAbsolutePath());

		PluginId pluginId = ((PluginClassLoader) MicrosoftNUnitModuleExtension.class.getClassLoader()).getPluginId();
		IdeaPluginDescriptor plugin = PluginManager.getPlugin(pluginId);
		assert plugin != null;

		commandLine.addParameter(new File(plugin.getPath(), "nunit-ext.dll").getAbsolutePath());
		commandLine.addParameter(nunitSdk.getHomePath() + "/bin/lib");

		return commandLine;
	}
}
