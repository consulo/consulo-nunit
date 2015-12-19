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

package org.mustbe.consulo.nunit;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.dotnet.psi.DotNetAttributeUtil;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.run.DotNetTestFramework;
import org.mustbe.consulo.nunit.module.extension.NUnitModuleExtension;
import com.intellij.openapi.module.ModuleUtilCore;

/**
 * @author VISTALL
 * @since 10.02.14
 */
public class NUnitTestFramework implements DotNetTestFramework
{
	@RequiredReadAction
	@Override
	public boolean isTestType(@NotNull DotNetTypeDeclaration element)
	{
		NUnitModuleExtension extension = ModuleUtilCore.getExtension(element, NUnitModuleExtension.class);
		if(extension == null)
		{
			return false;
		}

		return DotNetAttributeUtil.hasAttribute(element, NUnitTypes.TestFixtureAttribute);
	}
}
