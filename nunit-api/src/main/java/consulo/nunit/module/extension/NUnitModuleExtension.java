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

import consulo.content.bundle.Sdk;
import consulo.execution.executor.Executor;
import consulo.module.extension.ModuleExtensionWithSdk;
import consulo.process.ExecutionException;
import consulo.process.cmd.GeneralCommandLine;

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 23.04.14
 */
public interface NUnitModuleExtension<T extends NUnitModuleExtension<T>> extends NUnitSimpleModuleExtension<T>, ModuleExtensionWithSdk<T>
{
	@Nonnull
	GeneralCommandLine createCommandLine(@Nonnull Executor executor, @Nonnull Sdk dotNetSdk, @Nonnull Sdk nunitSdk) throws ExecutionException;
}
