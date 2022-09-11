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

package consulo.nunit.microsoft.module.extension;

import consulo.container.plugin.PluginManager;
import consulo.content.bundle.Sdk;
import consulo.content.bundle.SdkType;
import consulo.dotnet.sdk.DotNetSdkType;
import consulo.execution.executor.Executor;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.content.layer.extension.ModuleExtensionWithSdkBase;
import consulo.nunit.bundle.NUnitBundleType;
import consulo.nunit.module.extension.NUnitModuleExtension;
import consulo.process.ExecutionException;
import consulo.process.cmd.GeneralCommandLine;

import javax.annotation.Nonnull;
import java.io.File;

/**
 * @author VISTALL
 * @since 10.02.14
 */
public class MicrosoftNUnitModuleExtension extends ModuleExtensionWithSdkBase<MicrosoftNUnitModuleExtension> implements NUnitModuleExtension<MicrosoftNUnitModuleExtension>
{
	public MicrosoftNUnitModuleExtension(@Nonnull String id, @Nonnull ModuleRootLayer moduleRootLayer)
	{
		super(id, moduleRootLayer);
	}

	@Nonnull
	@Override
	public Class<? extends SdkType> getSdkTypeClass()
	{
		return NUnitBundleType.class;
	}

	@Nonnull
	@Override
	public GeneralCommandLine createCommandLine(@Nonnull Executor executor, @Nonnull Sdk dotNetSdk, @Nonnull Sdk nunitSdk) throws ExecutionException
	{
		DotNetSdkType dotNetSdkType = (DotNetSdkType) dotNetSdk.getSdkType();

		GeneralCommandLine commandLine = new GeneralCommandLine();
		commandLine.setExePath(dotNetSdkType.getLoaderFile(dotNetSdk).getAbsolutePath());

		commandLine.addParameter(new File(PluginManager.getPluginPath(MicrosoftNUnitModuleExtension.class), "nunit-ext.dll").getAbsolutePath());
		commandLine.addParameter(nunitSdk.getHomePath() + "/bin/lib");

		return commandLine;
	}
}
