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

import consulo.annotation.component.ExtensionImpl;
import consulo.container.plugin.PluginManager;
import consulo.content.OrderRootType;
import consulo.content.base.BinariesOrderRootType;
import consulo.content.base.DocumentationOrderRootType;
import consulo.content.bundle.Sdk;
import consulo.content.bundle.SdkModificator;
import consulo.content.bundle.SdkType;
import consulo.dotnet.dll.DotNetModuleFileType;
import consulo.internal.dotnet.asm.mbel.AssemblyInfo;
import consulo.internal.dotnet.asm.mbel.ModuleParser;
import consulo.nunit.icon.NUnitIconGroup;
import consulo.ui.image.Image;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.archive.ArchiveVfsUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author VISTALL
 * @since 10.02.14
 */
@ExtensionImpl
public class NUnitBundleType extends SdkType
{
	@Nonnull
	public static SdkType getInstance()
	{
		return EP_NAME.findExtensionOrFail(NUnitBundleType.class);
	}

	public NUnitBundleType()
	{
		super("NUNIT_BUNDLE");
	}

	@Nonnull
	@Override
	public Collection<String> suggestHomePaths()
	{
		List<String> paths = new ArrayList<>();
		File file = new File(PluginManager.getPluginPath(NUnitBundleType.class), "releases");
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
			try
			{
				AssemblyInfo assemblyInfo = ModuleParser.parseAssemblyInfo(file);
				return assemblyInfo.getMajorVersion() + "." + assemblyInfo.getMinorVersion() + "." + assemblyInfo.getBuildNumber();
			}
			catch(Exception ignored)
			{
				///
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
		return NUnitIconGroup.nunit();
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
				else if(StringUtil.equalsIgnoreCase(virtualFile.getExtension(), "xml"))
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
