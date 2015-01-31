/*
 * Copyright 2013-2015 must-be.org
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

package org.mustbe.consulo.nunit.bundle;

import java.io.File;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.nunit.module.extension.MicrosoftNUnitModuleExtension;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.plugins.cl.PluginClassLoader;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.projectRoots.BundledSdkProvider;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.projectRoots.impl.SdkImpl;
import com.intellij.util.SmartList;
import com.intellij.util.containers.ContainerUtil;

/**
 * @author VISTALL
 * @since 31.01.15
 */
public class NUnitBundledSdkProvider implements BundledSdkProvider
{
	@NotNull
	@Override
	public Sdk[] createBundledSdks()
	{
		PluginId pluginId = ((PluginClassLoader) MicrosoftNUnitModuleExtension.class.getClassLoader()).getPluginId();
		IdeaPluginDescriptor plugin = PluginManager.getPlugin(pluginId);
		assert plugin != null;

		SdkType sdkType = NUnitBundleType.getInstance();
		File file = new File(plugin.getPath(), "releases");
		if(file.exists())
		{
			List<Sdk> list = new SmartList<Sdk>();
			for(File child : file.listFiles())
			{
				String childPath = child.getPath();
				if(sdkType.isValidSdkHome(childPath))
				{
					SdkImpl sdk = new SdkImpl(sdkType.suggestSdkName(null, childPath) + " (bundled)", sdkType);
					sdk.setHomePath(childPath);
					sdk.setVersionString(sdkType.getVersionString(childPath));
					sdkType.setupSdkPaths(sdk);
					list.add(sdk);
				}
			}
			return ContainerUtil.toArray(list, Sdk.EMPTY_ARRAY);
		}
		return Sdk.EMPTY_ARRAY;
	}
}
