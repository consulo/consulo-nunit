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

package consulo.nunit.bundle;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.plugins.cl.PluginClassLoader;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import consulo.dotnet.dll.DotNetModuleFileType;
import consulo.dotnet.module.extension.DotNetLibraryOpenCache;
import consulo.internal.dotnet.asm.mbel.AssemblyInfo;
import consulo.nunit.NUnitIcons;
import consulo.nunit.module.extension.MicrosoftNUnitModuleExtension;
import consulo.roots.types.BinariesOrderRootType;
import consulo.roots.types.DocumentationOrderRootType;
import consulo.ui.image.Image;
import consulo.vfs.util.ArchiveVfsUtil;

/**
 * @author VISTALL
 * @since 10.02.14
 */
public class NUnitBundleType extends SdkType
{
	public static SdkType getInstance()
	{
		return EP_NAME.findExtension(NUnitBundleType.class);
	}

	public NUnitBundleType()
	{
		super("NUNIT_BUNDLE");
	}

	@Nonnull
	@Override
	public Collection<String> suggestHomePaths()
	{
		PluginId pluginId = ((PluginClassLoader) MicrosoftNUnitModuleExtension.class.getClassLoader()).getPluginId();
		IdeaPluginDescriptor plugin = PluginManager.getPlugin(pluginId);
		assert plugin != null;

		List<String> paths = new ArrayList<String>();
		File file = new File(plugin.getPath(), "releases");
		if(file.exists())
		{
			for(File child : file.listFiles())
			{
				paths.add(child.getPath());
			}
		}
		return paths;
	}

	@Override
	public boolean canCreatePredefinedSdks()
	{
		return true;
	}

	@Override
	public boolean isValidSdkHome(String s)
	{
		return new File(s, "bin/nunit.exe").exists();
	}

	@Nullable
	@Override
	public String getVersionString(String home)
	{
		File file = new File(home, "bin/nunit.exe");
		if(file.exists())
		{
			DotNetLibraryOpenCache.Record record = null;
			try
			{
				record = DotNetLibraryOpenCache.acquire(file.getPath());
				AssemblyInfo assemblyInfo = record.get().getAssemblyInfo();
				return StringUtil.join(new int[]{
						assemblyInfo.getMajorVersion(),
						assemblyInfo.getMinorVersion(),
						assemblyInfo.getBuildNumber()
				}, ".");
			}
			catch(Exception ignored)
			{
				///
			}
			finally
			{
				if(record != null)
				{
					record.finish();
				}
			}
		}
		return "0.0";
	}

	@Override
	public String suggestSdkName(String s, String sdkHome)
	{
		return "NUnit " + getVersionString(sdkHome);
	}

	@Override
	public boolean isRootTypeApplicable(OrderRootType type)
	{
		return type == BinariesOrderRootType.getInstance() || type == DocumentationOrderRootType.getInstance();
	}

	@Nullable
	@Override
	public Image getIcon()
	{
		return NUnitIcons.NUnit;
	}

	@Override
	public void setupSdkPaths(Sdk sdk)
	{
		SdkModificator sdkModificator = sdk.getSdkModificator();

		VirtualFile homeDirectory = sdk.getHomeDirectory();

		assert homeDirectory != null;

		VirtualFile relativePath = homeDirectory.findFileByRelativePath("bin/framework");

		if(relativePath != null)
		{
			for(VirtualFile virtualFile : relativePath.getChildren())
			{
				if(virtualFile.getFileType() == DotNetModuleFileType.INSTANCE)
				{
					VirtualFile archiveRootForLocalFile = ArchiveVfsUtil.getArchiveRootForLocalFile(virtualFile);
					if(archiveRootForLocalFile != null)
					{
						sdkModificator.addRoot(archiveRootForLocalFile, BinariesOrderRootType.getInstance());
					}
				}
				else if(virtualFile.getFileType() == XmlFileType.INSTANCE)
				{
					sdkModificator.addRoot(virtualFile, DocumentationOrderRootType.getInstance());
				}
			}
		}
		sdkModificator.commitChanges();
	}

	@Nonnull
	@Override
	public String getPresentableName()
	{
		return "NUnit";
	}
}
