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

package consulo.nunit.module.extension;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkType;
import consulo.dotnet.execution.DebugConnectionInfo;
import consulo.dotnet.run.DotNetRunKeys;
import consulo.dotnet.sdk.DotNetSdkType;
import consulo.module.extension.impl.ModuleExtensionWithSdkImpl;
import consulo.mono.dotnet.module.extension.MonoDotNetModuleExtension;
import consulo.nunit.bundle.NUnitBundleType;
import consulo.roots.ModuleRootLayer;

import javax.annotation.Nonnull;
import java.io.File;

/**
 * @author VISTALL
 * @since 23.04.14
 */
public class MonoNUnitModuleExtension extends ModuleExtensionWithSdkImpl<MonoNUnitModuleExtension> implements NUnitModuleExtension<MonoNUnitModuleExtension>
{
	public MonoNUnitModuleExtension(@Nonnull String id, @Nonnull ModuleRootLayer rootModel)
	{
		super(id, rootModel);
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

		DebugConnectionInfo debugConnectionInfo = null;
		if(executor instanceof DefaultDebugExecutor)
		{
			debugConnectionInfo = new DebugConnectionInfo("127.0.0.1", -1, true);
		}

		GeneralCommandLine commandLine = MonoDotNetModuleExtension.createDefaultCommandLineImpl(dotNetSdk, debugConnectionInfo, dotNetSdkType.getLoaderFile(dotNetSdk).getAbsolutePath());
		commandLine.putUserData(DotNetRunKeys.DEBUG_CONNECTION_INFO_KEY, debugConnectionInfo);

		commandLine.addParameter(new File(PluginManager.getPluginPath(MicrosoftNUnitModuleExtension.class), "mono-nunit-ext.dll").getAbsolutePath());
		commandLine.addParameter(nunitSdk.getHomePath() + "/bin/lib");

		return commandLine;
	}
}
