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

import javax.swing.JComponent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.util.ui.JBUI;
import consulo.annotations.RequiredDispatchThread;
import consulo.extension.ui.ModuleExtensionSdkBoxBuilder;
import consulo.module.extension.MutableModuleExtensionWithSdk;
import consulo.module.extension.MutableModuleInheritableNamedPointer;
import consulo.roots.ModuleRootLayer;

/**
 * @author VISTALL
 * @since 23.04.14
 */
public class MonoNUnitMutableModuleExtension extends MonoNUnitModuleExtension implements MutableModuleExtensionWithSdk<MonoNUnitModuleExtension>
{
	public MonoNUnitMutableModuleExtension(@NotNull String id, @NotNull ModuleRootLayer rootModel)
	{
		super(id, rootModel);
	}

	@NotNull
	@Override
	public MutableModuleInheritableNamedPointer<Sdk> getInheritableSdk()
	{
		return (MutableModuleInheritableNamedPointer<Sdk>) super.getInheritableSdk();
	}

	@Override
	public void setEnabled(boolean b)
	{
		myIsEnabled = b;
	}

	@RequiredDispatchThread
	@Nullable
	@Override
	public JComponent createConfigurablePanel(@NotNull Runnable runnable)
	{
		return JBUI.Panels.simplePanel().addToTop(ModuleExtensionSdkBoxBuilder.createAndDefine(this, runnable).build());
	}

	@Override
	public boolean isModified(@NotNull MonoNUnitModuleExtension extension)
	{
		return isModifiedImpl(extension);
	}
}
