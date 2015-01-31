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

package org.mustbe.consulo.nunit.bundle;

import java.io.File;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.dll.DotNetModuleFileType;
import org.mustbe.consulo.dotnet.module.extension.DotNetLibraryOpenCache;
import org.mustbe.consulo.nunit.NUnitIcons;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.types.BinariesOrderRootType;
import com.intellij.openapi.roots.types.DocumentationOrderRootType;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.util.ArchiveVfsUtil;
import edu.arizona.cs.mbel.mbel.AssemblyInfo;
import edu.arizona.cs.mbel.mbel.ModuleParser;

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

	@Nullable
	@Override
	public String suggestHomePath()
	{
		return null;
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
				ModuleParser parser = DotNetLibraryOpenCache.acquireWithNext(file.getPath());
				AssemblyInfo assemblyInfo = parser.getAssemblyInfo();
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
	public Icon getIcon()
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

	@NotNull
	@Override
	public String getPresentableName()
	{
		return "NUnit";
	}
}
