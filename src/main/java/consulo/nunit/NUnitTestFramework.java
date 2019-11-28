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

package consulo.nunit;

import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.psi.PsiElement;
import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.DotNetAttributeUtil;
import consulo.dotnet.psi.DotNetLikeMethodDeclaration;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.run.DotNetTestFramework;
import consulo.nunit.module.extension.NUnitSimpleModuleExtension;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 10.02.14
 */
public class NUnitTestFramework extends DotNetTestFramework
{
	@RequiredReadAction
	@Override
	public boolean isTestType(@Nonnull DotNetTypeDeclaration element)
	{
		if(!checkExtension(element))
		{
			return false;
		}
		return DotNetAttributeUtil.hasAttribute(element, NUnitTypes.TestFixtureAttribute) || super.isTestType(element);
	}

	@RequiredReadAction
	@Override
	public boolean isTestMethod(@Nonnull DotNetLikeMethodDeclaration element)
	{
		return DotNetAttributeUtil.hasAttribute(element, NUnitTypes.TestAttribute);
	}

	@RequiredReadAction
	private boolean checkExtension(@Nonnull PsiElement e)
	{
		return ModuleUtilCore.getExtension(e, NUnitSimpleModuleExtension.class) != null;
	}
}
